package com.landlink;

import com.landlink.dao.DatabaseHelper;
import java.sql.Connection;
import java.sql.Statement;

public class MigrateDB {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseHelper.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            int updated = stmt.executeUpdate("UPDATE messages SET sender_id = (SELECT id FROM users WHERE role = 'ADMIN' LIMIT 1) WHERE sender_id = 0");
            System.out.println("Migrated " + updated + " system messages to admin ID.");
            DatabaseHelper.getInstance().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
