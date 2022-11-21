package com.zerobase.fastlms.admin.service;

import com.zerobase.fastlms.admin.dto.BannerCategoryDto;
import com.zerobase.fastlms.admin.dto.CategoryDto;
import com.zerobase.fastlms.admin.model.CategoryInput;

import java.util.List;

public interface BannerCategoryService {
    List<BannerCategoryDto> list();
}
