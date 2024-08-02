import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class methods {
    public void Opening(){
        System.out.println("1. Login");
        System.out.println("2. Exit Program");
    }
    public void pageLogin(){
        System.out.println("Enter Username");
        System.out.println("Enter Password");
    }
    public void pageManager(){
        System.out.println("1. Change Employee Times");
        System.out.println("2. Create New Times");
        System.out.println("3. Back");
    }
    public void pageWorker(){
        System.out.println("1. View Times");
        System.out.println("2. Request Time Change");
        System.out.println("3. Back");
    }


    public void readScheduleFromFile(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.matches("\\d{1,2} \\w{3} \\d{4}")) {
                    // Parse date (e.g., "15 Sept 2021")
                    System.out.println("Date: " + line);
                } else if (line.matches("\\d{4} - \\d{4} .*")) {
                    // Parse time slot and worker name (e.g., "0930 - 1030 Alice")
                    StringTokenizer tokenizer = new StringTokenizer(line, " - ");
                    String startTime = tokenizer.nextToken();
                    String endTime = tokenizer.nextToken();
                    String workerName = tokenizer.nextToken();
                    System.out.println("Time: " + startTime + " - " + endTime + ", Worker: " + workerName);
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
