/**
 * Class used to figure out, which attack the enemy had used
 */
public class AttackResult {
    /**
     * Name of the attack used by the enemy
     */
    public final String attackName;
    /**
     * Damage dealt by the attack used by the enemy
     */
    public final int damage;

    /**
     * Constructor
     * @param attackName Name of the attack used by the enemy
     * @param damage Damage dealt by the attack used by the enemy
     */
    public AttackResult(String attackName, int damage) {
        this.attackName = attackName;
        this.damage = damage;
    }
}
