/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package common.formatting;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.LookupTranslator;

/**
 * collection of helper functions for handling Strings.
 * <p/>
 * @author fr
 */
public class StringFormatters {

	private final static List<String> EMPTY_ENTRIES = new ArrayList<String>();

	static {
		EMPTY_ENTRIES.add(null);
		EMPTY_ENTRIES.add("");
	}
	// leave Unicode intact
	public static final CharSequenceTranslator ESCAPE_JAVA
			= new LookupTranslator(
					new String[][]{
						{"\"", "\\\""},
						{"\\", "\\\\"},}).with(
					new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE()));

	public static String format(String pattern, Object[] args) {
		return format(pattern, args, null);
	}

	public static String format(String pattern, Object[] args, Locale locale) {
		MessageFormat messageFormat = (locale != null ? new MessageFormat(pattern, locale) : new MessageFormat(pattern));
		return messageFormat.format(args, new StringBuffer(), null).toString();
	}

	/**
	 * cut away path from full filename/URL.
	 *
	 * @param text
	 * @return
	 */
	public static String cutAwayPath(String text) {
		final String ch = "/";
		if (text.indexOf(ch) == -1) {
			return text;
		}
		return StringUtils.substringAfterLast(text, ch);
	}

	public static String cutAwaySearch(String text) {
		final String ch = "?";
		if (text.indexOf(ch) == -1) {
			return text;
		}
		return StringUtils.substringBefore(text, ch);
	}

	// MOLfile-related stuff
	/**
	 * replace line endings of molfiles and rxnfiles by pipes.
	 *
	 * @param molfile
	 * @return
	 */
	public static String addPipes(byte[] molfile) {
		return addPipes(new String(molfile));
	}

	public static String addPipes(String molfile) {
		molfile = fixLineEnds(molfile);
		if (molfile != null) {
			molfile = molfile.replace("\n", "|");
		}
		return molfile;
	}

	/**
	 * clean line endings of molfiles and rxnfiles.
	 *
	 * @param molfile
	 * @return
	 */
	public static String removePipes(String molfile) {
		molfile = fixLineEnds(molfile);
		if (molfile != null) {
			molfile = molfile.replace("|", "\n");
		}
		return molfile;
	}

	public static void removeEmpty(Collection list) {
		if (list != null) {
			list.removeAll(EMPTY_ENTRIES);
		}
//        return list;
	}

	@SuppressWarnings("deprecation")
	public static String fixUrlEncoding(String location) {
		try {
			URI.create(location);
			// seems ok
			return location;
		} catch (Exception ex) {
			// encode
			try {
				location = URLEncoder.encode(location, "UTF-8");
			} catch (UnsupportedEncodingException ex2) {
				Logger.getLogger(StringFormatters.class.getName()).log(Level.SEVERE, null, ex);
				location = URLEncoder.encode(location);
			}

			// fix most common problems, see http://www.permadi.com/tutorial/urlEncoding/ for useful info
			location = location.replaceFirst("(?ims)%3a", ":").
					replaceAll("(?ims)%2f", "/").
					replaceFirst("(?ims)%3f", "?").
					replaceAll("(?ims)%3d", "=").
					replaceAll("(?ims)%26", "&").
					replaceAll("(?ims)%2b", "+");
			return location;
		}
	}

	/**
	 * see http://de.wikipedia.org/wiki/CAS-Nummer for the algorithm.
	 *
	 * @param casNr
	 * @return
	 */
	public static String fixCASNr(String casNr) {
		Pattern casNrPattern = Pattern.compile("^\\s*(\\d+)-?(\\d{2})-?(\\d)\\s*$");
		Matcher matcher = casNrPattern.matcher(casNr);
		if (!matcher.matches()) {
			return null;
		}
		// check checksum
		char[] checkMe = (matcher.group(1) + matcher.group(2)).toCharArray();
		int sum = 0, checkLen = checkMe.length;
		for (int i = 0; i < checkLen; i++) {
			sum += (checkLen - i) * NumFormatters.safeInt(checkMe[i]);
		}
		if (matcher.group(3).equals(String.valueOf(sum % 10))) {
			return matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3);
		}
		return null;
	}

	/**
	 * called from py to get byte array, not yet supported in Python 2.5.
	 *
	 * @param str
	 * @return
	 */
	public static byte[] getByteArray(String str) {
		if (str != null) {
			return str.getBytes();
		}
		return new byte[]{};
	}

	/**
	 * replace non-breaking spaces by normal ones.
	 *
	 * @param text
	 * @return
	 */
	public static String fixNbsp(String text) {
		if (text != null) {
			text = text.replace("&nbsp;", " ");
			text = text.replace(String.valueOf((char) 160), " ");
			text = text.replace(String.valueOf((char) 8197), " ");
		}
		return text;
	}

	public static String fixLineEnds(String text) {
		if (text != null) {
			text = text.replace("\r\n", "\n"); // Win
			text = text.replace("\r", "\n"); // Mac
		}
		return text;
	}

	/**
	 * change & to &amp; in URL, leaving existing &amp;
	 *
	 * @param url
	 * @return
	 */
	public static String fixUrl(String url) {
		if (url != null) {
			url = url.replace("&", "&amp;");
			url = url.replace("&amp;amp;", "&amp;");
		}
		return url;
	}

	public static String fixQuot(String text) {
		return "&quot;" + StringEscapeUtils.escapeHtml4(text) + "&quot;";
	}

	public static String fixStr(byte[] text) {
		return fixStr(new String(text));
	}

	public static String fixStr(Object text) {
		if (text == null) {
			text = "";
		}
		return fixStr(text.toString());
	}

	public static String fixStr(String text) {
		// leave Unicode intact
		return "\"" + ESCAPE_JAVA.translate(text) + "\"";
	}

	public static String ifNotEmpty(String prefix, Object value) {
		return ifNotEmpty(prefix, value, "", "");
	}

	public static String ifNotEmpty(String prefix, Object value, String suffix) {
		return ifNotEmpty(prefix, value, suffix, "");
	}

	public static String ifNotEmpty(String prefix, Object value, String suffix, String defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		String strValue = value.toString();
		if (StringUtils.isBlank(strValue)) {
			return defaultValue;
		}
		return prefix + value + suffix;
	}

	public static String fixStr(Integer value) {
		return fixStr(String.valueOf(value));
	}

	public static String fixStr(Double value) {
		return fixStr(String.valueOf(value));
	}

	public static String fixStr00(String text) {
		int zeropos = text.indexOf("\0");
		if (zeropos < 0) {
			return text;
		}
		return text.substring(0, zeropos);
	}

	public static String removeStart(String str, String... remove) {
		for (int i = 0; i < remove.length; i++) {
			str = StringUtils.removeStart(str, remove[i]);
		}
		return str;
	}

	public static String removeEnd(String str, String... remove) {
		for (int i = 0; i < remove.length; i++) {
			str = StringUtils.removeEnd(str, remove[i]);
		}
		return str;
	}
}
