package com.zerobase.fastlms.course.dto;


import com.zerobase.fastlms.course.entity.Banner;
import com.zerobase.fastlms.course.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BannerDto {

    Long id;
    String subject;
    String urlAddress;
    int categoryId;
    int sortValue;
    boolean postYn;

    String filename;
    String urlFilename;

    LocalDateTime regDt; // 등록일(등록날짜)
    LocalDateTime udtDt; // 수정일(수정날짜)

    // 추가 컬럼
    long totalCount;
    long seq;
    String targetText;


    public static BannerDto of(Banner banner) {

        String target = "";

        if(banner.getCategoryId() == 1){
            target = "_target";
        }
        else if(banner.getCategoryId() == 2){
            target = "_self";
        }

        return BannerDto.builder()
                .id(banner.getId())
                .categoryId(banner.getCategoryId())
                .subject(banner.getSubject())
                .urlAddress(banner.getUrlAddress())
                .sortValue(banner.getSortValue())
                .targetText(target)
                .postYn(banner.isPostYn())
                .regDt(banner.getRegDt())
                .udtDt(banner.getUdtDt())
                .filename(banner.getFilename())
                .urlFilename(banner.getUrlFilename())
                .build();
    }


    public static List<BannerDto> of(List<Banner> courses){

        if(courses == null){
            return null;
        }

        List<BannerDto> courseList = new ArrayList<>();

        for(Banner x : courses){
            courseList.add(BannerDto.of(x));
        }

        return courseList;
    }

    public String getRegDtText(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return regDt != null ? regDt.format(formatter) : "";
    }
}
