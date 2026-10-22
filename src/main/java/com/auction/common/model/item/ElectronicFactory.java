abstract class ElectronicFactory extends ItemFactory{
    @Override
    public Item creatItem(String name, String description) {
        return new Electronics(name,description);
    }
}