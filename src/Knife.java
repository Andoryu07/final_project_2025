public class Knife extends Weapon {
    public Knife() {
        super("Knife", "Sharp blade for melee combat", 25, -1); // -1 = infinite use

    }

    @Override
    public void use(Player player) {
        System.out.println("You slash with the knife!");
    }
}