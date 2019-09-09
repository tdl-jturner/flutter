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
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class FlutterController extends BaseController {

    @GetMapping("/")
    public String index(HttpServletResponse response, Model model) {
        return "index";
    }

    @GetMapping("/userList")
    public String userlist(Model model,@CookieValue(value = "loggedInUser", defaultValue="") String loggedInUser) {
        Map<String, Map<String,Integer>> users = new HashMap<>();
        ResponseEntity<List<User>> userEntity = restTemplate.exchange( getUri("user-service", "/getAll"), HttpMethod.GET,null, new ParameterizedTypeReference<List<User>>(){});

        for(User user : userEntity.getBody()) {
            log.error("Pulling counts for user {}",user.getUsername());
            Map<String,Integer> counts = new HashMap();
            ResponseEntity<List<Message>> messageEntity = restTemplate.exchange( getUri("message-service", "/getAll/"+user.getUsername()), HttpMethod.GET,null, new ParameterizedTypeReference<List<Message>>(){});
            ResponseEntity<List<Follow>> followEntity = restTemplate.exchange( getUri("follow-service", "/get/"+user.getUsername()), HttpMethod.GET,null, new ParameterizedTypeReference<List<Follow>>(){});
            counts.put("messages",(messageEntity.getBody()==null?0:messageEntity.getBody().size()));
            counts.put("follows",(followEntity.getBody()==null?0:followEntity.getBody().size()));
            users.put(user.getUsername(),counts);
        }

        model.addAttribute("users",users);
        model.addAttribute("loggedInUser",loggedInUser);

        return "userList";
    }
}
