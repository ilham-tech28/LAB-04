public class VehicleRentalTest {
    public static void main(String[] args) {
        System.out.println("=== Vehicle Rental System Test ===\n");
        
      
        Vehicle vehicle1 = new Car("CAR-001", "Toyota", 5, 50.0);
        Vehicle vehicle2 = new Bike("BIKE-001", "Yamaha", 8, 10.0);
        
        System.out.println("--- Car Details ---");
        vehicle1.displayVehicleInfo();
        System.out.println("Rental Cost: $" + vehicle1.calculateRentalCost());
        
        System.out.println("\n--- Bike Details ---");
        vehicle2.displayVehicleInfo();
        System.out.println("Rental Cost: $" + vehicle2.calculateRentalCost());
        
        System.out.println("\n=== Explanation ===");
        System.out.println("This is runtime polymorphism because the method to execute");
        System.out.println("(Car's or Bike's calculateRentalCost()) is determined at");
        System.out.println("runtime based on the actual object type, not the reference type.");
        System.out.println("Even though both references are of type Vehicle, Java calls");
        System.out.println("the overridden methods of the actual objects (Car and Bike).");
    }
}
