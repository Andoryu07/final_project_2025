public class LoreItem extends Item{
    public LoreItem(String name, String description) {
        super(name, description);
    }

    @Override
    public void use(Player player) {
        System.out.println(description);
    }
}
