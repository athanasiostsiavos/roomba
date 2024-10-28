package com.atsiavos.roomba.service;

import com.atsiavos.roomba.model.Patch;
import com.atsiavos.roomba.model.RoombaRequest;
import com.atsiavos.roomba.model.RoombaResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class RoombaService {
  public RoombaResponse navigate(RoombaRequest request) {
    // Initialize coordinates
    int x = request.coords().get(0);
    int y = request.coords().get(1);
    int roomWidth = request.roomSize().get(0);
    int roomHeight = request.roomSize().get(1);

    Set<Patch> dirtPatches = new HashSet<>(request.patches());
    int patchesCleaned = 0;

    for (char command : request.instructions().toCharArray()) {
      // Instructions  N North, S South and so on..
      switch (command) {
        case 'N':
          y++;
          break;
        case 'S':
          y--;
          break;
        case 'E':
          x++;
          break;
        case 'W':
          x--;
          break;
        default: // Ignore other letters
          break;
      }

      //  check stay within valid boundaries
      if (x < 0 || x >= roomWidth || y < 0 || y >= roomHeight) {
        // Return error message when robot goes out of bounds
        throw new IllegalArgumentException("Out of bounds position: (" + x + ", " + y + ")");
      }

      // the Roomba is on a dirt patch then clean it
      Patch currentPosition = new Patch(x, y);
      if (dirtPatches.contains(currentPosition)) {
        patchesCleaned++;
        dirtPatches.remove(currentPosition);
      }
    }

    // Return the final coordinates and the number of cleaned patches
    return new RoombaResponse(List.of(x, y), patchesCleaned);
  }
}
