package UI.Costumer;

import Domain.LaundryItems.Item;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class ItemBox extends HBox {
    private Button button;
    private int itemID;

    public ItemBox(Item item) {
        this.getStylesheets().add("Foundation/Resources/CSS/Customer.css");
        button = new Button(item.getName());
        button.getStyleClass().add("AddButton");
        button.setPrefWidth(100);
        Label duration = new Label(String.valueOf(item.getHandlingDuration()) + " Days");
        duration.setWrapText(true);
        duration.setTextFill(Color.BLACK);
        duration.setStyle("-fx-text-alignment: center !important;");
        Label price = new Label(String.valueOf(item.getPrice()));
        price.setPrefWidth(40);
        price.setTextAlignment(TextAlignment.CENTER);
        price.setWrapText(true);
        this.itemID = item.getLaundryItemID();
        this.getChildren().addAll(duration, button, price);
        this.setAlignment(Pos.CENTER);
        ;
        this.setSpacing(10);
        this.setWidth(200);


    }

    public Button getButton() {
        return button;
    }

    public int getItemID() {
        return itemID;
    }

    public void setAddButton() {
        button.getStyleClass().add("AddButton");
    }

    public void setRemoveButton() {
        button.getStyleClass().add("RemoveButton");
    }
}
