package com.zerobase.fastlms.member.controller;

import com.zerobase.fastlms.admin.dto.MemberDto;
import com.zerobase.fastlms.course.dto.TakeCourseDto;
import com.zerobase.fastlms.course.model.ServiceResult;
import com.zerobase.fastlms.course.service.TakeCourseService;
import com.zerobase.fastlms.member.model.MemberInput;
import com.zerobase.fastlms.member.model.ResetPasswordInput;
import com.zerobase.fastlms.member.service.MemberService;
import com.zerobase.fastlms.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberService memberService;
    private final TakeCourseService takeCourseService;

    //@GetMapping("/member/register")
    //@RequestMapping(value = "/member/register", method = {RequestMethod.GET, RequestMethod.POST})


    @RequestMapping("/member/login")
    public String login(){
        return "member/login";
    }


    @GetMapping("/member/find/password")
    public String findPassword() {
        return "member/find_password";
    }

    @PostMapping("/member/find/password")
    public String findPasswordSubmit(
            Model model, ResetPasswordInput parameter) {


        boolean result = false;

        try{
            result = memberService.sendResetPassword(parameter);
        }
        catch(Exception e){

        }


        model.addAttribute("result", result);

        return "member/find_password_result";
    }


    // url 호출시
    //@RequestMapping(value = "/member/register", method = RequestMethod.GET)
    @GetMapping("/member/register")
    public String register(){

        System.out.println("request get");

        return "member/register";
    }



    // request  WEB -> SERVER
    // response SERVER -> WEB

    // submit 제출시
    //@RequestMapping(value="/member/register", method= RequestMethod.POST)
    @PostMapping("/member/register")
    public String registerSubmit(Model model, HttpServletRequest request, HttpServletResponse response, MemberInput parameter){

        System.out.println(parameter.toString());

        boolean result = memberService.register(parameter);


        model.addAttribute("result", result);

        return "member/register_complete";
    }

    @GetMapping("/member/email-auth")
    public String emailAuth(Model model, HttpServletRequest request){

        String uuid = request.getParameter("id");

        System.out.println(uuid);

        boolean result = memberService.emailAuth(uuid);
        model.addAttribute("result", result);
        return "member/email_auth";
    }


    @GetMapping("/member/info")
    public String memberInfo(Model model, Principal principal){

        String userId = principal.getName(); // userId가 담김

        MemberDto detail = memberService.detail(userId);

        model.addAttribute("detail", detail);

        return "member/info";
    }

    @PostMapping("/member/info")
    public String memberInfoSubmit(Model model
                                , Principal principal
                                , MemberInput parameter){

        String userId = principal.getName(); // userId가 담김
        parameter.setUserId(userId);

        ServiceResult result = memberService.updateMember(parameter);

        if(!result.isResult()){
            model.addAttribute("message", result.getMessage());
            return "comm/error";
        }

        return "redirect:/member/info";
    }

    @GetMapping("/member/reset/password")
    public String resetPassword(Model model, HttpServletRequest request){

        String uuid = request.getParameter("id");

        boolean result = memberService.checkResetPassword(uuid);

        model.addAttribute("result", result);

        return "member/reset_password";
    }


    @PostMapping("/member/reset/password")
    public String resetPasswordSubmit(Model model, ResetPasswordInput parameter){

        boolean result = false;

        try{
            result = memberService.resetPassword(parameter.getId(), parameter.getPassword());
        }
        catch(Exception e){

        }


        model.addAttribute("result", result);

        return "member/reset_password_result";
    }

    @GetMapping("/member/password")
    public String memberPassword(Model model, Principal principal){

        String userId = principal.getName(); // userId가 담김

        MemberDto detail = memberService.detail(userId);

        model.addAttribute("detail", detail);

        return "member/password";
    }

    @PostMapping("/member/password")
    public String memberPasswordSubmit(Model model
                                    , MemberInput parameter
                                    , Principal principal){

        String userId = principal.getName(); // userId가 담김
        parameter.setUserId(userId);

        ServiceResult result = memberService.updateMemberPassword(parameter);

        if(!result.isResult()){
            model.addAttribute("message", result.getMessage());
            return "comm/error";
        }

        return "redirect:/member/info";
    }

    @GetMapping("/member/takecourse")
    public String memberTakeCourse(Model model, Principal principal){

        String userId = principal.getName(); // userId가 담김


        List<TakeCourseDto> list = takeCourseService.myCourse(userId);

        model.addAttribute("list", list);

        return "member/takecourse";
    }



    @GetMapping("/member/withdraw")
    public String memberWithdraw(Model model){


        return "member/withdraw";
    }

    @PostMapping("/member/withdraw")
    public String memberWithdrawSubmit(Model model
                            , Principal principal
                            , MemberInput parameter){

        String userId = principal.getName();

        ServiceResult result = memberService.withdraw(userId, parameter.getPassword());

        if(!result.isResult()){
            model.addAttribute("message", result.getMessage());
            return "common/error";
        }


        return "redirect:/member/logout";
    }
}
