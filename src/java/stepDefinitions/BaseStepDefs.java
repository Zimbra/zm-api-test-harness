package stepDefinitions;

import java.io.File;
import java.io.FileInputStream;
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

	public BaseStepDefs() {
		baseline = new Baseline();
		globalProperties = new Properties();
		filename = "global.properties";
		try (FileInputStream fInputStream = new FileInputStream(new File(filename))) {
			globalProperties.load(fInputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
