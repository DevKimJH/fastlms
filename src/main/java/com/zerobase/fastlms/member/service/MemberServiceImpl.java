package com.zerobase.fastlms.member.service;


import com.zerobase.fastlms.components.MailComponents;
import com.zerobase.fastlms.member.entity.Member;
import com.zerobase.fastlms.member.model.MemberInput;
import com.zerobase.fastlms.member.repository.MemberRepository;
import com.zerobase.fastlms.member.service.impl.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MailComponents mailComponents;

    /**
     * 회원 가입
     */
    @Override
    public boolean register(MemberInput parameter) {


        Optional<Member> optionalMember = memberRepository.findById(parameter.getUserId());

        if(optionalMember.isPresent()){
            // 현재 userId에 해당하는 데이터(Column) 존재
            return false;
        }

        String uuid = UUID.randomUUID().toString();

        
        /*
        Member member = new Member();
        member.setUserId(parameter.getUserId());
        member.setUserName(parameter.getUserName());
        member.setPhone(parameter.getPhone());
        member.setPassword(parameter.getPassword());
        member.setRegDt(LocalDateTime.now());

        member.setEmailAuthYn(false);
        member.setEmailAuthKey(uuid);
         */
        

        // 위와 같은 set 방식을 @build라는 어노테이션을 통해 
        // 아래와 같이 구현 가능
        Member member = Member.builder()
                .userId(parameter.getUserId())
                .userName(parameter.getUserName())
                .phone(parameter.getPhone())
                .password(parameter.getPassword())
                .regDt(LocalDateTime.now())
                .emailAuthYn(false)
                .emailAuthKey(uuid)
                .build();

        

        memberRepository.save(member);

        // 후에 과제로 수정해야 할 부분?
        String email = parameter.getUserId();
        String subject = "fastlms 사이트 가입을 축하드립니다. ";
        String text = "<p>fastlms 사이트 가입을 축하드립니다. </p><p>아래 링크를 클릭하셔서 가입을 완료하세요.</p>"
                + "<div><a target='_blank' href='http://localhost:8080/member/email-auth?id=" + uuid + "'> 가입 완료 </div>";

        mailComponents.sendMail(email, subject, text);

        return true;
    }

    @Override
    public boolean emailAuth(String uuid) {

        // 해당 uuid를 가진 member가 실제로 존재하는지 확인
        Optional<Member> optionalMember = memberRepository.findByEmailAuthKey(uuid);

        // 존재하지 않으면 false 리턴
        if(!optionalMember.isPresent()){
            return false;
        }

        // uuid로 가져온 member row 정보를 member에 저장
        Member member = optionalMember.get();

        // 이메일 인증 여부를 Y로 변경
        member.setEmailAuthYn(true);
        member.setEmailAuthDt(LocalDateTime.now());
        memberRepository.save(member);

        return true;
    }
}
