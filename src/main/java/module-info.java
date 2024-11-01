module com.example.utpp1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.utpp1 to javafx.fxml;
    exports com.example.utpp1;
}