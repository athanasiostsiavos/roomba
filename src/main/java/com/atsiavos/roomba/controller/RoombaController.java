package com.atsiavos.roomba.controller;


import com.atsiavos.roomba.model.RoombaRequest;
import com.atsiavos.roomba.model.RoombaResponse;
import com.atsiavos.roomba.service.RoombaService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/roomba")
public class RoombaController {

    private final RoombaService roombaService;

    public RoombaController(RoombaService roombaService) {
        this.roombaService = roombaService;
    }

    @PostMapping("/navigate")
    public RoombaResponse navigate(@RequestBody RoombaRequest request) {
        return roombaService.navigate(request);
    }
}
