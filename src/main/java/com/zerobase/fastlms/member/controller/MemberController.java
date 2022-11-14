package com.zerobase.fastlms.member.controller;

import com.zerobase.fastlms.member.entity.Member;
import com.zerobase.fastlms.member.model.MemberInput;
import com.zerobase.fastlms.member.repository.MemberRepository;
import com.zerobase.fastlms.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberService memberService;

    //@GetMapping("/member/register")
    //@RequestMapping(value = "/member/register", method = {RequestMethod.GET, RequestMethod.POST})

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
    public String registerSubmit(HttpServletRequest request, HttpServletResponse response, MemberInput parameter){

        System.out.println(parameter.toString());

        boolean result = memberService.register(parameter);




        return "member/register_complete";
    }
}
