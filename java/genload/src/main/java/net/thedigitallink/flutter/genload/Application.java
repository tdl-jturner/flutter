package net.thedigitallink.flutter.genload;

import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
@Slf4j
@EnableAsync
public class Application implements CommandLineRunner {

    public static final Queue<Action> actionQueue = new LinkedBlockingQueue<>();

    @Autowired
    private GenLoadConfig config;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    public EurekaClient eurekaClient;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        log.info(config.toString());


        Reporter reporter = new Reporter(config);
        reporter.start();

        Producer producer = new Producer(config,eurekaClient);
        for(int i=0;i<config.getProducers();i++) {
            producer.start();
        }

        log.info("Warming up for {}",config.getWarmup());
        Thread.sleep(config.getWarmup());

        List<Consumer> consumerList = new ArrayList<>();
        for(int i=0;i<config.getConsumers();i++) {
            Consumer consumer = new Consumer(config);
            consumer.start();
            consumerList.add(consumer);
        }

        while(actionQueue.size()>0 || producer.isAlive()) {
            Thread.sleep(config.getPoll());
        }
        log.info("Shutting Down");
        reporter.close();
        consumerList.forEach(c -> c.close());

        System.exit(0);
    }

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AsynchThread-");
        executor.initialize();
        return executor;
    }
}

