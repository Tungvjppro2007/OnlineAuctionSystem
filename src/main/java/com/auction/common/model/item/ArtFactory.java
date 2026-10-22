abstract class ArtFactory extends com.auction.model.item.ItemFactory {
    @Override
    public com.auction.model.item.Item creatItem(String name, String description) {
        return new Art(name,description);
    }
}