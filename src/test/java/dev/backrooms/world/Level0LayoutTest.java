package dev.backrooms.world;

import org.junit.jupiter.api.Test;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class Level0LayoutTest {
    @Test
    void everyRoomHasTwoBlockDoorwaysOnAllSharedEdges() {
        for (int roomX = -16; roomX <= 16; roomX++) {
            for (int roomZ = -16; roomZ <= 16; roomZ++) {
                int x = roomX * 8;
                int z = roomZ * 8;
                int verticalOpen = 0;
                int horizontalOpen = 0;
                for (int offset = 0; offset < 8; offset++) {
                    if (!Level0Layout.isWall(x, z + offset)) verticalOpen++;
                    if (!Level0Layout.isWall(x + offset, z)) horizontalOpen++;
                }
                assertEquals(2, verticalOpen);
                assertEquals(2, horizontalOpen);
                assertTrue(Level0Layout.isWall(x, z));
                assertFalse(Level0Layout.isWall(x + 4, z + 4));
                assertTrue(Level0Layout.isLight(x + 4, z + 4));
            }
        }
    }

    @Test
    void chunkTraversalMatchesWorldCoordinatesAndLightsNeverOccupyWalls() {
        for (int chunkX = -8; chunkX <= 8; chunkX++) {
            for (int chunkZ = -8; chunkZ <= 8; chunkZ++) {
                int lights = 0;
                for (int localX = 0; localX < 16; localX++) {
                    for (int localZ = 0; localZ < 16; localZ++) {
                        int x = chunkX * 16 + localX;
                        int z = chunkZ * 16 + localZ;
                        assertEquals(chunkX, Math.floorDiv(x, 16));
                        assertEquals(chunkZ, Math.floorDiv(z, 16));
                        if (Level0Layout.isLight(x, z)) {
                            lights++;
                            assertFalse(Level0Layout.isWall(x, z));
                        }
                    }
                }
                assertEquals(4, lights);
            }
        }
    }

    @Test
    void roomsAcrossPositiveAndNegativeChunkBoundariesAreConnected() {
        record Point(int x, int z) {}
        Set<Point> visited = new HashSet<>();
        ArrayDeque<Point> queue = new ArrayDeque<>();
        queue.add(new Point(4, 4));
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!queue.isEmpty()) {
            Point point = queue.remove();
            if (point.x < -32 || point.x > 31 || point.z < -32 || point.z > 31
                    || Level0Layout.isWall(point.x, point.z) || !visited.add(point)) continue;
            for (int[] direction : directions) {
                queue.add(new Point(point.x + direction[0], point.z + direction[1]));
            }
        }
        for (int x = -31; x <= 31; x++) {
            for (int z = -31; z <= 31; z++) {
                if (!Level0Layout.isWall(x, z)) assertTrue(visited.contains(new Point(x, z)));
            }
        }
    }

    @Test
    void doorPlacementVariesRatherThanRepeatingAnIdenticalRoom() {
        Set<String> patterns = new HashSet<>();
        for (int x = -16; x <= 16; x++) {
            StringBuilder pattern = new StringBuilder();
            for (int z = 0; z < 8; z++) pattern.append(Level0Layout.isWall(x * 8, z));
            patterns.add(pattern.toString());
        }
        assertEquals(3, patterns.size());
    }
}
