package Domain.Managers;

import Domain.Route.Route;
import Foundation.Database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;

/**
 * @Author Robert Skaar
 * @Project CleaningService  -  https://github.com/robskaar
 * @Date 08-06-2020 LaundryManager
 **/

public class RouteHandler {


    /**
     * gets routes from DB.
     * @return returns observable array list converted from result set, with objects of routes.
     */
    public static ObservableList<Route> getRoutes( ) {
        DB.selectSQL("SELECT * FROM getRoutes()");
        return FXCollections.observableArrayList(convertResultSetToArrayList());
    }

    /**
     * converts result set to array list
     * @return returns an array list of route objects
     */
    private static ArrayList<Route> convertResultSetToArrayList( ) {
        int routeID;
        int isAssigned;

        // Stores all orders from result set
        ArrayList<Route> Routes = new ArrayList<>();

        // Data uses to assert that there is more data
        String data = DB.getData();

        while (!data.equals("|ND|") || DB.isPendingData()) {
            routeID=Integer.parseInt(data);
            isAssigned= Integer.parseInt(DB.getData());

            Routes.add(new Route(routeID,isAssigned));

            //assigning the data at the end to ensure the correct order.
            data = DB.getData();
        }

        return Routes;
    }

    /**
     * assigns a route in the DB, and updates route fldIsAssigned true/false depending on the current state.
     * @param routeID - the route ID to be assigned
     * @param driverCorpID - the corp ID of the driver to get the route assigned
     */
    public static void assignRoute(int routeID, int driverCorpID){
        try {
            CallableStatement cstmt;
            Connection con = DB.getConnection();
            cstmt = con.prepareCall("{call CleaningService.dbo.confirmRouteAssignment(?,?)}");
            cstmt.setInt(1, routeID);
            cstmt.setInt(2,driverCorpID);
            cstmt.execute();
            cstmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
