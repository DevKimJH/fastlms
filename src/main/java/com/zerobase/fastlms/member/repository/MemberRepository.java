package com.zerobase.fastlms.member.repository;

import com.zerobase.fastlms.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {


     // Spring JPA가 자동으로 이 method를 implement한다.
     Optional<Member> findByEmailAuthKey(String emailAuthKey);

     Optional<Member> findByUserIdAndUserName(String userId, String userName);

     Optional<Member> findByResetPasswordKey(String resetPasswordKey);

}
