package harness;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author swapnil.pingle
 *
 */
public class MultiValuedMap extends LinkedHashMap<String, List> {

	public void add(String key, Object value) {
		List list = get(key);
		if (list == null) {
			list = new ArrayList();
			put(key, list);
		}
		list.add(value);
	}

	public Object getFirst(String key) {
		List list = get(key);
		if (list == null) {
			return null;
		}
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

}
