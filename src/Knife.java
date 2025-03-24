public class Knife extends Weapon {
    public Knife() {
        super("Knife", "Weaker, but unlimited use", 10, -1);
    }

    @Override
    public void use(Player player) {
        System.out.println("Knife hit");
    }
}
