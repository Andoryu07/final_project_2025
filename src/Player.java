public class Player extends Character{
    private Inventory inventory;
    private Weapon equippedWeapon;

    public Player(String name, int health, Room startRoom) {
        super(name, health, startRoom);
        this.inventory = new Inventory(5);
    }

    public void attack(Enemy enemy) {
        if (equippedWeapon != null && equippedWeapon.hasAmmo()) {
            enemy.takeDamage(equippedWeapon.getDamage());
            equippedWeapon.use(this);
            System.out.println("You attacked" + enemy.getName() + " with " + equippedWeapon.getName());
        } else {
            System.out.println("You have no ammo left!");
        }
    }
    public void heal(int amount) {
        this.health += amount;
        System.out.println(name + " healed himself for " + amount + " HP. Current health: " + health);
    }
    public void pickUpItem(Item item) {
        if (inventory.addItem(item)) {
            System.out.println("You've picked up: " + item.getName());
            currentRoom.removeItem(item); // Removal of the item from the room
        } else {
            System.out.println("Inventory is full! You can't pick up the item: " + item.getName());
        }
    }

    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
    }

    public Weapon getEquippedWeapon() { return equippedWeapon; }
}
