module online.auction.system {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.auction.client to javafx.fxml;
    opens com.auction.client.controllers to javafx.fxml;

    exports com.auction.client;
}