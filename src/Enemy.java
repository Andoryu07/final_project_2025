import java.io.Serializable;
import java.util.*;

/**
 * Class used to create and implement the enemies' behavior, values and methods
 */
public abstract class Enemy extends Character implements Serializable {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * Map containing all the enemy's attacks (Name,Damage)
     */
    protected Map<String, Integer> attacks;
    /**
     * int value to specify, how many rooms(moves) away from the player the enemy is(specifically used for Stalker)
     */
    protected int distanceFromPlayer;

    /**
     * Constructor
     * @param name Name of the enemy
     * @param health Health of the enemy
     * @param world Current world of the enemy
     */
    public Enemy(String name, int health, World world, String startingRoomName) {
        super(name, Math.max(1,health), world,startingRoomName);//Ensures the enemy has atleast 1 health
        this.attacks = new HashMap<>();
        initializeAttacks();
    }

    /**
     * Method used to initialize the enemy's attacks, abstract in order to be usable for different enemies
     */
    protected abstract void initializeAttacks();

    /**
     * Method used to make the enemy perform a random attack
     * @return the attack used by the enemy
     */
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

    /**
     * Method used to decrease the enemy's distance from the player by one, used when the player moves to a different room
     */
    public void moveCloser() {
        if (distanceFromPlayer > 0) {
            distanceFromPlayer--;
        }
    }

    /**
     * Method used to determine, whether a fight should start between enemy and player, true if the distance between player's and enemy's room is 0
     * @return boolean value based on whether the enemy and player are in the same room
     */
    public boolean isInCombatRange() {
        return distanceFromPlayer == 0;
    }

    /**
     * Method used after a fight between enemy and player, in the case of player coming out victorious
     */
    public void retreat() {
        distanceFromPlayer = 3;
        this.health = 120;
    }
}
