package com.zerobase.fastlms.member.service.impl;


import com.zerobase.fastlms.admin.dto.MemberDto;
import com.zerobase.fastlms.admin.mapper.MemberMapper;
import com.zerobase.fastlms.admin.model.MemberParam;
import com.zerobase.fastlms.components.MailComponents;
import com.zerobase.fastlms.course.model.ServiceResult;
import com.zerobase.fastlms.member.entity.Member;
import com.zerobase.fastlms.member.entity.MemberCode;
import com.zerobase.fastlms.member.exception.MemberNotEmailAuthException;
import com.zerobase.fastlms.member.exception.MemberStopUserException;
import com.zerobase.fastlms.member.model.MemberInput;
import com.zerobase.fastlms.member.model.ResetPasswordInput;
import com.zerobase.fastlms.member.repository.MemberRepository;
import com.zerobase.fastlms.member.service.MemberService;
import com.zerobase.fastlms.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MailComponents mailComponents;

    private final MemberMapper memberMapper;

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

        // 스프링 시큐리티
        String encPassword = BCrypt.hashpw(parameter.getPassword(), BCrypt.gensalt());


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
                .password(encPassword)
                .regDt(LocalDateTime.now())
                .emailAuthYn(false)
                .emailAuthKey(uuid)
                .userStatus(Member.MEMBER_STATUS_REQ)
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

        // 이미 활성화된 계정일 경우
        if(member.isEmailAuthYn()){
            return false;
        }


        // 이메일 인증 여부를 Y로 변경
        member.setEmailAuthYn(true);
        member.setUserStatus(MemberCode.MEMBER_STATUS_ING);
        member.setEmailAuthDt(LocalDateTime.now());
        memberRepository.save(member);

        return true;
    }

    @Override
    public boolean sendResetPassword(ResetPasswordInput parameter) {

        // userId(이메일)과 이름으로 DB에서 일치하는 row값 찾기
        Optional<Member> optionalMember = memberRepository.findByUserIdAndUserName(parameter.getUserId(), parameter.getUserName());

        if(!optionalMember.isPresent()){
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }


        Member member = optionalMember.get();

        String uuid = UUID.randomUUID().toString();

        // 비밀번호 초기화를 위한 key 역할을 위한 새로운 uuid 설정
        member.setResetPasswordKey(uuid);

        // 비밀번호 초기화 가능한 시간 설정
        member.setResetPasswordLimitDt(LocalDateTime.now().plusDays(1));

        memberRepository.save(member);

        String email = parameter.getUserId();
        String subject = "[fastlms] 비밀번호 초기화 메일입니다. ";
        String text = "<p>fastlms 비밀번호 초기화 메일 입니다. </p><p>아래 링크를 클릭하셔서 비밀번호를 초기화 해주세요.</p>"
                + "<div><a target='_blank' href='http://localhost:8080/member/reset/password?id=" + uuid + "'> 비밀번호 초기화 링크 </div>";

        mailComponents.sendMail(email, subject, text);

        return true;
    }

    @Override
    public boolean resetPassword(String uuid, String password) {


        // 현재 시스템에서 username 은 email 의미
        Optional<Member> optionalMember = memberRepository.findByResetPasswordKey(uuid);

        if(!optionalMember.isPresent()){
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        // 초기화 날짜가 유효한지 체크
        if(member.getResetPasswordLimitDt() == null){
            throw new RuntimeException(" 유효한 날짜가 아닙니다. ");
        }

        if(member.getResetPasswordLimitDt().isBefore(LocalDateTime.now())){
            throw new RuntimeException(" 유효한 날짜가 아닙니다. ");
        }


        String encPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        member.setPassword(encPassword);
        member.setResetPasswordKey("");
        member.setResetPasswordLimitDt(null);
        memberRepository.save(member);

        return true;
    }

    @Override
    public boolean checkResetPassword(String uuid) {
        // 현재 시스템에서 username 은 email 의미
        Optional<Member> optionalMember = memberRepository.findByResetPasswordKey(uuid);

        if(!optionalMember.isPresent()){
            return false;
        }

        Member member = optionalMember.get();

        // 초기화 날짜가 유효한지 체크
        if(member.getResetPasswordLimitDt() == null){
            throw new RuntimeException(" 유효한 날짜가 아닙니다. ");
        }

        if(member.getResetPasswordLimitDt().isBefore(LocalDateTime.now())){
            throw new RuntimeException(" 유효한 날짜가 아닙니다. ");
        }
        return true;
    }

    @Override
    public List<MemberDto> list(MemberParam parameter) {

        //MemberDto parameter = new MemberDto();

        long totalCount = memberMapper.selectListCount(parameter);

        List<MemberDto> list = memberMapper.selectList(parameter);

        // list가 empty인 경우
        if(!CollectionUtils.isEmpty(list)){

            int i = 0;

            for(MemberDto x : list){
                x.setTotalCount(totalCount);
                x.setSeq(totalCount - parameter.getPageStart() - i);
                i++;
            }
        }


        return list;

        //return memberRepository.findAll();

    }

    @Override
    public MemberDto detail(String userId) {

        Optional<Member> optionalMember = memberRepository.findById(userId);
        if(!optionalMember.isPresent()){
            return null;
        }


        Member member = optionalMember.get();

        /*
        MemberDto memberDto = new MemberDto();
        memberDto.setUserId(member.getUserId());
        memberDto.setUserName(member.getUserName());
        memberDto.setPhone(member.getPhone());
        memberDto.setRegDt(member.getRegDt());
        memberDto.setEmailAuthYn(member.isEmailAuthYn());
        memberDto.setEmailAuthDt(member.getEmailAuthDt());
        memberDto.setEmailAuthKey(member.getEmailAuthKey());
        memberDto.setResetPasswordKey(member.getResetPasswordKey());
        memberDto.setResetPasswordLimitDt(member.getResetPasswordLimitDt());
        memberDto.setAdminYn(member.isAdminYn());
         */

        return MemberDto.of(member);
    }

    @Override
    public boolean updateStatus(String userId, String userStatus) {

        // 현재 시스템에서 username 은 email 의미
        Optional<Member> optionalMember = memberRepository.findById(userId);

        if(!optionalMember.isPresent()){
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        member.setUserStatus(userStatus);
        memberRepository.save(member);

        return true;
    }

    @Override
    public boolean updatePassword(String userId, String password) {


        System.out.println("####################");
        System.out.println(userId);
        System.out.println("####################");

        // 현재 시스템에서 username 은 email 의미
        Optional<Member> optionalMember = memberRepository.findById(userId);

        if(!optionalMember.isPresent()){
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        String encPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        member.setPassword(encPassword);
        memberRepository.save(member);

        return true;
    }

    @Override
    public ServiceResult updateMember(MemberInput parameter) {


        String userId = parameter.getUserId();

        // 현재 시스템에서 username 은 email 의미
        Optional<Member> optionalMember = memberRepository.findById(userId);

        if(!optionalMember.isPresent()){
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();
        member.setPhone(parameter.getPhone());
        member.setZipcode(parameter.getZipcode());
        member.setAddr(parameter.getAddr());
        member.setAddrDetail(parameter.getAddrDetail());
        member.setUdtDt(LocalDateTime.now());
        memberRepository.save(member);


        return new ServiceResult(true);
    }

    @Override
    public ServiceResult updateMemberPassword(MemberInput parameter) {

        String userId = parameter.getUserId();

        // 현재 시스템에서 username 은 email 의미
        Optional<Member> optionalMember = memberRepository.findById(userId);

        if(!optionalMember.isPresent()){
            return new ServiceResult(false, "회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        if(!PasswordUtils.equals(parameter.getPassword(), member.getPassword())){
            return new ServiceResult(false, "비밀번호가 일치하지 않습니다.");
        }

        /*
        if(!BCrypt.checkpw(parameter.getPassword(), member.getPassword())){
            return new ServiceResult(false, "비밀번호가 일치하지 않습니다.");
        }
         */

        //String encPassword = BCrypt.hashpw(parameter.getNewPassword(), BCrypt.gensalt());
        String encPassword = PasswordUtils.endPassword(parameter.getNewPassword());
        member.setPassword(encPassword);
        memberRepository.save(member);

        return new ServiceResult(true);
    }

    @Override
    public ServiceResult withdraw(String userId, String password) {

        // 현재 시스템에서 username 은 email 의미
        Optional<Member> optionalMember = memberRepository.findById(userId);

        if(!optionalMember.isPresent()){
            return new ServiceResult(false, "회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        if(!PasswordUtils.equals(password, member.getPassword())){
            return new ServiceResult(false, "비밀번호가 일치하지 않습니다.");
        }

        member.setUserName("삭제회원");
        member.setPhone("");
        member.setPassword("");
        member.setRegDt(null);
        member.setUdtDt(null);
        member.setEmailAuthYn(false);
        member.setEmailAuthDt(null);
        member.setEmailAuthKey("");
        member.setResetPasswordKey("");
        member.setResetPasswordLimitDt(null);
        member.setUserStatus(MemberCode.MEMBER_STATUS_WITHDRAW);

        member.setZipcode("");
        member.setAddr("");
        member.setAddrDetail("");

        memberRepository.save(member);

        return new ServiceResult(true);
    }


    /**
     * SpringSecurity 사용을 위한 메서드
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 현재 시스템에서 username 은 email 의미
        Optional<Member> optionalMember = memberRepository.findById(username);

        if(!optionalMember.isPresent()){
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        if(Member.MEMBER_STATUS_REQ.equals(member.getUserStatus())){
            throw new MemberNotEmailAuthException("이메일 활성화 이후에 로그인을 해주세요.");
        }

        if (Member.MEMBER_STATUS_STOP.equals(member.getUserStatus())) {
            throw new MemberStopUserException("정지된 회원입니다.");
        }

        if (Member.MEMBER_STATUS_WITHDRAW.equals(member.getUserStatus())) {
            throw new MemberStopUserException("탈퇴된 회원입니다.");
        }

        /*
        if(!member.isEmailAuthYn()){

        }
         */

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if(member.isAdminYn()){
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return new User(member.getUserId(), member.getPassword(), grantedAuthorities);
    }
}
