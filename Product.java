
package Lab06.Question2;
public class Product {
    private String productName;
    private double unitPrice;
    private float quantity;
    
    public Product(String productName, double unitPrice, float quantity){
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }
    public String getName(){
        return productName;
    }
    public double getPrice(){
        return unitPrice;
    }
    public float getQuantity(){
        return quantity;
    }
    public double calculateTotal(){
        return unitPrice*quantity;
    }
    public String getStockStatus(){
        if (quantity < 5){
            return "Low Stock";
        }
        else {
            return "Available";
        }
    }
}