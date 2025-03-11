public class GearPiece extends Item{
    public GearPiece() {
        super("Gear piece", "Part used to repair a certain mechanism");
    }

    @Override
    public void use(Player player) {
        System.out.println("You have used the gear piece to repair the machine");
    }
}
