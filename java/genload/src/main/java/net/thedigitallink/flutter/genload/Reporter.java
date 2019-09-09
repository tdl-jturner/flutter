package net.thedigitallink.flutter.genload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class Reporter extends Thread{

    Boolean keepRunning = true;
    GenLoadConfig config;

    public Reporter(GenLoadConfig config) {
        this.config=config;
    }

    public void close() {
        keepRunning=false;
    }

    public void run() {
        log.info("Started {}({})",this.getClass().getSimpleName(), Thread.currentThread().getId() );
        long lastCount = 0;
        long lastTime = 0;
        while(keepRunning) {
            long currentCount = Application.actionQueue.size();
            long currentTime = System.currentTimeMillis();
            try {
                if (lastCount == 0) {
                    log.info("Current Queue Size: Current={}, Delta={}, Rate={}", currentCount, "N/A", "N/A");
                } else {
                    log.info("Current Queue Size: Current={}, Delta={}, Rate={}", currentCount, lastCount - currentCount, Math.abs((lastCount - currentCount) / ((lastTime - currentTime) / 1000)));
                }
            }
            catch (Exception e) {
                log.info("Failed to get queue size.");
            }

            lastCount=currentCount;
            lastTime=currentTime;
            try {
                Thread.sleep(this.config.getReportingInterval());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("Stopped {}({})",this.getClass().getSimpleName(), Thread.currentThread().getId() );
    }

}
