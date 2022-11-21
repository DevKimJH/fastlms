package com.zerobase.fastlms.course.mapper;

import com.zerobase.fastlms.course.dto.BannerDto;
import com.zerobase.fastlms.course.dto.CourseDto;
import com.zerobase.fastlms.course.model.BannerParam;
import com.zerobase.fastlms.course.model.CourseParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BannerMapper {
    long selectListCount(BannerParam parameter);
    List<BannerDto> selectList(BannerParam parameter);
}
