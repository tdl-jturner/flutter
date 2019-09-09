package net.thedigitallink.flutter.genload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.net.URI;

@AllArgsConstructor
@Getter
@ToString
public class Action {
    URI uri;
    String payload;
}
