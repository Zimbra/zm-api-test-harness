package harness.utils;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

/**
 * @author swapnil.pingle
 *
 */
public class JsonDoc {

	public DocumentContext toJsonDoc(String raw) {
		return JsonPath.parse(raw);
	}

}
