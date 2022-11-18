package com.zerobase.fastlms;

// MainPage 클래스를 만드는 목적
// 매핑하기 위해서
// 논리적인 주소(인터넷 주소)와 물리적인 파일(프로그래밍 소스 파일) 매핑

// http://ww.naver.com/new/list.do
// 하나의 주소에 대해서
// 어디서 매핑? 누가 매핑?
// 후보군? 클래스, 속성, 메소드

// http://localhost:8080/


import com.zerobase.fastlms.components.MailComponents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
@Controller
public class MainController {


    private final MailComponents mailComponents;


    @RequestMapping("/")
    public String index(){

        String email= "hg116701@naver.com";
        String subject = "안녕하세요. 제로베이스입니다.";
        String text = "<p> 안녕하세요. </p><p>반갑습니다.</p>";

        //mailComponents.sendMailTest();
        //mailComponents.sendMail(email, subject, text);

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
