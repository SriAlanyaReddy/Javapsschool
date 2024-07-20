import java.io.File;
import java.io.IOException;

 class fib {
    public static void main(String[] args)  throws  IOException{
        try {
            // Specify the file name and path
            File f = new File("emp.txt");

            // Create the file
            if (f.createNewFile()) {
                System.out.println("File created: " + f.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.err.println("An error occurred while creating the file: " + e.getMessage());
        }
    }
}
