public class Ammo extends Item{
    protected int amount;

    public Ammo(String name, String description, int amount) {
        super(name, description);
        this.amount = amount;
    }

    public int getAmount() { return amount; }
    public void addAmmo(int amount) { this.amount += amount; }
    public void use(Player player) {
        System.out.println("You can't use this item like that...");
    }
}
