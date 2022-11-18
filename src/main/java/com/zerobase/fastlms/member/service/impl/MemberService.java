package com.zerobase.fastlms.member.service.impl;

import com.zerobase.fastlms.member.model.MemberInput;


public interface MemberService {
    boolean register(MemberInput parameter);

    /**
     * uuid에 해당하는 계정을 활성화 함.
     */
    boolean emailAuth(String uuid);
}
