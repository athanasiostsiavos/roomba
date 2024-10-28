package com.atsiavos.roomba.model;

import java.util.List;

public record RoombaRequest(
    List<Integer> roomSize, List<Integer> coords, List<Patch> patches, String instructions) {}
