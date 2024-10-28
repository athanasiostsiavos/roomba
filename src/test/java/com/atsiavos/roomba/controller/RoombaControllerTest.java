package com.atsiavos.roomba.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.atsiavos.roomba.model.Patch;
import com.atsiavos.roomba.model.RoombaRequest;
import com.atsiavos.roomba.model.RoombaResponse;
import com.atsiavos.roomba.service.RoombaService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(RoombaController.class)
class RoombaControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private RoombaService roombaService;

  private RoombaRequest request;
  private RoombaResponse response;

  @BeforeEach
  public void setup() {
    // Set up a general response object
    response =
        new RoombaResponse(
            List.of(1, 3), // final coords
            1 // number of patches cleaned
            );
  }

  @Test
  void testNavigateHoover() throws Exception {
    request =
        new RoombaRequest(
            List.of(5, 5), // roomSize
            List.of(1, 2), // initial coords
            List.of(new Patch(1, 0), new Patch(2, 2), new Patch(2, 3)), // patches
            "NNESEESWNWW" // instructions
            );

    Mockito.when(roombaService.navigate(request)).thenReturn(response);

    String jsonRequest =
        """
        {
            "roomSize": [5, 5],
            "coords": [1, 2],
            "patches": [
                {"x": 1, "y": 0},
                {"x": 2, "y": 2},
                {"x": 2, "y": 3}
            ],
            "instructions": "NNESEESWNWW"
        }
        """;

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/roomba/navigate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.coords[0]", is(1)))
        .andExpect(jsonPath("$.coords[1]", is(3)))
        .andExpect(jsonPath("$.patches", is(1)));
  }

  @Test
  void testOutOfBoundsWest() throws Exception {
    request =
        new RoombaRequest(
            List.of(5, 5),
            List.of(0, 0),
            List.of(new Patch(1, 0), new Patch(2, 2), new Patch(2, 3)),
            "WWWW");

    Mockito.when(roombaService.navigate(request))
        .thenThrow(new IllegalArgumentException("Out of bounds position"));

    String jsonRequest =
        """
        {
            "roomSize": [5, 5],
            "coords": [0, 0],
            "patches": [
                {"x": 1, "y": 0},
                {"x": 2, "y": 2},
                {"x": 2, "y": 3}
            ],
            "instructions": "WWWW"
        }
        """;

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/roomba/navigate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(jsonPath("$.error", is("Out of bounds position")));
  }

  @Test
  void testOutOfBoundsNorth() throws Exception {
    request =
        new RoombaRequest(
            List.of(5, 5),
            List.of(2, 4),
            List.of(new Patch(1, 0), new Patch(2, 2), new Patch(2, 3)),
            "NNNN");

    Mockito.when(roombaService.navigate(request))
        .thenThrow(new IllegalArgumentException("Out of bounds position"));

    String jsonRequest =
        """
        {
            "roomSize": [5, 5],
            "coords": [2, 4],
            "patches": [
                {"x": 1, "y": 0},
                {"x": 2, "y": 2},
                {"x": 2, "y": 3}
            ],
            "instructions": "NNNN"
        }
        """;

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/roomba/navigate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(jsonPath("$.error", is("Out of bounds position")));
  }

  @Test
  void testPatchesOnBorders() throws Exception {
    request =
        new RoombaRequest(
            List.of(5, 5),
            List.of(0, 0),
            List.of(new Patch(0, 0), new Patch(4, 4), new Patch(4, 0), new Patch(0, 4)),
            "NESEWNWN");

    response = new RoombaResponse(List.of(1, 1), 4);
    Mockito.when(roombaService.navigate(request)).thenReturn(response);

    String jsonRequest =
        """
        {
            "roomSize": [5, 5],
            "coords": [0, 0],
            "patches": [
                {"x": 0, "y": 0},
                {"x": 4, "y": 4},
                {"x": 4, "y": 0},
                {"x": 0, "y": 4}
            ],
            "instructions": "NESEWNWN"
        }
        """;

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/roomba/navigate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.coords[0]", is(1)))
        .andExpect(jsonPath("$.coords[1]", is(1)))
        .andExpect(jsonPath("$.patches", is(4))); // Number of patches cleaned
  }

  @Test
  void testNoDirtPatches() throws Exception {
    request = new RoombaRequest(List.of(5, 5), List.of(1, 1), List.of(), "NNEESSWW");

    response =
        new RoombaResponse(
            List.of(1, 1), // final coordinates
            0 // 0 patches cleaned
            );

    Mockito.when(roombaService.navigate(request)).thenReturn(response);

    String jsonRequest =
        """
        {
            "roomSize": [5, 5],
            "coords": [1, 1],
            "patches": [],
            "instructions": "NNEESSWW"
        }
        """;

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/roomba/navigate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.coords[0]", is(1)))
        .andExpect(jsonPath("$.coords[1]", is(1)))
        .andExpect(jsonPath("$.patches", is(0))); // 0 patches cleaned
  }
}
