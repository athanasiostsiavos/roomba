package com.atsiavos.roomba.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoombaRequest {
    private List<Integer> roomSize;
    private List<Integer> coords;
    private List<List<Integer>> patches;
    private String instructions;
}
