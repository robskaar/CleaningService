package Domain.Driver;

import java.sql.Date;

/**
 * @Author Robert Skaar
 * @Project CleaningService  -  https://github.com/robskaar
 * @Date 08-06-2020
 **/

public class Driver {
    int driverID;
    String firstName;
    String lastName;
    String emailAddress;
    String phoneNo;
    int corporateIDNO;
    Date dateOfBirth;


    public Driver(int driverID, String firstName, String lastName, String emailAddress, String phoneNo, int corporateIDNO, Date dateOfBirth ) {
        this.driverID = driverID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNo = phoneNo;
        this.corporateIDNO = corporateIDNO;
        this.dateOfBirth = dateOfBirth;
    }

    public int getDriverID( ) {
        return driverID;
    }

    public String getFirstName( ) {
        return firstName;
    }

    public String getLastName( ) {
        return lastName;
    }

    public String getEmailAddress( ) {
        return emailAddress;
    }

    public String getPhoneNo( ) {
        return phoneNo;
    }

    public int getCorporateIDNO( ) {
        return corporateIDNO;
    }

    public Date getDateOfBirth( ) {
        return dateOfBirth;
    }
}
