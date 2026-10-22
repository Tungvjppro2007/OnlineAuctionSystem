abstract class ArtFactory extends ItemFactory {
    @Override
    public Item creatItem(String name, String description) {
        return new Art(name,description);
    }
}