abstract class ItemFactory{
    public abstract com.auction.model.item.Item creatItem(String name, String description);

    public com.auction.model.item.Item prepareForAI(String name, String description){
        com.auction.model.item.Item item = creatItem(name, description);
        System.out.println("Sẵn sàng đấu giá sản phẩm: " + item.getName());
        return item;
    }
}