package com.atsiavos.roomba.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
public class RoombaControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private RoombaService roombaService;

  private RoombaRequest request;
  private RoombaResponse response;

  @BeforeEach
  public void setup() {

    request =
        new RoombaRequest(
            List.of(5, 5), // roomSize
            List.of(1, 2), // initial coords
            List.of(List.of(1, 0), List.of(2, 2), List.of(2, 3)), // patches
            "NNESEESWNWW" // instructions
            );

    response =
        new RoombaResponse(
            List.of(1, 3), // final coords
            1 // number of patches cleaned
            );

    Mockito.when(roombaService.navigate(any(RoombaRequest.class))).thenReturn(response);
  }

  @Test
  public void testNavigateHoover() throws Exception {

    String jsonRequest =
        "{\n"
            + "\"roomSize\": [5, 5],\n"
            + "\"coords\": [1, 2],\n"
            + "\"patches\": [[1, 0], [2, 2], [2, 3]],\n"
            + "\"instructions\": \"NNESEESWNWW\"\n"
            + "}";

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
  public void testOutOfBoundsWest() throws Exception {

    Mockito.when(roombaService.navigate(any(RoombaRequest.class)))
        .thenThrow(new IllegalArgumentException("Out of bounds position"));

    String jsonRequest =
        "{\n"
            + "\"roomSize\": [5, 5],\n"
            + "\"coords\": [0, 0],\n"
            + "\"patches\": [[1, 0], [2, 2], [2, 3]],\n"
            + "\"instructions\": \"WWWW\"\n"
            + "}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/roomba/navigate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(jsonPath("$.error", is("Out of bounds position")));
  }

  // Out of bounds to the North
  @Test
  public void testOutOfBoundsNorth() throws Exception {

    Mockito.when(roombaService.navigate(any(RoombaRequest.class)))
        .thenThrow(new IllegalArgumentException("Out of bounds position"));

    String jsonRequest =
        "{\n"
            + "\"roomSize\": [5, 5],\n"
            + "\"coords\": [2, 4],\n"
            + "\"patches\": [[1, 0], [2, 2], [2, 3]],\n"
            + "\"instructions\": \"NNNN\"\n"
            + "}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/roomba/navigate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(jsonPath("$.error", is("Out of bounds position")));
  }

  // Room borders
  @Test
  public void testPatchesOnBorders() throws Exception {

    request =
        new RoombaRequest(
            List.of(5, 5),
            List.of(0, 0),
            List.of(List.of(0, 0), List.of(4, 4), List.of(4, 0), List.of(0, 4)),
            "NESEWNWN");

    response = new RoombaResponse(List.of(1, 1), 4);

    Mockito.when(roombaService.navigate(any(RoombaRequest.class))).thenReturn(response);

    String jsonRequest =
        "{\n"
            + "\"roomSize\": [5, 5],\n"
            + "\"coords\": [0, 0],\n"
            + "\"patches\": [[0, 0], [4, 4], [4, 0], [0, 4]],\n"
            + "\"instructions\": \"NESEWNWN\"\n"
            + "}";

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
  public void testNoDirtPatches() throws Exception {
    // mock request and response for no dirt patches
    request = new RoombaRequest(List.of(5, 5), List.of(1, 1), List.of(), "NNEESSWW");

    response =
        new RoombaResponse(
            List.of(1, 1), // final coordinates
            0 // 0 patches cleaned
            );

    Mockito.when(roombaService.navigate(any(RoombaRequest.class))).thenReturn(response);

    String jsonRequest =
        "{\n"
            + "\"roomSize\": [5, 5],\n"
            + "\"coords\": [1, 1],\n"
            + "\"patches\": [],\n"
            + "\"instructions\": \"NNEESSWW\"\n"
            + "}";

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
