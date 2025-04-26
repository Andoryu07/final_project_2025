public class EquipCommand implements Command {
    private final Player player;
    private final Weapon weapon;

    public EquipCommand(Player player, Weapon weapon) {
        this.player = player;
        this.weapon = weapon;
    }

    @Override
    public void execute() {
        player.equipWeapon(weapon);
    }
}
