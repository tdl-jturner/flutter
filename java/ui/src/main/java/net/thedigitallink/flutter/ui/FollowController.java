package net.thedigitallink.flutter.ui;

import lombok.extern.slf4j.Slf4j;
import net.thedigitallink.flutter.service.models.Follow;
import net.thedigitallink.flutter.service.models.Message;
import net.thedigitallink.flutter.service.models.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class FollowController extends BaseController {

    @GetMapping("/follow/{username}")
    public String follow(Model model,@PathVariable String username,@CookieValue(value = "loggedInUser", defaultValue="") String loggedInUser, @RequestHeader(value = "referer", required = false) String referer) {
        if(loggedInUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        else {
            restTemplate.postForEntity(getUri("follow-service", "/create"), new HttpEntity<>(Follow.builder().author(username).follower(loggedInUser).build(), httpHeaders), Void.class);
            model.addAttribute("notice","You are now following"+username+".");
            model.addAttribute("redirect",referer);
            return "index";
        }
    }

    @GetMapping("/unfollow/{username}")
    public String unfollow(Model model,@PathVariable String username,@CookieValue(value = "loggedInUser", defaultValue="") String loggedInUser, @RequestHeader(value = "referer", required = false) String referer) {
        if(loggedInUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        else {
            restTemplate.postForEntity(getUri("follow-service", "/delete"), new HttpEntity<>(Follow.builder().author(username).follower(loggedInUser).build(), httpHeaders), Void.class);
            model.addAttribute("notice","You are no longer following"+username+".");
            model.addAttribute("redirect",referer);
            return "index";
        }
    }

}
