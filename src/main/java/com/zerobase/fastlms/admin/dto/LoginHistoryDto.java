package com.zerobase.fastlms.admin.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LoginHistoryDto {

    long id;
    String userId;
    String clientIp;
    String userAgent;
    LocalDateTime loginTime;
}
