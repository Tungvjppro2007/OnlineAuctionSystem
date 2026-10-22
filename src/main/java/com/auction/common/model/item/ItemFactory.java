abstract class ItemFactory{
    public abstract Item creatItem(String name, String description);

    public Item prepareForAI(String name, String description){
        Item item = creatItem(name, description);
        System.out.println("Sẵn sàng đấu giá sản phẩm: " + item.getName());
        return item;
    }
}