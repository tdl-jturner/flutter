package net.thedigitallink.flutter.service.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractEntity {

    @Getter
    class Request {
        AbstractEntity payload;
        Request(AbstractEntity payload) {
            this.payload = payload;
        }
    }

    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("Unable to render JSON",e);
            return "Unable to render JSON.";
        }
    }

    public String toRequestString() {
        try {
            return new ObjectMapper().writeValueAsString(new Request(this));
        } catch (JsonProcessingException e) {
            log.error("Unable to render JSON",e);
            return "Unable to render JSON.";
        }
    }
}
