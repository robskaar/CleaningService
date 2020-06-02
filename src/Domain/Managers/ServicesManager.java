package Domain.Managers;

import Domain.DeliveryPoint.DeliveryPoint;
import Foundation.Database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * @Author Robert Skaar
 * @Project CleaningService  -  https://github.com/robskaar
 * @Date 02-06-2020
 **/

public class ServicesManager {
    ArrayList<String> serviceNames = new ArrayList<>();

    public static ObservableList<DeliveryPoint> getServicesFromDB() {

        DB.selectSQL("SELECT * FROM getRouteDeliveryPoints(" + routeID + ")");

        return FXCollections.observableArrayList(convertResultSetToArrayList());
    }

    private static ArrayList<DeliveryPoint> convertResultSetToArrayList() {

        // Stores all delivery from result set
        ArrayList<DeliveryPoint> deliveryPoints = new ArrayList<>();

        String ID;
        String name;
        String address;
        String zipCode;
        String email;
        String phone;
        String routeID;

        ID = DB.getData();

        while(!ID.equals(DB.NOMOREDATA)){
            name = DB.getData();
            address = DB.getData();
            zipCode = DB.getData();
            email = DB.getData();
            phone = DB.getData();
            routeID = DB.getData();

            deliveryPoints.add(new DeliveryPoint(ID,name,address,zipCode,email,phone,routeID));

            ID = DB.getData();
        }

        return deliveryPoints;
    }

    public void setServicesInDB(){

    }

}
