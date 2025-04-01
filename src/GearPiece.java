import java.io.Serializable;
/**
 * Class used to create and implement the GearPiece item, its values and fields
 */
public class GearPiece extends KeyItem implements Serializable {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * String, storing the FILE name of the gear piece (GEAR_PIECE_1,...)
     */
    private final String internalName; // For game logic (GEAR_PIECE_1)

    /**
     * Constructor, contains super from KeyItem class
     * @param internalName FILE name of the gear piece
     */
    public GearPiece(String internalName) {
        super(convertToDisplayName(internalName), "A mechanical gear piece for unlocking doors");
        this.internalName = internalName;
    }

    /**
     * Method used to convert the name of the gear piece into a more readable form in console
     * @param internalName FILE name of the gear piece
     * @return formated name of the gear piece
     */
    private static String convertToDisplayName(String internalName) {
        // Converts "GEAR_PIECE_1" to "Gear Piece 1"
        return internalName.replace("_", " ").replace("GEAR", "Gear").replace("PIECE", "Piece");
    }

    /**
     * Getter for 'internalName'
     * @return value of 'internalName'
     */
    public String getInternalName() {
        return internalName;
    }
}
