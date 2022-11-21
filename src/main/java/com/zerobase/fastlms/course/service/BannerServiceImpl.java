package com.zerobase.fastlms.course.service;

import com.zerobase.fastlms.course.dto.BannerDto;
import com.zerobase.fastlms.course.dto.CourseDto;
import com.zerobase.fastlms.course.entity.Course;
import com.zerobase.fastlms.course.mapper.BannerMapper;
import com.zerobase.fastlms.course.mapper.CourseMapper;
import com.zerobase.fastlms.course.model.BannerInput;
import com.zerobase.fastlms.course.model.BannerParam;
import com.zerobase.fastlms.course.repository.BannerRepository;
import com.zerobase.fastlms.course.repository.TakeCourseRepository;
import lombok.RequiredArgsConstructor;
import com.zerobase.fastlms.course.entity.Banner;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class BannerServiceImpl implements BannerService{

    private final BannerRepository bannerRepository;
    private final TakeCourseRepository takeCourseRepository;
    private final CourseMapper courseMapper;
    private final BannerMapper bannerMapper;

    private LocalDate getLocalDate(String value){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try{
            return LocalDate.parse(value, formatter);
        }
        catch(Exception e){

        }

        return null;

    }

    @Override
    public boolean add(BannerInput parameter) {

        Banner banner = Banner.builder()
                .postYn(parameter.isPostYn())
                .sortValue(parameter.getSortValue())
                .categoryId(parameter.getCategoryId())
                .urlAddress(parameter.getUrlAddress())
                .subject(parameter.getSubject())
                .regDt(LocalDateTime.now())
                .filename(parameter.getFilename())
                .urlFilename(parameter.getUrlFilename())
                .build();


        bannerRepository.save(banner);

        return true;
    }

    @Override
    public boolean set(BannerInput parameter) {

        Optional<Banner> optionalBanner = bannerRepository.findById(parameter.getId());

        if(!optionalBanner.isPresent()){
            return false;
        }

        Banner banner = optionalBanner.get();
        banner.setCategoryId(parameter.getCategoryId());
        banner.setFilename(parameter.getFilename());
        banner.setPostYn(parameter.isPostYn());
        banner.setUdtDt(LocalDateTime.now());
        banner.setSubject(parameter.getSubject());
        banner.setSortValue(parameter.getSortValue());
        banner.setUrlAddress(parameter.getUrlAddress());
        banner.setUrlFilename(parameter.getUrlFilename());
        bannerRepository.save(banner);
        return true;
    }

    @Override
    public List<BannerDto> list(BannerParam parameter) {

        long totalCount = bannerMapper.selectListCount(parameter);

        List<BannerDto> list = bannerMapper.selectList(parameter);

        // list가 empty가 아니면
        if (!CollectionUtils.isEmpty(list)) {
            int i = 0;

            for(BannerDto x : list){
                x.setTotalCount(totalCount);
                x.setSeq(totalCount - parameter.getPageStart() - i);
                i++;
            }
        }

        return list;
    }

    @Override
    public BannerDto getById(long id) {

        return bannerRepository.findById(id).map(BannerDto::of).orElse(null);
    }

    @Override
    public boolean del(String idList) {

        if( idList != null && idList.length() > 0){
            String[] ids = idList.split(",");

            for(String x : ids){
                long id = 0L;

                try{
                    id = Long.parseLong(x);
                }
                catch(Exception e){

                }

                if(id > 0){
                    bannerRepository.deleteById(id);
                }
            }
        }

        return true;
    }

    @Override
    public List<CourseDto> listAll() {
        return null;
    }

    @Override
    public List<BannerDto> frontList() {

        Optional<List<Banner>> optionalBanners = bannerRepository.findByPostYnOrderBySortValue(true);

        if(optionalBanners.isPresent()){
            return BannerDto.of(optionalBanners.get());
        }
        return null;
    }


    /*
    @Override
    public boolean set(CourseInput parameter) {

        System.out.println("####################################");
        System.out.println(parameter.toString());
        LocalDate saleEndDt = getLocalDate(parameter.getSaleEndDtText());
        System.out.println(saleEndDt);

        Optional<Course> optionalCourse = courseRepository.findById(parameter.getId());

        if(!optionalCourse.isPresent()){
            return false;
        }

        System.out.println("ENTER");
        System.out.println("####################################");
        Course course = optionalCourse.get();
        course.setCategoryId(parameter.getCategoryId());
        course.setSubject(parameter.getSubject());
        course.setKeyword(parameter.getKeyword());
        course.setSummary(parameter.getSummary());
        course.setContents(parameter.getContents());
        course.setPrice(parameter.getPrice());
        course.setSalePrice(parameter.getSalePrice());
        course.setSaleEndDt(saleEndDt);
        course.setUdtDt(LocalDateTime.now());
        course.setFilename(parameter.getFilename());
        course.setUrlFilename(parameter.getUrlFilename());
        courseRepository.save(course);

        return true;

    }

    @Override
    public List<CourseDto> list(CourseParam parameter) {

        long totalCount = courseMapper.selectListCount(parameter);

        List<CourseDto> list = courseMapper.selectList(parameter);

        // list가 empty인 경우
        if(!CollectionUtils.isEmpty(list)){

            int i = 0;

            for(CourseDto x : list){
                x.setTotalCount(totalCount);
                x.setSeq(totalCount - parameter.getPageStart() - i);
                i++;
            }
        }


        return list;
    }

    @Override
    public CourseDto getById(long id) {
        return courseRepository.findById(id).map(CourseDto::of).orElse(null);
    }

    @Override
    public boolean del(String idList) {

        if ( idList != null && idList.length() > 0){
            String[] ids = idList.split(",");

            for(String x : ids){
                long id = 0L;
                try{
                    id = Long.parseLong(x);
                }
                catch(Exception e){

                }

                if( id > 0){
                    courseRepository.deleteById(id);
                }
            }
        }

        return true;
    }

    @Override
    public List<CourseDto> frontList(CourseParam parameter) {

        if(parameter.getCategoryId() < 1){
            List<Course> courseList = courseRepository.findAll();
            return CourseDto.of(courseList);
        }

        Optional<List<Course>> optionalCourses = courseRepository.findByCategoryId(parameter.getCategoryId());

        if(optionalCourses.isPresent()){
            return CourseDto.of(optionalCourses.get());
        }

        return null;
    }

    @Override
    public CourseDto frontDetail(long id) {

        Optional<Course> optionalCourse = courseRepository.findById(id);
        if(optionalCourse.isPresent()){
            return CourseDto.of(optionalCourse.get());
        }

        return null;
    }


     // 수강신청
    @Override
    public ServiceResult req(TakeCourseInput parameter) {

        ServiceResult result = new ServiceResult();

        Optional<Course> optionalCourse = courseRepository.findById(parameter.getCourseId());

        if(!optionalCourse.isPresent()){

            result.setResult(false);
            result.setMessage("강좌 정보가 존재하지 않습니다.");

            return result;
        }

        Course course = optionalCourse.get();

        // 이미 신청정보가 있는지 확인
        String[] statusList = {TakeCourse.STATUS_REQ, TakeCourseCode.STATUS_COMPLETE};
        long count = takeCourseRepository.countByCourseIdAndUserIdAndStatusIn(course.getId(), parameter.getUserId(), Arrays.asList(statusList));

        if(count > 0){
            result.setResult(false);
            result.setMessage("이미 신청한 강좌 정보가 존재합니다.");

            return result;
        }

        TakeCourse takeCourse = TakeCourse.builder()
                .courseId(course.getId())
                .userId(parameter.getUserId())
                .payPrice(course.getSalePrice())
                .regDt(LocalDateTime.now())
                .status(TakeCourse.STATUS_REQ)
                .build();

        takeCourseRepository.save(takeCourse);

        result.setResult(true);
        result.setMessage("");

        return result;
    }

    @Override
    public List<CourseDto> listAll() {

        List<Course> courseList = courseRepository.findAll();

        return CourseDto.of(courseList);
    }
    */
}
