package dev.backrooms.world;

/** Stateless world-coordinate layout: neighboring chunks agree, including at negative coordinates. */
public final class Level0Layout {
    public static final int FLOOR_Y = 0;
    public static final int CEILING_Y = 4;
    public static final int HEIGHT = 16;
    public static final int ROOM_SIZE = 8;

    private Level0Layout() {}

    public static boolean isWall(int x, int z) {
        int localX = Math.floorMod(x, ROOM_SIZE);
        int localZ = Math.floorMod(z, ROOM_SIZE);
        int roomX = Math.floorDiv(x, ROOM_SIZE);
        int roomZ = Math.floorDiv(z, ROOM_SIZE);
        if (localX == 0 && localZ == 0) {
            return true;
        }
        // Each shared room edge has a two-block doorway, so rooms cannot be sealed off.
        if (localX == 0) {
            int door = doorOffset(roomX, roomZ, 0x51EDL);
            return localZ < door || localZ > door + 1;
        }
        if (localZ == 0) {
            int door = doorOffset(roomX, roomZ, 0xBEEFL);
            return localX < door || localX > door + 1;
        }
        return false;
    }

    public static boolean isLight(int x, int z) {
        return Math.floorMod(x, ROOM_SIZE) == 4 && Math.floorMod(z, ROOM_SIZE) == 4;
    }

    private static int doorOffset(int x, int z, long salt) {
        long hash = x * 341873128712L + z * 132897987541L + salt;
        hash = (hash ^ (hash >>> 33)) * 0xff51afd7ed558ccdL;
        return 2 + (int) Math.floorMod(hash ^ (hash >>> 33), 3L);
    }
}
