package com.zerobase.fastlms.course.controller;

import com.zerobase.fastlms.admin.service.BannerCategoryService;
import com.zerobase.fastlms.admin.service.CategoryService;
import com.zerobase.fastlms.course.dto.BannerDto;
import com.zerobase.fastlms.course.dto.CourseDto;
import com.zerobase.fastlms.course.model.BannerInput;
import com.zerobase.fastlms.course.model.BannerParam;
import com.zerobase.fastlms.course.model.CourseParam;
import com.zerobase.fastlms.course.service.BannerService;
import com.zerobase.fastlms.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Controller
public class AdminBannerController extends BaseController{

    private final CourseService courseService;
    private final CategoryService categoryService;
    private final BannerCategoryService bannerCategoryService;
    private final BannerService bannerService;


    @GetMapping("/admin/banner/list.do")
    public String list(Model model, BannerParam parameter){

        List<BannerDto> bannerList = bannerService.list(parameter);

        long totalCount = 0;

        if(!CollectionUtils.isEmpty(bannerList)){
            totalCount = bannerList.get(0).getTotalCount();
        }

        String queryString = parameter.getQueryString();

        String pagerHtml = super.getPagerHtml(totalCount, parameter.getPageSize(), parameter.getPageIndex(), queryString);

        model.addAttribute("list", bannerList);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pager", pagerHtml);

        return "admin/banner/list";
    }

    @GetMapping(value={"/admin/banner/add.do", "/admin/banner/edit.do"})
    public String add(Model model, HttpServletRequest request, BannerInput parameter){

        // 오픈 방법 카테고리 정보
        model.addAttribute("openCategory", bannerCategoryService.list());


        // 추가인지 수정인지 구분
        boolean editMode = request.getRequestURI().contains("/edit.do");
        BannerDto detail = new BannerDto();

        if(editMode){
            long id = parameter.getId();
            BannerDto existBanner = bannerService.getById(id);

            if(existBanner == null){
                // error 처리
                model.addAttribute("message", "강좌정보가 존재하지 않습니다.");
                return "common/error";
            }

            detail = existBanner;
            model.addAttribute("detail", existBanner);
        }

        model.addAttribute("detail", detail);
        model.addAttribute("editMode", editMode);

        return "admin/banner/add";
    }


    private String[] getNewSaveFile(String baseLocalPath, String baseUrlPath, String originalFilename){

        LocalDate now = LocalDate.now();

        String[] dirs = {
                String.format("%s/%d/", baseLocalPath, now.getYear()),
                String.format("%s/%d/%02d", baseLocalPath, now.getYear(), now.getMonthValue()),
                String.format("%s/%d/%02d/%02d/", baseLocalPath, now.getYear(), now.getMonthValue(), now.getDayOfMonth())};

        String urlDir = String.format("%s/%d/%02d/%02d/", baseUrlPath, now.getYear(), now.getMonthValue(), now.getDayOfMonth());

        for(String dir : dirs){
            File file = new File(dir);

            if(!file.isDirectory()){
                file.mkdir();
            }
        }

        // 파일 확장자
        String fileExtension = "";

        if(originalFilename != null){
            int dotPos = originalFilename.lastIndexOf(".");
            if(dotPos > -1){
                fileExtension = originalFilename.substring(dotPos + 1);
            }
        }

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String newFilename = String.format("%s%s", dirs[2], uuid);
        String newUrlFilename = String.format("%s%s", urlDir, uuid);
        if(fileExtension.length() > 0){
            newFilename += "." + fileExtension;
            newUrlFilename += "." + fileExtension;
        }

        return new String[]{newFilename, newUrlFilename};
    }

    @PostMapping(value = {"/admin/banner/add.do", "/admin/banner/edit.do"})
    public String addSubmit(Model model, HttpServletRequest request
                , MultipartFile file
                , BannerInput parameter){


        String saveFilename = "";
        String urlFilename = "";

        if(file != null){
            String originalFilename = file.getOriginalFilename();
            String baseLocalPath = "/Users/kimjoohyun/dev/intellij-workspace/fastlms/files";
            String baseUrlPath = "/files";

            String[] arrFilenames  = getNewSaveFile(baseLocalPath, baseUrlPath, originalFilename);

            saveFilename = arrFilenames[0];
            urlFilename = arrFilenames[1];

            try{
                File newFile = new File(saveFilename);
                FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(newFile));
            }
            catch(IOException e){
                log.info("###############################");
                log.info(e.getMessage());
            }
        }

        parameter.setFilename(saveFilename);
        parameter.setUrlFilename(urlFilename);

        // 추가인지 수정인지 구분
        boolean editMode = request.getRequestURI().contains("/edit.do");
        if(editMode){
            long id = parameter.getId();
            BannerDto existBanner = bannerService.getById(id);

            if(existBanner == null){
                // error 처리
                model.addAttribute("message", "배너정보가 존재하지 않습니다.");
                return "common/error";
            }

            boolean result = bannerService.set(parameter);

        }
        else{
            boolean result = bannerService.add(parameter);
        }

        return "redirect:/admin/banner/list.do";
    }

    @PostMapping("/admin/banner/delete.do")
    public String del(Model model, HttpServletRequest request, BannerInput parameter){
        boolean result = bannerService.del(parameter.getIdList());

        return "redirect:/admin/banner/list.do";
    }

}
