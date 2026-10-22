class Vehicle extends com.auction.model.item.Item {
    public Vehicle(String name,String description){
        super(name,description);
    }
    @Override
    public String getCategory() {
        return "Vehicle";
    }
}

