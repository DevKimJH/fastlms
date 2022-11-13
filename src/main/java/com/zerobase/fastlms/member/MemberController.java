package com.zerobase.fastlms.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {



    @GetMapping("/member/register")
    public String register(){
        System.out.println("enter");
        return "member/register";
    }
}
