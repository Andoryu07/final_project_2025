import java.io.Serializable;

/**
 * Class used to implement the user/player, his fields, values and behavior, choices
 */
public class Player extends Character implements Serializable {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * Inventory instance
     */
    private Inventory inventory;
    /**
     * Weapon instance storing the equipped weapon
     */
    private Weapon equippedWeapon;
    /**
     * Boolean to decide whether the player is using the block option(in fight) or not
     */
    private boolean isBlocking = false;
    /**
     * Boolean to decide whether the player is currently in a fight or not
     */
    private boolean isFighting = false;
    /**
     * World instance
     */
    private World world;
    /**
     * X and Y coordinates on the map
     */
    private double x, y;
    /**
     * String of the current room name
     */
    private String currentRoomName;
    private double speedX = 0;
    private double speedY = 0;
    private boolean movementEnabled = true;
    private boolean isTransitioning = false;
    private double walkCyclePosition = 0;
    private final double WALK_CYCLE_SPEED = 0.1;
    private double maxStamina = 100.0;
    private double currentStamina = 100.0;
    private boolean isSprinting = false;
    private long lastSprintTime = 0;
    private final double STAMINA_DRAIN_RATE = 20.0; // 20% per second (5s to drain fully)
    private final double STAMINA_RECHARGE_RATE = 10.0; // 10% per second (10s to recharge fully)
    private final double STAMINA_RECHARGE_DELAY = 1.0;// 1 second delay before recharge starts
    private final double SPRINT_SPEED_MULTIPLIER = 2.0;
    public void updateWalkCycle(boolean isMoving) {
        if (isMoving) {
            walkCyclePosition += WALK_CYCLE_SPEED;
            if (walkCyclePosition >= 2 * Math.PI) {
                walkCyclePosition = 0;
            }
        } else {
            walkCyclePosition = 0;
        }
    }

    public double getWalkCyclePosition() {
        return walkCyclePosition;
    }
    /**
     * Constructor, contains super from Character
     * @param name name of the Player
     * @param health health of the Player
     * @param world World, in which the Player is in
     */
    public Player(String name, int health, World world, String startingRoomName) {
        super(name, health, world,startingRoomName);
        this.inventory = new Inventory(10);

    }

