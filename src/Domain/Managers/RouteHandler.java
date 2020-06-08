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


    public static ObservableList<Route> getRoutes( ) {
        DB.selectSQL("SELECT * FROM getRoutes()");
        return FXCollections.observableArrayList(convertResultSetToArrayList());
    }
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
