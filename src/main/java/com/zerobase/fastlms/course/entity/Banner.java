package com.zerobase.fastlms.course.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String subject;
    String urlAddress;
    int categoryId;
    int sortValue;
    boolean postYn;


    LocalDateTime regDt; // 등록일(등록날짜)
    LocalDateTime udtDt; // 수정일(수정날짜)

    String filename;
    String urlFilename;
}
