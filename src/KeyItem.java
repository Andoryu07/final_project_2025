public class KeyItem extends Item {
    public KeyItem(String name, String description) {
        super(name, description);
    }

    @Override
    public void use(Player player) {
        System.out.println("You have used " + name + ".");
    }
}
