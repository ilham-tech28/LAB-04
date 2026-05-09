public class LabAssistant extends StaffMembernew {

    private int hoursWorked;
    private double hourlyRate;

    public LabAssistant(String name, String id, String dept, int hoursWorked, double hourlyRate) {
        super(name, id, dept);
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
    }

    @Override
    public double calculateMonthlyPayment() {
        return hoursWorked * hourlyRate;
    }

    public void displayLabAssistantDetails() {
        displayBasicDetails();
        System.out.println("Hours Worked: " + hoursWorked);
        System.out.println("Hourly Rate: " + hourlyRate);
    }
}

/*
department is protected, so it can be accessed inside child classes.
*/
