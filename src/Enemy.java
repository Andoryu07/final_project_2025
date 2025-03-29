import java.util.*;

public abstract class Enemy extends Character {
    protected Map<String, Integer> attacks;
    protected int distanceFromPlayer;

    public Enemy(String name, int health, Room currentRoom) {
        super(name, Math.max(1,health), currentRoom);//Ensures the enemy has atleast 1 health
        this.attacks = new HashMap<>();
        initializeAttacks();
    }

    protected abstract void initializeAttacks();

    public void performRandomAttack(Player player) {
        if (attacks.isEmpty()) {
            System.out.println(name + " has no attacks!");
            return;
        }
        List<String> attackNames = new ArrayList<>(attacks.keySet());
        String randomAttack = attackNames.get(new Random().nextInt(attackNames.size()));
        int damage = attacks.get(randomAttack);

        System.out.println(name + " uses " + randomAttack + " for " + damage + " damage!");
        player.takeDamage(damage);
    }

    public void moveCloser() {
        if (distanceFromPlayer > 0) {
            distanceFromPlayer--;
        }
    }

    public boolean isInCombatRange() {
        return distanceFromPlayer == 0;
    }

    public void retreat() {
        distanceFromPlayer = 3;
    }
}
