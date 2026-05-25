import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CreateDirectories {
    public static void main(String[] args) throws IOException {
        String basePath = "c:\\Users\\CaioBrito\\Personal Projects\\Senai\\GCSeCS\\mylibrary-gcs\\backend";
        
        String[] mainDirs = {
            "src\\main\\java\\com\\mylibrary\\entity",
            "src\\main\\java\\com\\mylibrary\\repository",
            "src\\main\\java\\com\\mylibrary\\service",
            "src\\main\\java\\com\\mylibrary\\dto",
            "src\\main\\java\\com\\mylibrary\\controller",
            "src\\main\\java\\com\\mylibrary\\exception"
        };
        
        String[] testDirs = {
            "src\\test\\java\\com\\mylibrary\\service",
            "src\\test\\java\\com\\mylibrary\\controller"
        };
        
        System.out.println("Creating main source directories:");
        for (String dir : mainDirs) {
            String fullPath = Paths.get(basePath, dir).toString();
            Files.createDirectories(Paths.get(fullPath));
            System.out.println("  Created: " + fullPath);
        }
        
        System.out.println("\nCreating test directories:");
        for (String dir : testDirs) {
            String fullPath = Paths.get(basePath, dir).toString();
            Files.createDirectories(Paths.get(fullPath));
            System.out.println("  Created: " + fullPath);
        }
        
        System.out.println("\nAll directories created successfully!");
    }
}
