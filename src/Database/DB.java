package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

/**
 * @Author Robert Skaar, revised version from Tommy Haug
 * @Project newProject  -  https://github.com/robskaar
 * @Date 18-03-2020
 **/

public class DB {

    // Static fields used in DB
    private static Connection con;
    private static PreparedStatement ps;
    private static ResultSet rs;
    private static String port;
    private static String dataBaseName;
    private static String userName;
    private static String password;
    private static final String NOMOREDATA = "|ND|";
    private static int numberOfColumns;
    private static int currentColumnNumber = 1;

    /**
     * STATES
     */
    private static boolean moreData = false;  // from Resultset
    private static boolean pendingData = false; // from select statement
    private static boolean terminated = false;

    // Static connection informations
    static {
        try {
// port to be establish connection on by default
            //    if (port.equals(null))
            port = "1433";
            //  else{
//should be updated with port to be used
            //  }

// should be updated with username of logged in
            userName = "appUser";

// should be updated with passsword logged in
            password = "ItsAmeMario123";

// should be updated with database to be edited
            dataBaseName = "CleaningService";

// Load compiled driver(jar file) specific to DB into memory  
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

// status message that connection established succesfull
            System.out.println("Database Ready");

        }
        catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            con = DriverManager.getConnection("jdbc:sqlserver://localhost:" + port + ";databaseName=" + dataBaseName,
                                              userName, password);
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return con;
    }

    // Establish Connection with the Connection interface
    private static void connect() {
        try {
                con = DriverManager.getConnection(
                        "jdbc:sqlserver://localhost:" + port + ";databaseName=" + dataBaseName, userName, password);
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    // close the connection with the Connection interface
    private static void disconnect() {
        try {

            con.close();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * @param sql the sql string to be executed in SQLServer
     */
    public static void selectSQL(String sql) {
        if (terminated) {
            System.exit(0);
        }
        try {
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
            connect();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            pendingData = true;
            moreData = rs.next();
            ResultSetMetaData rsmd = rs.getMetaData();
            numberOfColumns = rsmd.getColumnCount();
        }
        catch (Exception e) {
            System.err.println("Error in the sql parameter, please test this in SQLServer first");
            System.err.println(e.getMessage());
        }
    }

    /**
     * @return The next single value (formatted) from previous select
     */
    public static String getDisplayData() {
        if (terminated) {
            System.exit(0);
        }
        if (!pendingData) {
            terminated = true;
            throw new RuntimeException("ERROR! No previous select, communication with the database is lost!");
        }
        else if (!moreData) {
            disconnect();
            pendingData = false;
            return NOMOREDATA;
        }
        else {
            return getNextValue(true);
        }
    }

    public static String getData() {
        if (terminated) {
            System.exit(0);
        }
        if (!pendingData) {
            terminated = true;
            throw new RuntimeException("ERROR! No previous select, communication with the database is lost!");
        }
        else if (!moreData) {
            disconnect();
            pendingData = false;
            return NOMOREDATA;
        }
        else {
            return getNextValue(false).trim();
        }
    }

    public static int getNumberOfColumns() {
        return numberOfColumns;
    }

    private static String getNextValue(boolean view) {
        StringBuilder value = new StringBuilder();
        try {
            value.append(rs.getString(currentColumnNumber));
            if (currentColumnNumber >= numberOfColumns) {
                currentColumnNumber = 1;
                if (view) {
                    value.append("\n");
                }
                moreData = rs.next();
            }
            else {
                if (view) {
                    value.append(" ");
                }
                currentColumnNumber++;
            }
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return value.toString();
    }

    public static boolean insertSQL(String sql) {
        return executeUpdate(sql);
    }

    /**
     * @param sql    - this is the sql query string itself
     * @param values - this is the supplied values from hashmap arrays
     * @throws SQLException- exception from sql execution
     */
    public static void addBatch(String sql, LinkedList<LinkedList> values) throws SQLException {
        connect();
        if (ps == null) {
            ps = con.prepareStatement(sql);
        }
        for (LinkedList linkedList : values) {
            for (int i = 1; i < linkedList.size() + 1; i++) {
                ps.setString(i, linkedList.get(i).toString());
            }
            ps.addBatch();
        }
    }

    /**
     * user for batch sql commands
     * this executes and closes prepared statement + connection to DB.
     *
     * @throws SQLException - exception from sql execution
     */
    public static void executeBatch() throws SQLException {
        ps.executeBatch();
        ps.close();
        disconnect();
    }

    public static boolean updateSQL(String sql) {
        return executeUpdate(sql);
    }

    public static boolean deleteSQL(String sql) {
        return executeUpdate(sql);
    }

    private static boolean executeUpdate(String sql) {
        if (terminated) {
            System.exit(0);
        }
        if (pendingData) {
            terminated = true;
            throw new RuntimeException(
                    "ERROR! There were pending data from previous select, communication with the database is lost! ");
        }
        try {
            if (ps != null) {
                ps.close();
            }
            connect();
            ps = con.prepareStatement(sql);
            int rows = ps.executeUpdate();
            ps.close();
            if (rows > 0) {
                return true;
            }
        }
        catch (RuntimeException | SQLException e) {
            System.err.println(e.getMessage());
        }
        finally {
            disconnect();
        }
        return false;
    }


}