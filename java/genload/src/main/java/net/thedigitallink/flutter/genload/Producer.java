package net.thedigitallink.flutter.genload;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import net.thedigitallink.flutter.service.models.Follow;
import net.thedigitallink.flutter.service.models.Message;
import net.thedigitallink.flutter.service.models.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Slf4j
public class Producer extends Thread {

    private final List<String> userList;

    private final Random rand;

    private EurekaClient eurekaClient;
    private GenLoadConfig config;

    public Producer(GenLoadConfig genLoadConfig,EurekaClient eurekaClient) {
        this.eurekaClient=eurekaClient;
        this.config=genLoadConfig;
        this.userList = new ArrayList<>();
        this.rand = new Random();

        RestTemplate restTemplate= new RestTemplate();
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        ResponseEntity<List<User>> entity = restTemplate.exchange( getUri("user-service", "/getAll"), HttpMethod.GET,null, new ParameterizedTypeReference<List<User>>(){});
        if(entity !=null && entity.getBody() != null) {
            entity.getBody().forEach(u -> userList.add(u.getUsername()));
        }

    }

    private PodamFactory podamFactory = new PodamFactoryImpl();

    public void run() {
        log.info("Started {}({})",this.getClass().getSimpleName(), Thread.currentThread().getId() );
        for(int i = 0; i<this.config.getDuration();i++) {
            long users=Math.round(this.config.getUsers()*this.config.getRate());
            long follows=Math.round(this.config.getFollows()*this.config.getRate());
            long messages=Math.round(this.config.getMessages()*this.config.getRate());
            long unfollows=Math.round(this.config.getUnfollows()*this.config.getRate());
            long timelineViews=Math.round(this.config.getTimelineViews()*this.config.getRate());
            while(users>0) {
                Application.actionQueue.add(userAction());
                users--;
            }
            while(follows>0) {
                Application.actionQueue.add(followAction());
                follows--;
            }
            while(messages>0) {
                Application.actionQueue.add(messageAction());
                messages--;
            }
            while(unfollows>0) {
                Application.actionQueue.add(unfollowAction());
                unfollows--;
            }
            while(timelineViews>0) {
                Application.actionQueue.offer(timelineViewAction());
                timelineViews--;
            }
        }
        log.info("Stopped {}({})",this.getClass().getSimpleName(), Thread.currentThread().getId() );
    }

    private URI getUri(String service, String api) {
        if(eurekaClient==null) log.error("Fail");
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(service.toUpperCase(),false);
        return URI.create(String.format("http://%s:%s/%s%s",instanceInfo.getIPAddr(),instanceInfo.getPort(),service.toLowerCase(),api));
    }

    private String randomUser() {
        return userList.get(rand.nextInt(userList.size()));
    }

    private Action userAction() {
        User user = podamFactory.manufacturePojoWithFullData(User.class);
        userList.add(user.getUsername());
        return new Action(getUri("user-service","/create"),user.toString());
    }

    private Action followAction() {
        Follow follow = Follow.builder().author(randomUser()).follower(randomUser()).build();
        return new Action(getUri("follow-service","/create"),follow.toString());
    }

    private Action messageAction() {
        Message message = podamFactory.manufacturePojoWithFullData(Message.class);
        message.setAuthor(randomUser());
        return new Action(getUri("message-service","/create"),message.toString());
    }

    private Action unfollowAction() {
        Follow follow = Follow.builder().author(randomUser()).follower(randomUser()).build();
        return new Action(getUri("follow-service","/delete"),follow.toString());
    }

    private Action timelineViewAction() {
        return new Action(getUri("timeline-service","/get/"+randomUser()),null);
    }
}
