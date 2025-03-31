import java.io.Serializable;
import java.util.*;

public abstract class Enemy extends Character implements Serializable {
    private static final long serialVersionUID = 1L;
    protected Map<String, Integer> attacks;
    protected int distanceFromPlayer;

    public Enemy(String name, int health, Room currentRoom) {
        super(name, Math.max(1,health), currentRoom);//Ensures the enemy has atleast 1 health
        this.attacks = new HashMap<>();
        initializeAttacks();
    }

    protected abstract void initializeAttacks();

    public AttackResult performRandomAttack() {
        if (attacks.isEmpty()) {
            System.out.println(name + " has no attacks!");
            return new AttackResult("misses", 0);
        }
        List<String> attackNames = new ArrayList<>(attacks.keySet());
        String randomAttack = attackNames.get(new Random().nextInt(attackNames.size()));
        int damage = attacks.get(randomAttack);
        return new AttackResult(randomAttack, damage);
    }

    private String getAttackNameForDamage(int damage) {
        for (Map.Entry<String, Integer> entry : attacks.entrySet()) {
            if (entry.getValue() == damage) {
                return entry.getKey();
            }
        }
        return "attack"; // Fallback if no matching attack name found
    }
    public int getAttackDamage() {
        if (attacks.isEmpty()) {
            return 0; // Default damage if no attacks exist
        }
        List<String> attackNames = new ArrayList<>(attacks.keySet());
        String randomAttack = attackNames.get(new Random().nextInt(attackNames.size()));
        return attacks.get(randomAttack);
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
        this.health = 120;
    }
}
