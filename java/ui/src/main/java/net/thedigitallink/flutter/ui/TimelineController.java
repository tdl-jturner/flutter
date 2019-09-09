package net.thedigitallink.flutter.ui;

import lombok.extern.slf4j.Slf4j;
import net.thedigitallink.flutter.service.models.Follow;
import net.thedigitallink.flutter.service.models.Message;
import net.thedigitallink.flutter.service.models.Timeline;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class TimelineController extends BaseController {


    @GetMapping("/timeline")
    public String myTimeline(Model model, @CookieValue(value = "loggedInUser", defaultValue="") String loggedInUser) {
        model.addAttribute("redirect","/timeline/"+loggedInUser);
        model.addAttribute("redirectTime",0);
        return "index";
    }

    @GetMapping("/timeline/{username}")
    public String timeline(Model model, @PathVariable String username,@CookieValue(value = "loggedInUser", defaultValue="") String loggedInUser) {
        log.trace("Logged in user {}",loggedInUser);
        model.addAttribute("loggedInUser",loggedInUser);
        model.addAttribute("timelineUser",username);
        ResponseEntity<List<Timeline>> entity;
        try {
            entity = restTemplate.exchange(getUri("timeline-service", String.format("/get/%s", username)), HttpMethod.GET, null, new ParameterizedTypeReference<List<Timeline>>(){});
            model.addAttribute("timelines",entity.getBody());
        }
        catch (HttpServerErrorException e) {
            model.addAttribute("timelines",null);
        }
        ResponseEntity<Boolean> exists = restTemplate.postForEntity(getUri("follow-service", "/exists"), new HttpEntity<>(Follow.builder().follower(loggedInUser).author(username).build(), httpHeaders), Boolean.class);
        model.addAttribute("following",exists.getBody());
        model.addAttribute("message", new Message());
        return "timeline";
    }

    @PostMapping("/timeline/{username}")
    public String timelineSubmit(Message message, Model model,@PathVariable String username,@CookieValue(value = "loggedInUser", defaultValue="") String loggedInUser) {
        if(loggedInUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        else {
            message.setAuthor(loggedInUser);
            restTemplate.postForEntity(getUri("message-service", "/create"), new HttpEntity<>(message, httpHeaders), Void.class);
            model.addAttribute("notice","Your message has been posted");
            model.addAttribute("redirect","/timeline/"+username);
            return "index";
        }
    }

}
