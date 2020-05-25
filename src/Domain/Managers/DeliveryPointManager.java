package Domain.Managers;

import Domain.DeliveryPoint.DeliveryPoint;
import Foundation.Database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class DeliveryPointManager {

    public static ObservableList<DeliveryPoint> getRouteDeliveryPoints(int routeID) {

        DB.selectSQL("SELECT * FROM getRouteDeliveryPoints(" + routeID + ")");

        return FXCollections.observableArrayList(convertResultSetToArrayList());
    }

    private static ArrayList<DeliveryPoint> convertResultSetToArrayList() {

        // Stores all delivery from result set
        ArrayList<DeliveryPoint> deliveryPoints = new ArrayList<>();

        // Data uses to assert that there is more data
        String data;

        do {
            data = DB.getData();
            if (data.equals("|ND|")) {
                break;
            }
            else {
                deliveryPoints.add(new DeliveryPoint(data,DB.getData(),DB.getData(),DB.getData(),DB.getData(),DB.getData(),DB.getData()));
            }

        }
        while (true);

        return deliveryPoints;
    }

}
