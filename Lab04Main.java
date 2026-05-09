public class Lab04Main{

    public static void main(String[] args) {

        // System name
        StaffMembernew.showSystemName();

        // Policy
        UniversityPolicy.showPolicyHeader();
        System.out.println("University: " + UniversityPolicy.UNIVERSITY_NAME);

        // Create objects
        Lecturer lecturer1 = new Lecturer("Ali", "L001", "IT", 3, 5000);
        Lecturer lecturer2 = new Lecturer("Sara", "L002", "CS", 2, 6000);
        LabAssistant assistant = new LabAssistant("John", "A001", "Lab", 120, 50);

        // Change department
        lecturer1.changeDepartment("SE");

        // Display details
        lecturer1.displayLecturerDetails();
        System.out.println("Monthly Payment: " + lecturer1.calculateMonthlyPayment());

        lecturer2.displayLecturerDetails();
        System.out.println("Monthly Payment: " + lecturer2.calculateMonthlyPayment());

        assistant.displayLabAssistantDetails();
        System.out.println("Monthly Payment: " + assistant.calculateMonthlyPayment());

        // Total payment
        double total = lecturer1.calculateMonthlyPayment()
                     + lecturer2.calculateMonthlyPayment()
                     + assistant.calculateMonthlyPayment();

        System.out.println("Total Monthly Payment: " + total);

        // Staff count
        System.out.println("Total Staff: " + StaffMembernew.getStaffCount());

        // Common notice
        lecturer1.showCommonNotice();
        lecturer2.showCommonNotice();
        assistant.showCommonNotice();
    }
}

/*
changeDepartment() is useful to safely update department without direct access.
*/
