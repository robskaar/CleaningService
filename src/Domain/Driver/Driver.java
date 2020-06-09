package Domain.Driver;

import java.sql.Date;

/**
 * @Author Robert Skaar
 * @Project CleaningService  -  https://github.com/robskaar
 * @Date 08-06-2020
 **/

public class Driver {
    private int driverID;
    private String firstName;
    private String lastName;
    private  String emailAddress;
    private String phoneNo;
    /*
     corporateIDNO is the ID we expect the driver to have at the delivery company.
      used for manager to see time schedule and choose a driver for a route
     */
    private  int corporateIDNO;
    private  Date dateOfBirth;


    public Driver(int driverID, String firstName, String lastName, String emailAddress, String phoneNo, int corporateIDNO, Date dateOfBirth ) {
        this.driverID = driverID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNo = phoneNo;
        this.corporateIDNO = corporateIDNO;
        this.dateOfBirth = dateOfBirth;
    }

    public int getCorporateIDNO( ) {
        return corporateIDNO;
    }
}
