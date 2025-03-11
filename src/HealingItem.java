public class HealingItem extends Item {
    private int healAmount;
    public HealingItem(String name, String description, int healAmount) {
        super(name, description);
        this.healAmount = healAmount;
    }
    @Override
    public void use(Player player) {
        System.out.println("Item " + name + " has been used, healed " + healAmount + " HP.");
        player.heal(healAmount);
    }
}
