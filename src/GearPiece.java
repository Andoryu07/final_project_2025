public class GearPiece extends KeyItem {
    private final String internalName; // For game logic (GEAR_PIECE_1)

    public GearPiece(String internalName) {
        super(convertToDisplayName(internalName), "A mechanical gear piece for unlocking doors");
        this.internalName = internalName;
    }

    private static String convertToDisplayName(String internalName) {
        // Converts "GEAR_PIECE_1" to "Gear Piece 1"
        return internalName.replace("_", " ").replace("GEAR", "Gear").replace("PIECE", "Piece");
    }

    public String getInternalName() {
        return internalName;
    }
}