    /**
     * Method for player to equip the weapon, if he owns at least one
     * @param weapon Weapon the player wants to equip
     */
    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
        if (weapon != null) {
            System.out.println("Equipped " + weapon.getName());
            if (weapon.isInfiniteUse()) {
                System.out.println("This weapon has unlimited uses");
            } else {
                System.out.printf("Ammo: %d/%d%n",
                        weapon.getCurrentAmmo(), weapon.getMaxAmmo());
            }
        } else {
            System.out.println("No weapon equipped");
        }
    }

    /**
     * Method for player to attempt and attack the enemy in a fight, only works if player has a weapon equipped
     * @param enemy enemy player is attempting to attack
     */
    public void attack(Enemy enemy) {
        if (equippedWeapon == null) {
            System.out.println("You have no weapon equipped");
        } else {
            // Remove the ammo check here - let the weapon handle it
            System.out.printf("You attack with %s for %d damage!%n",
                    equippedWeapon.getName(), equippedWeapon.getDamage());
            enemy.takeDamage(equippedWeapon.getDamage());
            equippedWeapon.use(this);
        }
    }

    /**
     * Method used to heal the player when he chooses to in a fight
     * @param amount amount of health player will heal from this action
     */
    public void heal(int amount) {
        this.health += amount;
        System.out.println(name + " healed himself for " + amount + " HP. Current health: " + health);
    }

    /**
     * Method used for a player to pick up and add an item into his inventory
     * @param item which item is the player attempting to pick up
     */
    public void pickUpItem(Item item) {
        if (inventory.addItem(item)) {
            System.out.println("You've picked up: " + item.getName());
            getCurrentRoom().removeItem(item);
        } else {
            System.out.println("Inventory is full! You can't pick up the item: " + item.getName());
        }
    }

    /**
     * Checks whether a certain item is in the player's inventory
     * @param itemName name of the item attempting to find
     * @return true/false based on whether player's inventory contains the said item
     */
    public boolean hasItem(String itemName) {
        return inventory.getItems().stream()
                .anyMatch(item -> item.getName().equalsIgnoreCase(itemName));
    }

    /**
     * Method used to remove an item from the inventory, if it's there
     * @param itemName name of the item we're attempting to remove
     * @return true/false based on whether the removal of the item was successful or not
     */
    public boolean removeItem(String itemName) {
        Item item = findItemInInventory(itemName);
        if (item != null) {
            inventory.removeItem(item);
            return true;
        }
        return false;
    }

    /**
     * Method used to decrease player's health by a certain amount
     * @param damage amount of health to decrease by
     */
    public void takeDamage(int damage) {
        if (isBlocking) {
            damage = (int) (damage * 0.5); // Reduce damage by 50%
            isBlocking = false; // Block only lasts one turn
        }
        this.health -= damage;
        if (this.health < 0) this.health = 0;
    }

    /**
     * Setter for 'health'
     * @param health what to set 'health' to
     */
    public void setHealth(int health) {
        this.health = Math.max(0, health); // Ensure health doesn't go negative
    }

    /**
     * Getter for 'equippedWeapon'
     * @return value of 'equippedWeapon'
     */
    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    /**
     * Getter for 'inventory'
     * @return list 'inventory'
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * method used to find a certain item in player's inventory
     * @param itemName name of the item we're attempting to find
     * @return the item we were looking for if it was found, null if not
     */
    public Item findItemInInventory(String itemName) {
        return inventory.findItem(itemName);
    }

    /**
     * Setter for 'isBlocking'
     * @param blocking sets the value of 'isBlocking'
     */
    public void setBlocking(boolean blocking) {
        this.isBlocking = blocking;
    }

    /**
     * Setter for 'isFighting'
     * @param fighting sets the value of 'isFighting'
     */
    public void setFighting(boolean fighting) {
        isFighting = fighting;
    }

    /**
     * Getter for 'isFighting'
     * @return value of 'isFighting'
     */
    public boolean isFighting() {
        return isFighting;
    }

    /**
     * Getter for 'isBlocking'
     * @return value of 'isBlocking'
     */
    public boolean isBlocking() {
        return isBlocking;
    }

    /**
     * Getter for World
     * @return world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Setter for World
     * @param world what to set the world to
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Method used to set player's current room
     * @param room what room to set player's current room to
     */
    public void setCurrentRoom(Room room) {
        this.currentRoomName = room.getName();
        if (world != null) {
            world.setCurrentRoom(room);
        }
    }
    public void setCurrentRoom(String roomName) {
        this.currentRoomName = roomName;
    }

    public String getCurrentRoomName() {
        return currentRoomName;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    /**
     * Getter for X
     * @return value of X
     */
    public double getX() {
        return x;
    }

    /**
     * Getter for Y
     * @return value of Y
     */
    public double getY() {
        return y;
    }
    public void setSpeed(double speedX, double speedY) {
        this.speedX = speedX;
        this.speedY = speedY;
    }

    public double getSpeedX() {
        return speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    /**
     * Setter for X and Y
     * @param x what to set X to
     * @param y what to set Y to
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setTransitioning(boolean transitioning) {
        this.isTransitioning = transitioning;
        if (transitioning) {
            this.speedX = 0;
            this.speedY = 0;
        }
    }
    public void updateStamina(double deltaTimeSeconds, boolean isMoving) {
        if (isSprinting && movementEnabled && isMoving) {
            // Only drain stamina when actually moving while sprinting
            currentStamina -= STAMINA_DRAIN_RATE * deltaTimeSeconds;
            if (currentStamina <= 0) {
                currentStamina = 0;
                isSprinting = false;
            }
            lastSprintTime = System.currentTimeMillis();
        } else {
            // Recharge stamina after a delay
            long timeSinceLastSprintMillis = System.currentTimeMillis() - lastSprintTime;
            double timeSinceLastSprintSeconds = timeSinceLastSprintMillis / 1000.0;

            if (timeSinceLastSprintSeconds > STAMINA_RECHARGE_DELAY) {
                currentStamina += STAMINA_RECHARGE_RATE * deltaTimeSeconds;
                if (currentStamina > maxStamina) {
                    currentStamina = maxStamina;
                }
            }
        }
    }
    public void updatePosition() {
        if (!isTransitioning) {
            this.x += speedX;
            this.y += speedY;
        }
    }
    public void setMovementEnabled(boolean enabled) {
        this.movementEnabled = enabled;
        if (!enabled) {
            setSpeed(0, 0);
        }
    }
    public boolean isTransitioning() {
        return isTransitioning;
    }

    public boolean isMovementEnabled() {
        return movementEnabled;
    }
    public double getCurrentStamina() {
        return currentStamina;
    }

    public double getMaxStamina() {
        return maxStamina;
    }

    public boolean isSprinting() {
        return isSprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.isSprinting = sprinting;
        if (sprinting) {
            this.lastSprintTime = System.currentTimeMillis();
        }
    }

    public double getSPRINT_SPEED_MULTIPLIER() {
        return SPRINT_SPEED_MULTIPLIER;
    }
}
