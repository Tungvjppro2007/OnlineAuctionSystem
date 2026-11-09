module online.auction.system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.auction.client to javafx.fxml;
    opens com.auction.client.controllers to javafx.fxml;

    exports com.auction.client;
    exports com.auction.common.util;
}