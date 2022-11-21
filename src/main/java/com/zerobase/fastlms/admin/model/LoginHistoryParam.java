package com.zerobase.fastlms.admin.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoginHistoryParam {
    String userId;
    String clientIp;
    String userAgent;
    LocalDateTime loginTime;
}
