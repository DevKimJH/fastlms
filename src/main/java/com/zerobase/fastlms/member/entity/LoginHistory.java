package com.zerobase.fastlms.member.entity;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@ToString
public class LoginHistory{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String userId;
    private String clientIp;
    private String userAgent;
    private LocalDateTime loginTime;


}
