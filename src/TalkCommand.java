public class TalkCommand implements Command {
    private Player player;
    private Character npc;
    public TalkCommand(Player player, Character npc) {
        this.player = player;
        this.npc = npc;
    }

    @Override
    public void execute() {
        System.out.println(player.getName() + " is talking to " + npc.getName());
        // Zde můžeme přidat logiku pro rozhovory (např. skripty)
    }
}
