public class Lecturer extends StaffMembernew {

    private int courseCount;
    private double paymentPerCourse;

    public Lecturer(String name, String id, String dept, int courseCount, double paymentPerCourse) {
        super(name, id, dept);
        this.courseCount = courseCount;
        this.paymentPerCourse = paymentPerCourse;
    }

    @Override
    public double calculateMonthlyPayment() {
        return courseCount * paymentPerCourse;
    }

    public void displayLecturerDetails() {
        displayBasicDetails();
        System.out.println("Courses: " + courseCount);
        System.out.println("Payment per Course: " + paymentPerCourse);
    }
}
