
package TestTracker;
import java.sql.*;

public class TaskDAO {

    Connection con;

    public TaskDAO() {
        con = DatabaseConnection.getConnection();
    }
    
    public boolean addTask(Task task) {

        try {

            String sql =
                    "INSERT INTO tasks VALUES(?,?,?)";

            PreparedStatement pst =
                    con.prepareStatement(sql);

            pst.setInt(1, task.getTaskId());
            pst.setString(2, task.getTaskTitle());
            pst.setString(3, task.getStatus());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(e.getMessage());
            return false;
        }
    }

    // SEARCH
    public Task searchTask(int id) {

        try {

            String sql =
                    "SELECT * FROM tasks WHERE task_id=?";

            PreparedStatement pst =
                    con.prepareStatement(sql);

            pst.setInt(1, id);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                return new Task(
                        rs.getInt("task_id"),
                        rs.getString("task_title"),
                        rs.getString("status")
                );
            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());
        }

        return null;
    }

    // UPDATE
    public boolean updateTask(Task task) {

        try {

            String sql =
                    "UPDATE tasks SET task_title=?, status=? WHERE task_id=?";

            PreparedStatement pst =
                    con.prepareStatement(sql);

            pst.setString(1, task.getTaskTitle());
            pst.setString(2, task.getStatus());
            pst.setInt(3, task.getTaskId());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean deleteTask(int id) {

        try {

            String sql =
                    "DELETE FROM tasks WHERE task_id=?";

            PreparedStatement pst =
                    con.prepareStatement(sql);

            pst.setInt(1, id);

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(e.getMessage());
            return false;
        }
    }
}
