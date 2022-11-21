package com.zerobase.fastlms.admin.dto;

import com.zerobase.fastlms.admin.entity.BannerCategory;
import com.zerobase.fastlms.admin.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BannerCategoryDto {

    Long id;
    String categoryName;

    public static List<BannerCategoryDto> of(List<BannerCategory> categories){

        if(categories != null){
            List<BannerCategoryDto> categoryList = new ArrayList<>();

            for(BannerCategory x : categories){
                categoryList.add(of(x));
            }

            return categoryList;
        }

        return null;
    }

    public static BannerCategoryDto of(BannerCategory bannerCategory){
        return BannerCategoryDto.builder()
                .id(bannerCategory.getId())
                .categoryName(bannerCategory.getCategoryName())
                .build();
    }
}
