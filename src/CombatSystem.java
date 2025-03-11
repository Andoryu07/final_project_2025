public class CombatSystem {
    public static void fight(Player player, Enemy enemy) {
        System.out.println("\n🔴 Boj začíná mezi " + player.getName() + " a " + enemy.getName() + "!\n");

        while (player.getHealth() > 0 && enemy.getHealth() > 0) {
            // Hráč útočí
            if (player.getEquippedWeapon() != null && player.getEquippedWeapon().hasAmmo()) {
                player.attack(enemy);
            } else {
                System.out.println("Nemáš munici! Zkus utéct nebo použít jinou zbraň.");
            }

            // Kontrola, zda je nepřítel poražen
            if (enemy.getHealth() <= 0) {
                System.out.println("\n✅ " + enemy.getName() + " byl poražen!\n");
                return;
            }

            // Nepřítel útočí
            enemy.attack(player);

            // Kontrola, zda hráč přežil
            if (player.getHealth() <= 0) {
                System.out.println("\n💀 " + player.getName() + " byl zabit...\n");
                return;
            }
        }
    }

}
