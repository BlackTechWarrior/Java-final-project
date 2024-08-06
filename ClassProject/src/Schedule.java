import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Schedule 
{

    // Employee class to store employee details
    static class Employee 
    {
        String id;
        String role;
        String name;
        String password;
        String schedulePreference;
        Map<String, List<String>> availability = new HashMap<>(); // Map to store day and shifts

        
        Employee(String id, String role, String name, String password, String schedulePreference) //Constructor for Employee class
        {
            this.id = id;
            this.role = role;
            this.name = name;
            this.password = password;
            this.schedulePreference = schedulePreference;
        }
    }

    // Function to parse the employee.txt file
    public static List<Employee> parseEmployeeFile(String filename) 
    {
        List<Employee> employees = new ArrayList<>(); // List to store employee objects

        // buffered reader to read the file
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) 
        {
            String line;
            Employee employee = null;

            while ((line = br.readLine()) != null) // Read the file line by line
            {
                line = line.trim();
                if (line.startsWith("Employee ID:")) 
                {
                    if (employee != null) 
                    {
                        employees.add(employee);
                    }
                    String id = line.substring(12).trim();
                    employee = new Employee(id, "", "", "", ""); // Create a new employee object
                } 
                else if (line.startsWith("Role:")) 
                {
                    employee.role = line.substring(5).trim();
                } 
                else if (line.startsWith("Employee Name:")) 
                {
                    employee.name = line.substring(14).trim();
                } 
                else if (line.startsWith("Password:")) 
                {
                    employee.password = line.substring(9).trim();
                } 
                else if (line.startsWith("Schedule Preference:")) 
                {
                    employee.schedulePreference = line.substring(20).trim();
                } 
                else if (line.startsWith("Schedule:")) 
                {
                    if (employee.role.equals("lead")) 
                    {
                        employee.availability.put("Full-time", Collections.singletonList("Full-time")); // Leads are available full-time
                    }
                } 
                else if (employee.role.equals("member") && line.contains(":")) // Parsing availability for members
                {
                    String[] parts = line.split(":");
                    if (parts.length == 2) 
                    {  
                        // Ensuring correct splitting
                        String day = parts[0].trim();
                        List<String> shifts = Arrays.asList(parts[1].trim().split(","));
                        employee.availability.put(day, shifts); // Adding the availability to the employee object
                    }
                } 
                else if (line.startsWith("------------------------------")) 
                {
                    if (employee != null) {
                        employees.add(employee);
                    }
                    employee = null;
                }
            }

            if (employee != null) 
            {
                employees.add(employee);
            }
        } 

        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return employees;
    }

    public static void autoAssignShifts(List<Employee> employees) // Function to auto-assign shifts
    {
        Map<String, List<String>> shiftAssignments = new TreeMap<>(); // Map to store assigned shifts
        Map<String, Integer> hoursWorked = new HashMap<>(); // Map to store hours worked by each employee
        Map<String, Map<String, List<String>>> employeeAssignedShifts = new HashMap<>();
        Map<String, Integer> leadShiftsPerDay = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy"); // Date format for shift assignments
        Calendar startDate = Calendar.getInstance(); // Start date for the schedule
        startDate.add(Calendar.DATE, 1);
        Calendar endDate = (Calendar) startDate.clone(); // End date for the schedule
        endDate.add(Calendar.DATE, 6); // Schedule for a week
    
        Random random = new Random(System.currentTimeMillis()); // Use current time as seed for randomness
    
        // Shuffle the employees list to introduce randomness in assignment order
        Collections.shuffle(employees, random);
    
        for (Employee employee : employees) // Loop through the employees
        {
            int maxHours = employee.schedulePreference.contains("Part-time") ? // Check if the employee is part-time
                           Integer.parseInt(employee.schedulePreference.replaceAll("[^0-9]", "")) : 
                           40; // Default to 40 hours for full-time employees
    
            int assignedHours = 0;
            List<Calendar> availableDates = new ArrayList<>(); // List to store available dates for the employee
            for (Calendar date = (Calendar) startDate.clone(); !date.after(endDate); date.add(Calendar.DATE, 1)) 
            {
                availableDates.add((Calendar) date.clone()); // Add the date to the list
            }
            
            // Shuffle the available dates to randomize shift assignment
            Collections.shuffle(availableDates, random);
    
            for (Calendar currentDate : availableDates) 
            {
                if (assignedHours >= maxHours) break;
    
                String day = new SimpleDateFormat("EEEE").format(currentDate.getTime()); // Get the day of the week
                String dateStr = sdf.format(currentDate.getTime()); // Get the date in string format
    
                if (employee.role.equals("lead")) 
                {
                    employee.availability.put(day, Arrays.asList("AM", "PM", "EV", "Overnight")); // Leads can work all shifts
                }
    
                if (employee.availability.containsKey(day)) 
                {
                    List<String> shifts = new ArrayList<>(employee.availability.get(day)); // Get the available shifts for the day
                    Collections.shuffle(shifts, random);
    
                    for (String shift : shifts) 
                    {
                        if (shift.equalsIgnoreCase("off")) // Skip off shifts
                        {
                            continue;
                        }
    
                        int shiftHours = calculateShiftHours(shift);
                        if (assignedHours + shiftHours <= maxHours) // Check if we can assign this shift
                        {
                            // Check if we can assign this shift (especially for leads), no 2 leads can work the same shift
                            int currentLeadShifts = leadShiftsPerDay.getOrDefault(dateStr, 0);
                            int maxLeadShiftsPerDay = 2;
    
                            if ((employee.role.equals("lead") && currentLeadShifts < maxLeadShiftsPerDay) ||
                                !employee.role.equals("lead"))
                            {
                                
                                // Assign shift
                                assignedHours += shiftHours;
    
                                Calendar shiftStart = (Calendar) currentDate.clone();
                                shiftStart.set(Calendar.HOUR_OF_DAY, getShiftStartHour(shift));
                                shiftStart.set(Calendar.MINUTE, getShiftStartMinute(shift));
                                Calendar shiftEnd = (Calendar) shiftStart.clone();
                                shiftEnd.add(Calendar.HOUR, shiftHours);
    
                                String shiftEntry = String.format("%02d:%02d-%02d:%02d %s (%s)", 
                                                     shiftStart.get(Calendar.HOUR_OF_DAY), shiftStart.get(Calendar.MINUTE), 
                                                     shiftEnd.get(Calendar.HOUR_OF_DAY), shiftEnd.get(Calendar.MINUTE), 
                                                     employee.name, employee.role); // Shift entry for the schedule
    
                                shiftAssignments.computeIfAbsent(dateStr, k -> new ArrayList<>()).add(shiftEntry); // Add the shift to the schedule
                                hoursWorked.put(employee.id, hoursWorked.getOrDefault(employee.id, 0) + shiftHours); // Update hours worked
    
                                // Store assigned shift for the employee
                                employeeAssignedShifts
                                    .computeIfAbsent(employee.id, k -> new HashMap<>()) // Add the employee to the map
                                    .computeIfAbsent(dateStr, k -> new ArrayList<>()) // Add the date to the map
                                    .add(String.format("%02d:%02d-%02d:%02d", 
                                        shiftStart.get(Calendar.HOUR_OF_DAY), shiftStart.get(Calendar.MINUTE), 
                                        shiftEnd.get(Calendar.HOUR_OF_DAY), shiftEnd.get(Calendar.MINUTE))); // Add the shift to the map
    
                                // Update lead shifts count
                                if (employee.role.equals("lead")) 
                                {
                                    leadShiftsPerDay.put(dateStr, currentLeadShifts + 1);
                                }
                            }
                        }
                    }
                }
            }
        }
    
        // Write assigned shifts to schedule.txt
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/schedule.txt"))) 
        {
            bw.write("Here's the list of shifts generated starting date " + sdf.format(startDate.getTime()) + "\n\n"); // Write the start date
            for (Map.Entry<String, List<String>> entry : shiftAssignments.entrySet()) // Loop through the assigned shifts
            {
                bw.write(entry.getKey() + ":\n");
                for (String shiftEntry : entry.getValue()) // Write the shift entry
                {
                    bw.write("  " + shiftEntry + "\n");
                }
                bw.write("\n");
            }
    
            // Write employee details including assigned shifts
            for (Employee employee : employees) 
            {
                bw.write("Employee ID: " + employee.id + "\n");
                bw.write("Name: " + employee.name + "\n");
                bw.write("Assigned Hours: " + hoursWorked.getOrDefault(employee.id, 0) + "\n");
                
                Map<String, List<String>> assignedShifts = employeeAssignedShifts.getOrDefault(employee.id, new HashMap<>()); // Get assigned shifts for the employee
                
                if (!assignedShifts.isEmpty()) 
                {
                    for (Map.Entry<String, List<String>> entry : assignedShifts.entrySet()) 
                    {
                        String date = entry.getKey();
                        List<String> shifts = entry.getValue();
                        bw.write(date + ": " + String.join(", ", shifts) + "\n");
                    }
                } 
                else 
                {
                    bw.write("No shifts assigned\n");
                }
                
                bw.write("------------------------------\n");
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }        

    // Helper function to calculate shift start hour
    public static int getShiftStartHour(String shift) 
    {
        switch (shift.trim().toLowerCase()) 
        {
            case "am": return 8;
            case "pm": return 13;
            case "ev": return 18;
            case "overnight": return 0;
            case "open": return 0; // Open means all day
            default: return 0; // Default case for custom shifts
        }
    }

    // Helper function to calculate shift start minute
    public static int getShiftStartMinute(String shift) 
    {
        return 0; // Default to start at the beginning of the hour
    }

    // Helper function to calculate shift hours based on the shift string
    public static int calculateShiftHours(String shift) 
    {
        switch (shift.trim().toLowerCase()) 
        {
            case "am": return 4;
            case "pm": return 4;
            case "ev": return 5;
            case "overnight": return 7;
            case "open": return 12; // Assuming open means available for all shifts
            default: return customShiftHours(shift);
        }
    }

    // Custom shift calculation for brackets, e.g., (00:00-11:00)
    public static int customShiftHours(String shift) 
    {
        if (shift.startsWith("(") && shift.endsWith(")")) 
        {
            String[] times = shift.substring(1, shift.length() - 1).split("-");
            int start = Integer.parseInt(times[0].replace(":", ""));
            int end = Integer.parseInt(times[1].replace(":", ""));
            return (end - start) / 100;
        }
        return 0;
    }

    // Function to view the weekly schedule
    public static void viewWeeklySchedule() 
    {
        try (BufferedReader br = new BufferedReader(new FileReader("src/schedule.txt"))) 
        {
            String line;
            while ((line = br.readLine()) != null) 
            {
                System.out.println(line);
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    public static void viewLoggedInEmployeeSchedule(String employeeID) // Function to view the schedule of a logged-in employee
    {
        File file = new File("src/schedule.txt");
        boolean employeeFound = false;
        StringBuilder schedule = new StringBuilder();

        try (Scanner scanner = new Scanner(file)) 
        {
            while (scanner.hasNextLine()) 
            {
                String line = scanner.nextLine();

                // Check if the current line contains the Employee ID
                if (line.contains("Employee ID: " + employeeID)) 
                {
                    employeeFound = true;
                
                    scanner.nextLine(); 
                    while (scanner.hasNextLine()) 
                    {
                        line = scanner.nextLine();
                        if (line.startsWith("Employee ID:") && !line.contains(employeeID)) 
                        {
                            break; // Stop reading when reaching the next employee
                        }
                        schedule.append(line).append("\n"); // Add the rest of the employee's data
                    }
                    break;
                }
            }

            if (employeeFound) 
            {
                System.out.println("\nHere's your upcoming schedule:");
                System.out.println(schedule.toString());
            } 
            else 
            {
                System.out.println("\nEmployee not found.\n");
            }

        } 
        catch (FileNotFoundException e) 
        {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
    }

}