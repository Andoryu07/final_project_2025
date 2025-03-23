public class SearchSpot {
    private String name;
    private boolean searched;
    private Item hiddenItem;

    public SearchSpot(String name, Item hiddenItem) {
        this.name = name;
        this.hiddenItem = hiddenItem;
        this.searched = false;
    }

    public String getName() {
        return name;
    }

    public boolean isSearched() {
        return searched;
    }

    public Item search() {
        if (!searched) {
            searched = true;
            return hiddenItem;
        }
        return null;
    }
}


