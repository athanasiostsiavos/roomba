package com.atsiavos.roomba.model;

import java.util.List;

public record RoombaResponse(List<Integer> coords, int patches) {}
