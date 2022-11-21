package com.zerobase.fastlms.admin.service;

import com.zerobase.fastlms.admin.dto.BannerCategoryDto;
import com.zerobase.fastlms.admin.dto.CategoryDto;
import com.zerobase.fastlms.admin.entity.BannerCategory;
import com.zerobase.fastlms.admin.entity.Category;
import com.zerobase.fastlms.admin.mapper.CategoryMapper;
import com.zerobase.fastlms.admin.model.CategoryInput;
import com.zerobase.fastlms.admin.repository.BannerCategoryRepository;
import com.zerobase.fastlms.admin.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class BannerCategoryServiceImpl implements BannerCategoryService{

    private final BannerCategoryRepository bannerCategoryRepository;


    @Override
    public List<BannerCategoryDto> list() {
        List<BannerCategory> bannerCategories = bannerCategoryRepository.findAll();
        return BannerCategoryDto.of(bannerCategories);
    }
}
