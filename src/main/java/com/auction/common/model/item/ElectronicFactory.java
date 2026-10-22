abstract class ElectronicFactory extends ItemFactory{
    @Override
    public com.auction.model.item.Item creatItem(String name, String description) {
        return new com.auction.model.item.Electronics(name,description);
    }
}