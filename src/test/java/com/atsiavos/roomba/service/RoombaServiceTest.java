package com.atsiavos.roomba.service;

import com.atsiavos.roomba.model.RoombaRequest;
import com.atsiavos.roomba.model.RoombaResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class RoombaServiceTest {

    @Test
    public void testNavigate_ValidMovement() {

        RoombaService roombaService = new RoombaService();
        RoombaRequest request = new RoombaRequest(
                List.of(5, 5),   // roomSize
                List.of(1, 2),   // initial coordinates
                List.of(List.of(1, 0), List.of(2, 2), List.of(2, 3)), // list of patches
                "NNESEESWNWW" // instructions
        );


        RoombaResponse response = roombaService.navigate(request);


        assertEquals(List.of(1, 3), response.getCoords());  // Final coordinates
        assertEquals(1, response.getPatches());  // Patches cleaned
    }

    @Test
    public void testNavigate_OutOfBounds() {

        RoombaService roombaService = new RoombaService();
        RoombaRequest request = new RoombaRequest(
                List.of(5, 5),
                List.of(1, 1),
                List.of(),  // No dirt patches
                "NNNNN" // Out of bounds
        );


        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            roombaService.navigate(request);
        });

        assertEquals("Out of bounds position: (1, 5)", thrown.getMessage());
    }
}
