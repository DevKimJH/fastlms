package com.zerobase.fastlms.course.service;

import com.zerobase.fastlms.course.dto.BannerDto;
import com.zerobase.fastlms.course.dto.CourseDto;
import com.zerobase.fastlms.course.model.*;

import java.util.List;

public interface BannerService {


    /**
     * 배너 등록
     */
    boolean add(BannerInput parameter);

    /**
     * 강좌정보 수정
     */
    boolean set(BannerInput parameter);


    /**
     * 강좌 목록
     */
    List<BannerDto> list(BannerParam parameter);


    /**
     * 강좌 상세정보
     */
    BannerDto getById(long id);


    /**
     * 강좌 내용 삭제
     */
    boolean del(String idList);

    /**
     * 전체 강좌 목록
     */
    List<CourseDto> listAll();


    /**
     * 프론트 배너
     */
    List<BannerDto> frontList();
}
