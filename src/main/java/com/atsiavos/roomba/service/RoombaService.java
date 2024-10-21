package com.atsiavos.roomba.service;
import com.atsiavos.roomba.model.RoombaRequest;
import com.atsiavos.roomba.model.RoombaResponse;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoombaService {
    public RoombaResponse navigate(RoombaRequest request) {
        // Initialize coordinates
        int x = request.getCoords().get(0);
        int y = request.getCoords().get(1);
        int roomWidth = request.getRoomSize().get(0);
        int roomHeight = request.getRoomSize().get(1);

        Set<List<Integer>> dirtPatches = new HashSet<>(request.getPatches());
        int patchesCleaned = 0;

        for (char command : request.getInstructions().toCharArray()) {
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
                default:
                    // Ignore any letter
                    break;
            }

            //  check stay within valid boundaries
            if (x < 0 || x >= roomWidth || y < 0 || y >= roomHeight) {
                // Return error message when robot goes out of bounds
                throw new IllegalArgumentException("Out of bounds position: (" + x + ", " + y + ")");
            }

            // the Roomba is on a dirt patch then clean it
            List<Integer> currentPosition = List.of(x, y);
            if (dirtPatches.contains(currentPosition)) {
                patchesCleaned++;
                dirtPatches.remove(currentPosition);
            }
        }

        // Return the final coordinates and the number of cleaned patches
        return new RoombaResponse(List.of(x, y), patchesCleaned);
    }
}

