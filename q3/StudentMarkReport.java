/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ilhamma-pe23027
 */
public class StudentMarkReport {
     // Private array attribute
    private String[] marks;

    // Constructor
    public StudentMarkReport(String[] marks) {
        this.marks = marks;
    }

    // Method to get mark at a given index
    public int getMarkAt(int index) {

        String selectedMark = marks[index];

        return Integer.parseInt(selectedMark);
    }
    
}
