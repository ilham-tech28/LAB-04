/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ilhamma-pe23027
 */
public class StudentMarkReportTest {
    
    // StudentMarkReportTest.java


    public static void main(String[] args) {

        // Creating the marks array
        String[] marks = {"78", "82", "absent", "90"};

        // Creating object
        StudentMarkReport report = new StudentMarkReport(marks);

        // First try block for invalid array index
        try {

            // Exception occurs because index 6 does not exist in the array
            int mark = report.getMarkAt(6);

            System.out.println("Mark: " + mark);

        } catch (ArrayIndexOutOfBoundsException e) {

            System.out.println("Error: Selected mark position does not exist.");

        } finally {

            System.out.println("Array access checking completed.");
        }

        // Second try block for invalid number format
        try {

            // Exception occurs because "absent" cannot be converted into an integer
            int mark = report.getMarkAt(2);

            System.out.println("Mark: " + mark);

        } catch (NumberFormatException e) {

            System.out.println("Error: Selected mark is not a valid number.");

        } finally {

            System.out.println("Number conversion checking completed.");
        }

        System.out.println("Report checking completed.");
    }

    
}
