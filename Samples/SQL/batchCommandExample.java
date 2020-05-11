package Samples.SQL;

import Database.DB;

import java.sql.SQLException;
import java.util.LinkedList;

/**
 * @Author Robert Skaar
 * @Project newProject  -  https://github.com/robskaar
 * @Date 18-03-2020
 **/

public class batchCommandExample {
    public void thisIsExample() throws SQLException {
        // copy paste into method needed and alter the values.
        LinkedList<Object> bulkAction = new LinkedList<>();
        bulkAction.add(1, "teste");
        bulkAction.add(2, 2);
        bulkAction.add(3, "whatever");
        bulkAction.add(4, "whatevers");
        bulkAction.add(5, "whateverss");
        bulkAction.add(6, "whateversss");
        bulkAction.add(7, "2020-02-02");

        LinkedList<Object> bulkAction2 = new LinkedList<>();
        bulkAction2.add(1, "tester");
        bulkAction2.add(2, 2);
        bulkAction2.add(3, "whatever");
        bulkAction2.add(4, "whatevers");
        bulkAction2.add(5, "whateverss");
        bulkAction2.add(6, "whateversss");
        bulkAction2.add(7, "2020-02-02");

        LinkedList<Object> bulkAction3 = new LinkedList<>();
        bulkAction3.add(1, "testeas");
        bulkAction3.add(2, 2);
        bulkAction3.add(3, "whatever");
        bulkAction3.add(4, "whatevers");
        bulkAction3.add(5, "whateverss");
        bulkAction3.add(6, "whateversss");
        bulkAction3.add(7, "2020-02-02");

        LinkedList<LinkedList> values = new LinkedList<>();
        values.add(bulkAction);
        values.add(bulkAction2);
        values.add(bulkAction3);
        DB.addBatch("INSERT INTO tblAccount VALUES (?,?,?,?,?,?,?)", values);
        DB.executeBatch();
    }
}
