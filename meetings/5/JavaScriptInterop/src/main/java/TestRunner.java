import javax.script.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import resolving.tests.*;


public class TestRunner {
    public static void main(String[] args) {
        // create a script engine manager
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a Nashorn script engine
        ScriptEngine engine = factory.getEngineByName("nashorn");
        // evaluate JavaScript statement
        String jsScript;

        try {
            Optional<String> readedFile = Files.readAllLines(Paths.get("src/main/java/TestRunnerJs.js")).stream().reduce(String::concat);
            if (!readedFile.isPresent()){
                System.out.println("Cannot read js file");
                return;
            }
            jsScript = readedFile.get();
        } catch (IOException e) {
            System.out.println("Cannot read js file: " + e.getMessage());
            return;
        }


        try {
            engine.eval(jsScript);
        } catch (final ScriptException se) {
            se.printStackTrace();
        }
    }
}
