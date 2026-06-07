
package Question;
public class StudentSearchUpdateMain {

    public static void main(String[] args) {

        StudentDAO dao = new StudentDAO();

        dao.findStudentById(1);

        dao.updateStudentMark(1, 95);

        dao.findStudentById(1);
    }
}
