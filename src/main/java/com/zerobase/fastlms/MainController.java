package com.zerobase.fastlms;

// MainPage 클래스를 만드는 목적
// 매핑하기 위해서
// 논리적인 주소(인터넷 주소)와 물리적인 파일(프로그래밍 소스 파일) 매핑

// http://ww.naver.com/new/list.do
// 하나의 주소에 대해서
// 어디서 매핑? 누가 매핑?
// 후보군? 클래스, 속성, 메소드

// http://localhost:8080/


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class MainController {

    @RequestMapping("/")
    public String index(){
        return "index";
    }


    // request -> WEB -> SERVER
    // response -> SERVER -> WEB
    @RequestMapping("/hello")
    public void hello(HttpServletRequest request, HttpServletResponse response) throws IOException {


        response.setContentType("text/html;charset=UTF-8");

        PrintWriter printWriter = response.getWriter();


        String msg =
                "<html>" +
                        "<head>" +
                        "<meta charset='utf-8'>" +
                        "</head>" +
                        "<body>" +
                        "<p>hello</p>" +
                        "<p>fastlms website!!!</p>" +
                        "<p> 안녕하세요 </p>"+
                        "</body>" +
                "</html>";
        printWriter.write(msg);
        printWriter.close();
    }

}
