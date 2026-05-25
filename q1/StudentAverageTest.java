/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ilhamma-pe23027
 */
public class StudentAverageTest {
       public static void main(String[] args) {

        // Creating object with 0 students
        StudentAverageCalculator calculator =
                new StudentAverageCalculator(500, 0);

        try {

            // Exception occurs because division by zero is not allowed
            int average = calculator.calculateAverage();

            System.out.println("Average: " + average);

        } catch (ArithmeticException e) {

            System.out.println("Error: Number of students cannot be zero.");

        } finally {

            System.out.println("Average calculation completed.");
        }

        System.out.println("Program continues...");
    }
    
}
