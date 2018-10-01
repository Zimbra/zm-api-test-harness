package harness.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author swapnil.pingle
 *
 */
public class StringUtils {
	public boolean isEmpty(String input) {
		if (input.equals("")) {
			return true;
		}
		return false;
	}

	public ByteArrayOutputStream toByteStream(InputStream is) {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		try {
			while ((length = is.read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			return result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
