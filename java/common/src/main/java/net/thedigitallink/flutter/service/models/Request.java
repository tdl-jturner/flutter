package net.thedigitallink.flutter.service.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Request<E> {
    E payload;
    public Request(E payload) {
        this.payload=payload;
    }
}
