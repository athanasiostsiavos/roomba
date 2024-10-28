package com.atsiavos.roomba;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.atsiavos.roomba.model.Patch;
import com.atsiavos.roomba.model.RoombaRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RoombaApplicationTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  // Test valid Roomba navigation
  @Test
  void testNavigateHooverIntegration() throws Exception {
    // Create the request payload for valid movement
    RoombaRequest request =
        new RoombaRequest(
            List.of(5, 5), // roomSize
            List.of(1, 2), // initial coordinates
            List.of(new Patch(1, 0), new Patch(2, 2), new Patch(2, 3)), // list of dirty patches
            "NNESEESWNWW" // driving instructions
            );

    String jsonRequest = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            post("/roomba/navigate").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.coords[0]", is(1)))
        .andExpect(jsonPath("$.coords[1]", is(3)))
        .andExpect(jsonPath("$.patches", is(1)));
  }

  @Test
  void testOutOfBoundsError() throws Exception {
    // Create the request payload for out-of-bounds scenario
    RoombaRequest request =
        new RoombaRequest(
            List.of(5, 5), // roomSize
            List.of(1, 1), // initial coordinates
            List.of(), // No dirt patches
            "NNNNNNNNNN" // Invalid: moves out of bounds
            );

    // Convert the RoombaRequest to JSON
    String jsonRequest = objectMapper.writeValueAsString(request);

    // Perform the POST request and expect a Bad Request with an error message
    mockMvc
        .perform(
            post("/roomba/navigate").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", containsString("Out of bounds position")));
  }
}
