package harness.utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author swapnil.pingle
 *
 */
public class RequestContentType {

	private static final String[] PRINTABLES = { "json", "xml", "text", "urlencoded", "html" };
	public static final String CHARSET = "charset";
	private static final String EMPTY = "";

	public ContentType getContentType(String mediaType, Charset charset) {
		if (!isPrintable(mediaType)) {
			try {
				return ContentType.create(mediaType);
			} catch (Exception e) {
				return null;
			}
		}
		Map<String, String> map = parseContentTypeParams(mediaType);
		if (map != null) {
			String cs = map.get(CHARSET);
			if (cs != null) {
				charset = Charset.forName(cs);
				map.remove(CHARSET);
			}
		}
		ContentType ct = ContentType.parse(mediaType).withCharset(charset);
		if (map != null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				ct = ct.withParameters(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		return ct;
	}

	public boolean isPrintable(String mediaType) {
		if (mediaType == null) {
			return false;
		}
		String type = mediaType.toLowerCase();
		for (String temp : PRINTABLES) {
			if (type.contains(temp)) {
				return true;
			}
		}
		return false;
	}

	public Map<String, String> parseContentTypeParams(String mimeType) {
		List<String> items = split(mimeType, ';');
		int count = items.size();
		if (count <= 1) {
			return null;
		}
		Map<String, String> map = new LinkedHashMap<String, String>(count - 1);
		for (int i = 1; i < count; i++) {
			String item = items.get(i);
			int pos = item.indexOf('=');
			if (pos == -1) {
				continue;
			}
			String key = item.substring(0, pos).trim();
			String val = item.substring(pos + 1).trim();
			map.put(key, val);
		}
		return map;
	}

	public List<String> split(String s, char delimiter) {
		int pos = s.indexOf(delimiter);
		if (pos == -1) {
			return Collections.singletonList(s);
		}
		List<String> list = new ArrayList<String>();
		int startPos = 0;
		while (pos != -1) {
			String temp = s.substring(startPos, pos);
			if (!EMPTY.equals(temp)) {
				list.add(temp);
			}
			startPos = pos + 1;
			pos = s.indexOf(delimiter, startPos);
		}
		if (startPos != s.length()) {
			String temp = s.substring(startPos);
			if (!EMPTY.equals(temp)) {
				list.add(temp);
			}
		}
		return list;
	}
}
