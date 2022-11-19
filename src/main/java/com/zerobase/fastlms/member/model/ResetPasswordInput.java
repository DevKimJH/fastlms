package com.zerobase.fastlms.member.model;


import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class ResetPasswordInput {
    private String userId;
    private String userName;

    private String id; // 비밀번호 초기화 uuid값
    private String password;
}
