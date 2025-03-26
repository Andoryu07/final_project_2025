public class InsertGearCommand implements Command {
    @Override
    public void execute(Player player) {
        Inventory inventory = player.getInventory();  // Get the player's inventory

        // Check if the player has any GearPieces
        boolean hasGearPiece = false;
        Item gearPiece = null;

        for (Item item : inventory.getItems()) {
            if (item.getName().toLowerCase().contains("gearpiece")) {  // Assuming they are named like "GearPiece1"
                gearPiece = item;
                hasGearPiece = true;
                break;  // Exit loop when first gear piece is found
            }
        }

        if (!hasGearPiece) {
            System.out.println("You don't have any GearPieces.");
            return;
        }

        // Remove the gear piece from inventory
        inventory.removeItem(gearPiece);
        System.out.println("Inserted " + gearPiece.getName() + " into the mechanism.");

        // TODO: Check if all 4 GearPieces have been inserted, then unlock the door
    }
}
