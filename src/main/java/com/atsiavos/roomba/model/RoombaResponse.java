package com.atsiavos.roomba.model;



import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RoombaResponse {
    private List<Integer> coords;
    private int patches;
}

