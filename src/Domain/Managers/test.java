package Domain.Managers;

import Domain.Enums.Role;
import Foundation.Database.DB;

public class test {
    public static void main(String[] args) {
        DB.setDBPropertiesPath(Role.Costumer);
    }
}
