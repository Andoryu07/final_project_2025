public class CombatSystem {
    public static void fight(Player player, Enemy enemy) {
        System.out.println("\n🔴 Fight between " + player.getName() + " and " + enemy.getName() + "begins!\n");

        while (player.getHealth() > 0 && enemy.getHealth() > 0) {

            if (player.getEquippedWeapon() != null && player.getEquippedWeapon().hasAmmo()) {
                player.attack(enemy);
            } else {
                System.out.println("You have no ammo left! Try to run or use a different weapon.");
            }


            if (enemy.getHealth() <= 0) {
                System.out.println("\n✅ " + enemy.getName() + " has been defeated!\n");
                return;
            }


            enemy.attack(player);


            if (player.getHealth() <= 0) {
                System.out.println("\n💀 " + player.getName() + " had died...\n");
                return;
            }
        }
    }

}
