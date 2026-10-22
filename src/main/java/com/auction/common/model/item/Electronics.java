
class Electronics extends com.auction.model.item.Item {
    public Electronics(String name,String description){
        super(name,description);
    }

    @Override
    public String getCategory() {
        return "Electronics";
    }
}