package net.thedigitallink.flutter.ui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
public class LoginController {

    @GetMapping("/login/{username}")
    public String login(Model model, HttpServletResponse response, @PathVariable String username, @RequestHeader(value = "referer", required = false) String referer) {
        Cookie cookie =new Cookie("loggedInUser",username);
        cookie.setPath("/");
        cookie.setDomain("127.0.0.1");
        log.error("Creating cookie : {}",cookie.toString());
        response.addCookie(cookie);
        model.addAttribute("notice", "Logged in as "+username);
        if(referer!=null) model.addAttribute("redirect",referer);
        return "index";
    }

    @GetMapping("/logout")
    public String logout(Model model,HttpServletResponse response, @RequestHeader(value = "referer", required = false) String referer) {
        Cookie cookie =new Cookie("loggedInUser","");
        cookie.setPath("/");
        cookie.setDomain("127.0.0.1");
        cookie.setMaxAge(0);
        log.error("Removing cookie : {}",cookie.toString());
        response.addCookie(cookie);
        model.addAttribute("notice", "Logged Out");
        if(referer!=null) model.addAttribute("redirect",referer);
        return "index";
    }
}
