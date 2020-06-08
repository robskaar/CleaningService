package Domain.Managers;

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



    public static ObservableList<Driver> getDrivers() {
        DB.selectSQL("SELECT * FROM getDrivers()");
        return FXCollections.observableArrayList(convertResultSetToArrayList());
    }
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
