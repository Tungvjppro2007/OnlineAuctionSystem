abstract class VehicleFactory extends ItemFactory{
    @Override
    public com.auction.model.item.Item creatItem(String name, String description) {
        return new Vehicle(name,description);
    }
}

