public class DeliveryChargeTest {
    public static void main(String[] args) {
        DeliveryChargeCalculator calculator = new DeliveryChargeCalculator();
        
        System.out.println("=== Delivery Charge Calculator Test ===\n");
        
      
        double result1 = calculator.calculateCharge(150.0);
        System.out.println("1. Base charge only (150.0): $" + result1);
        
  
        double result2 = calculator.calculateCharge(150.0, 10.0);
        System.out.println("2. Base + distance (150.0, 10km): $" + result2);
        
       
        double result3 = calculator.calculateCharge(150.0, 10.0, 5.0);
        System.out.println("3. Base + distance + weight (150.0, 10km, 5kg): $" + result3);
        
    
        double result4 = calculator.calculateCharge(150.0, true);
        System.out.println("4. Base + express delivery (150.0, true): $" + result4);
        
    
        double result5 = calculator.calculateCharge(150.0, false);
        System.out.println("5. Base + express delivery (150.0, false): $" + result5);
        
        System.out.println("\n=== Explanation ===");
        System.out.println("This is compile-time polymorphism because the compiler determines");
        System.out.println("which calculateCharge() method to call based on the number and");
        System.out.println("types of arguments passed at compile time.");
    }
}
