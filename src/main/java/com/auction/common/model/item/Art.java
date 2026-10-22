class Art extends com.auction.model.item.Item {
    public Art(String name,String description){
        super(name,description);
    }
    @Override
    public String getCategory() {
        return "Art";
    }
}