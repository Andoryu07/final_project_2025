public class Hint extends Item {
    public Hint(String name, String description) {
        super(name, description);
    }

    @Override
    public void use(Player player) {
        System.out.println("📜 Hint: " + description);
    }
}

