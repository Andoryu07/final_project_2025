public class Enemy extends Character {
    private int damage;

    public Enemy(String name, int health, int damage, Room startRoom) {
        super(name, health, startRoom);
        this.damage = damage;
    }

    public void attack(Player player) {
        System.out.println(name + " attacked and dealt " + damage + " damage!");
        player.takeDamage(damage);
    }

}
