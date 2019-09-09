package net.thedigitallink.flutter.genload;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
@ToString
@Getter
@Setter
public class GenLoadConfig {
    private int rate=100;
    private double users=.1;
    private double follows=.1;
    private double messages=.5;
    private double timelineViews=.2;
    private double unfollows = .1;
    private int duration = 30;
    private int warmup = 30;
    private int reportingInterval=1000;
    private int consumers = 1;
    private int producers = 1;
    private int poll=100;
    public GenLoadConfig(ApplicationArguments applicationArguments) {
        if(applicationArguments.containsOption("rate")) {
            rate=Integer.parseInt(applicationArguments.getOptionValues("rate").get(0));
        }
        if(applicationArguments.containsOption("users")) {
            users=Double.parseDouble(applicationArguments.getOptionValues("users").get(0));
        }
        if(applicationArguments.containsOption("follows")) {
            follows=Double.parseDouble(applicationArguments.getOptionValues("follows").get(0));
        }
        if(applicationArguments.containsOption("messages")) {
            messages=Double.parseDouble(applicationArguments.getOptionValues("messages").get(0));
        }
        if(applicationArguments.containsOption("timelineViews")) {
            timelineViews=Double.parseDouble(applicationArguments.getOptionValues("timelineViews").get(0));
        }
        if(applicationArguments.containsOption("unfollows")) {
            unfollows=Double.parseDouble(applicationArguments.getOptionValues("unfollows").get(0));
        }
        if(applicationArguments.containsOption("duration")) {
            duration=Integer.parseInt(applicationArguments.getOptionValues("duration").get(0));
        }
        if(applicationArguments.containsOption("warmup")) {
            warmup=Integer.parseInt(applicationArguments.getOptionValues("warmup").get(0));
        }
        if(applicationArguments.containsOption("reportingInterval")) {
            reportingInterval=Integer.parseInt(applicationArguments.getOptionValues("reportingInterval").get(0));
        }
        if(applicationArguments.containsOption("consumers")) {
            consumers=Integer.parseInt(applicationArguments.getOptionValues("consumers").get(0));
        }
        if(applicationArguments.containsOption("producers")) {
            producers=Integer.parseInt(applicationArguments.getOptionValues("producers").get(0));
        }
        if(applicationArguments.containsOption("poll")) {
            poll=Integer.parseInt(applicationArguments.getOptionValues("poll").get(0));
        }
    }
}
