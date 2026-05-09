public class Bike extends Vehicle {
    private int numberOfHours;
    private double hourlyRate;
    
    public Bike(String vehicleNumber, String brand, int numberOfHours, double hourlyRate) {
        super(vehicleNumber, brand);
        this.numberOfHours = numberOfHours;
        this.hourlyRate = hourlyRate;
    }
    
    @Override
    public double calculateRentalCost() {
        super.calculateRentalCost();
        return numberOfHours * hourlyRate;
    }
    
    @Override
    public void displayVehicleInfo() {
        super.displayVehicleInfo();
        System.out.println("Number of Hours: " + numberOfHours);
        System.out.println("Hourly Rate: $" + hourlyRate);
    }
}