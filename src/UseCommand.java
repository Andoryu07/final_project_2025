public class UseCommand implements Command{

        private Player player;
        private Item item;

        public UseCommand(Player player, Item item) {
            this.player = player;
            this.item = item;
        }

        @Override
        public void execute() {
            item.use(player);
        }


}
