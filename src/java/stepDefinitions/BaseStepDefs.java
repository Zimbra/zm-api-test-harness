package stepDefinitions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import baseLine.Baseline;

/**
 * @author swapnil.pingle
 *
 */
public class BaseStepDefs {

	public Baseline baseline;
	public static Properties globalProperties;
	private static String filename;
	public static final Logger logger = LoggerFactory.getLogger(BaseStepDefs.class);
	public String nameMask;

	public BaseStepDefs() {
		baseline = new Baseline();
		globalProperties = new Properties();
		filename = "global.properties";
		try (FileInputStream fInputStream = new FileInputStream(new File(filename))) {
			globalProperties.load(fInputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		File nameMaskFile = new File("userNameMask.txt");
		try {
            BufferedReader reader = new BufferedReader(new FileReader(nameMaskFile));
            nameMask = reader.readLine();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		System.out.println(nameMask);
	}
    public String generateUserName(String user){
        if(user.contains("@host")){
            String[] userNameArray = user.split("@");
            String domain = globalProperties.getProperty("server");
            String name = nameMask+ userNameArray[0].split("_")[1];
            user = name + "@" + domain;
        }
        return user;
    }

}
