
public class Pistol extends Weapon {
    public Pistol() {
        super("Pistol", "Medium power, uses ammo", 25, 6);
    }

    @Override
    public void use(Player player) {
        super.use(player);
        System.out.println("Bang! Pistol shot fired.");
    }
}

