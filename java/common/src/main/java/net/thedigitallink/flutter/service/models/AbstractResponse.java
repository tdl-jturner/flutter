package net.thedigitallink.flutter.service.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractResponse<E>{
    List<E> payload = new ArrayList<>();
}
