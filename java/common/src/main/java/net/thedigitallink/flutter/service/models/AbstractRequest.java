package net.thedigitallink.flutter.service.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractRequest<E> {
    E payload;
    public AbstractRequest(E payload) {
        this.payload=payload;
    }

    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "Unable to render JSON.";
        }
    }
    public static AbstractRequest fromJson(String json) throws IOException {
        return new ObjectMapper().readValue(json, AbstractRequest.class);
    }
}
