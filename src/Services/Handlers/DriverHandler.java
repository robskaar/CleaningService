package Services.Handlers;

import Domain.Driver.Driver;
import Foundation.Database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Date;
import java.util.ArrayList;

/**
 * @Author Robert Skaar
 * @Project CleaningService  -  https://github.com/robskaar
 * @Date 08-06-2020
 **/

public class DriverHandler {


    /**
     * will check if a route is assigned to the current user - this is only called by a driver
     * @return - returns boolean, if assigned or not
     */
    public static boolean isDriverAssignedWithRoute( ){

        DB.selectSQL("SELECT * FROM getDriverRoute('" + AccountHandler.currentUser + "')");

        String data = DB.getData();

        if(data.equals("null")){
            return false;
        }

        return true;
    }



    /**
     * gets drivers from db.
     * @return returns a observable array list with driver objects converted from result set.
     */
    public static ObservableList<Driver> getDrivers() {
        DB.selectSQL("SELECT * FROM getDrivers()");
        return FXCollections.observableArrayList(convertResultSetToArrayList());
    }

    /**
     * makes a object from a result set and adds it to an array list until no more objects can be made
     * @return returns an array list of driver objects
     */
    private static ArrayList<Driver> convertResultSetToArrayList() {
        int driverID = 0;
        String firstName = null;
        String lastName = null;
        String emailAddress = null;
        String phoneNo = null;
        int corporateIDNO = 0;
        Date dateOfBirth = null;

        // Stores all orders from result set
        ArrayList<Driver> Drivers = new ArrayList<>();

        // Data uses to assert that there is more data
        String data = DB.getData();

        while (!data.equals("|ND|") || DB.isPendingData()) {

            driverID=Integer.parseInt(data);
            firstName= DB.getData();
            lastName= DB.getData();
            emailAddress = DB.getData();
            phoneNo=DB.getData();
            dateOfBirth = Date.valueOf(DB.getData());
            corporateIDNO = Integer.parseInt(DB.getData());



            Drivers.add(new Driver(driverID, firstName, lastName, emailAddress, phoneNo, corporateIDNO, dateOfBirth));

            //assigning the data at the end to ensure the correct order.
            data = DB.getData();
        }

        return Drivers;
    }

}
