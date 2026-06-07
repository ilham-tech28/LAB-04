
package Question;
public class Student {
    private int studentId;
    private String studentName;
    private int mark;

    public Student(int studentId, String studentName, int mark) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.mark = mark;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getMark() {
        return mark;
    }
}