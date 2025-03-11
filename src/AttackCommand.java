public class AttackCommand implements Command {
    private Player player;
    private Enemy enemy;
    public AttackCommand(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
    }
    @Override
    public void execute() {
        player.attack(enemy);
    }


}
