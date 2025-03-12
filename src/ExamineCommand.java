public class ExamineCommand implements Command {
    private Player player;
    private Item item;

    public ExamineCommand(Player player, Item item) {
        this.player = player;
        this.item = item;
    }

    @Override
    public void execute() {
        System.out.println("🔍 " + item.getName() + ": " + item.getDescription());
    }
}
