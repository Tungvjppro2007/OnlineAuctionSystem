abstract class VehicleFactory extends ItemFactory{
    @Override
    public Item creatItem(String name, String description) {
        return new Vehicle(name,description);
    }
}

