
package Lab06.Question3;
public class Appointment {
    private String patientName;
    private String appointmentType;
    private String patientCategory;
    private double baseFee;
    
    public Appointment(String Name, String Type, String Category, double Fee){
        this.patientName = Name;
        this.appointmentType = Type;
        this.patientCategory = Category;
        this.baseFee = Fee;
    }
    
    public String getName(){
        return patientName;
    }
    
    public String getType(){
        return appointmentType;
    }
    
    public String getCategory(){
        return patientCategory;
    }
    
    public double getFee(){
        return baseFee;
    }
    
    public double calculateFinalFee(){
        double finalFee = baseFee;

        if (appointmentType.equals("Specialist")) {
            finalFee += 1500;
        } else if (appointmentType.equals("Online")) {
            finalFee -= 500;
        }

        if (patientCategory.equals("Child")) {
            finalFee -= 300;
        } else if (patientCategory.equals("Senior")) {
            finalFee -= 500;
        }

        return finalFee;
    }
     public String getAppointmentMessage() {
        return appointmentType + " appointment selected";
    }
}
