package com.atsiavos.roomba.service;

import static org.junit.jupiter.api.Assertions.*;

import com.atsiavos.roomba.model.Patch;
import com.atsiavos.roomba.model.RoombaRequest;
import com.atsiavos.roomba.model.RoombaResponse;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoombaServiceTest {

  @Test
  void testNavigate_ValidMovement() {

    RoombaService roombaService = new RoombaService();
    RoombaRequest request =
        new RoombaRequest(
            List.of(5, 5), // roomSize
            List.of(1, 2), // initial coordinates
            List.of(new Patch(1, 0), new Patch(2, 2), new Patch(2, 3)), // list of patches
            "NNESEESWNWW" // instructions
            );

    RoombaResponse response = roombaService.navigate(request);

    assertEquals(List.of(1, 3), response.coords()); // Final coordinates
    assertEquals(1, response.patches()); // Patches cleaned
  }

  @Test
  void testNavigate_OutOfBounds() {

    RoombaService roombaService = new RoombaService();
    RoombaRequest request =
        new RoombaRequest(
            List.of(5, 5),
            List.of(1, 1),
            List.of(), // No dirt patches
            "NNNNN" // Out of bounds
            );

    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> roombaService.navigate(request));

    assertEquals("Out of bounds position: (1, 5)", thrown.getMessage());
  }
}
