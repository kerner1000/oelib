/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package common.formatting;

import java.util.regex.Matcher;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import common.system.utils.ScriptingSupport;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * Tools for easily getting numbers out of most anything that can be interpreted
 * as number. If not, functions return 0 or 0.0, respectively.
 *
 * @author fr
 */
public class NumFormatters {

	protected static ScriptEngine JS_ENGINE = ScriptingSupport.getJavascriptInterpreter();
	protected static Pattern EXPR_INT = Pattern.compile("[\\-\\d]+", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL);
	protected static Pattern EXPR_INT_STRICT = Pattern.compile("^[\\-\\d]+", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL);
	protected static Pattern EXPR_DOUBLE = Pattern.compile("\\-?\\d*[\\.\\d]\\d*[eE]?\\-?\\d*", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL);
	protected static Pattern EXPR_DOUBLE_STRICT = Pattern.compile("^\\-?\\d*[\\.\\d]\\d*[eE]?\\-?\\d*", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL);
	//																8211 8722 
	private static final String[] SEARCH_NUMBER_MINUS = new String[]{"–", "−", "&minus;", "&#8211;", "&#8722;", "&#x2212;", "&hyphen;", "&dash;", "&#8208;", "&#x2010;"};
	private static final String[] SEARCH_NUMBER_REMOVE = new String[]{" ", "&#160;", "&#x00A0;", "&#x00a0;", "&nbsp;"};

	public static Boolean safeBoolWithNull(String string) {
		if (string == null) {
			return null;
		}
		string = string.trim().toLowerCase();
		if (string.isEmpty() || string.equals("0") || string.equals("0.0") || string.equals("false") || string.equals("no")) {
			return false;
		} else if (string.equals("1") || string.equals("1.0") || string.equals("true") || string.equals("yes")) {
			return true;
		}
		return null;
	}

	public static Boolean safeBoolWithNull(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Number) {
			Double dbl = ((Number) obj).doubleValue();
			if (dbl == 1.0) {
				return true;
			}
			if (dbl == 0.0) {
				return false;
			}
			return null;
		}
		if (obj instanceof Boolean) {
			return (Boolean) obj;
		}
		return safeBoolWithNull(obj.toString());
	}

	public static boolean safeBool(Boolean obj) {
		return Boolean.TRUE.equals(obj);
	}

	public static boolean safeBool(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Boolean) {
			return (Boolean) obj;
		}
		if (obj instanceof Number) {
			return safeBool(((Number) obj).doubleValue());
		}
		return safeBool(obj.toString());
	}

	/**
	 * interpret a String (GET parameter for instance) as true or false.
	 *
	 * @param string
	 * @return
	 */
	public static boolean safeBool(String string) {
		if (string == null) {
			return false;
		}
		string = string.trim().toLowerCase();
		if (string.isEmpty()
				|| string.equals("0")
				|| string.equals("false")
				|| string.equals("no")
				|| string.equals("null")
				|| string.equals("undefined")) {
			return false;
		}
		return true;
	}

	public static boolean safeBool(Integer integer) {
		if (integer == null || integer == 0) {
			return false;
		}
		return true;
	}

	public static boolean safeBool(Double dbl) {
		if (dbl == null || (dbl > -0.5 && dbl < 0.5)) {
			return false;
		}
		return true;
	}

	/**
	 * safely convert an object (presumably a String) into an int.
	 *
	 * @param integer
	 * @return
	 */
	public static int safeInt(Object integer) {
		Integer safeIntWithNull = safeIntWithNull(integer);
		return (safeIntWithNull == null ? 0 : safeIntWithNull);
	}

	/**
	 * safely convert a double into an int.
	 *
	 * @param integer
	 * @return
	 */
	public static int safeInt(Double integer) {
		if (integer != null) {
			return (int) Math.round(integer);
		}
		return 0;
	}

	/**
	 * null-safe interpretation of an Integer.
	 *
	 * @param integer
	 * @return
	 */
	public static int safeInt(Integer integer) { // returns int value or 0
		if (integer != null) {
			return integer;
		}
		return 0;
	}

	/**
	 * safely convert a String into an int.
	 *
	 * @param string
	 * @return
	 */
	public static int safeInt(String string) { // returns int value or 0
		if (string == null || string.isEmpty()) {
			return 0;
		}
		Integer safeIntWithNull = safeIntWithNull(string);
		return (safeIntWithNull == null ? 0 : safeIntWithNull);
	}

	public static Integer safeIntWithNull(Object number) {
		return safeIntWithNull(number, true);
	}

	public static Integer safeIntWithNull(Object number, boolean tolerant) {
		if (number == null) {
			return null;
		}
		if (number instanceof Integer) {
			return (Integer) number;
		}
		if (number instanceof Short) {
			return ((Short) number).intValue();
		}
		if (number instanceof Long) {
			return ((Long) number).intValue();
		}
		if (number instanceof Number) {
			return safeInt(((Number) number).doubleValue());
		}
		if (number instanceof Boolean) {
			return (((Boolean) number) == true ? 1 : 0);
		}
		return safeIntWithNull(number.toString(), tolerant);
	}

	public static Integer safeIntWithNull(String string) {
		return safeIntWithNull(string, true);
	}

	/**
	 * return Integer with value or null.
	 *
	 * @param string
	 * @param tolerant: if true, a suitable number anywhere is accepted
	 * @return
	 */
	public static Integer safeIntWithNull(String string, boolean tolerant) {
		Integer retval = null;
		try {
			// try simple way
			string = prepareNumberString(string);
			retval = Integer.parseInt(string);
		} catch (Exception e) {
			try {
				// try slow and difficult way using RE
				Matcher intMatcher = (tolerant ? EXPR_INT : EXPR_INT_STRICT).matcher(string);
				if (intMatcher.find()) {
					retval = Integer.parseInt(intMatcher.group(0));
				}
			} catch (Exception e2) {
				// irrelevant
			}
		}
		return retval;
	}

	/**
	 * safely convert an object (presumably a String) into a long.
	 *
	 * @param longNum
	 * @return
	 */
	public static long safeLong(Object longNum) {
		Long safeLongWithNull = safeLongWithNull(longNum);
		return (safeLongWithNull == null ? 0 : safeLongWithNull);
	}

	/**
	 * safely convert a double into a long.
	 *
	 * @param longNum
	 * @return
	 */
	public static long safeLong(Double longNum) {
		if (longNum != null) {
			return safeLong((long) Math.round(longNum));
		}
		return 0;
	}

	/**
	 * null-safe longerpretation of a Long.
	 *
	 * @param longNum
	 * @return
	 */
	public static long safeLong(Long longNum) { // returns long value or 0
		if (longNum != null) {
			return longNum;
		}
		return 0;
	}

	/**
	 * safely convert a String into a long.
	 *
	 * @param string
	 * @return
	 */
	public static long safeLong(String string) { // returns long value or 0
		if (string == null || string.isEmpty()) {
			return 0;
		}
		Long safeLongWithNull = safeLongWithNull(string);
		return (safeLongWithNull == null ? 0 : safeLongWithNull);
	}

	public static Long safeLongWithNull(Object number) {
		if (number == null) {
			return null;
		}
		if (number instanceof Long) {
			return (Long) number;
		}
		if (number instanceof Short) {
			return ((Short) number).longValue();
		}
		if (number instanceof Integer) {
			return ((Integer) number).longValue();
		}
		if (number instanceof Number) {
			return safeLong(((Number) number).doubleValue());
		}
		if (number instanceof Boolean) {
			return (((Boolean) number) == true ? 1L : 0L);
		}
		return safeLongWithNull(number.toString());
	}

	/**
	 * return Long with value or null.
	 *
	 * @param string
	 * @return
	 */
	public static Long safeLongWithNull(String string) {
		Long retval = null;
		try {
			// try simple way
			string = prepareNumberString(string);
			retval = Long.parseLong(string);
		} catch (Exception e) {
			try {
				// try slow and difficult way using RE
				Matcher longMatcher = EXPR_INT.matcher(string);
				if (longMatcher.find()) {
					retval = Long.parseLong(longMatcher.group(0));
				}
			} catch (Exception e2) {
				// irrelevant
			}
		}
		return retval;
	}

	public static Double safeDoubleWithNull(Object number) {
		return safeDoubleWithNull(number, true);
	}

	public static Double safeDoubleWithNull(Object number, boolean tolerant) {
		if (number == null || number.equals(Double.NaN)) {
			return null;
		}
		if (number instanceof Double) {
			return (Double) number;
		}
		if (number instanceof Number) {
			return ((Number) number).doubleValue();
		}
		if (number instanceof Boolean) {
			return (((Boolean) number) == true ? 1.0 : 0.0);
		}
		return safeDoubleWithNull(number.toString(), tolerant);
	}

	public static Double safeDoubleWithNull(String string) {
		return safeDoubleWithNull(string, true);
	}

	/**
	 * return Double with value or null.
	 *
	 * @param string
	 * @param tolerant: if true, a suitable number anywhere is accepted
	 * @return
	 */
	public static Double safeDoubleWithNull(String string, boolean tolerant) {
		Double retval = null;
		try {
			// try simple way
			string = prepareNumberString(string);
			retval = Double.valueOf(string);
		} catch (Exception e) {
			if (tolerant) {
				if ("true".equalsIgnoreCase(string)) {
					retval = 1.0;
				} else if ("false".equalsIgnoreCase(string)) {
					retval = 0.0;
				} else {
					try {
						// try slow and difficult way using RE
						int // firstDotIndex = string.indexOf("."),
								firstCommaIndex = string.indexOf(","),
								lastDotIndex = string.lastIndexOf(".");
						// lastCommaIndex = string.lastIndexOf(",");
						if (firstCommaIndex >= 0) { // contains comma, otherwise nothing to do at all
							if (lastDotIndex > firstCommaIndex) {// dot is also present, remove any commas
								// case 1,000.1234
								string = StringUtils.remove(string, ",");
							} else { // remove dots, replace commas by dot
								// case ca. 0,887
								string = StringUtils.remove(string, ".").replace(',', '.');
							}
						}
						//string = string.replace(",", (string.contains(".") ? "" : ".")); // if dot is also present, remove comma, otherwise replace by dot
						Matcher doubleMatcher = (tolerant ? EXPR_DOUBLE : EXPR_DOUBLE_STRICT).matcher(string);
						if (doubleMatcher.find()) {
							retval = Double.valueOf(doubleMatcher.group());
						}
					} catch (Exception e2) {
						// irrelevant
					}
				}
			}
		}
		return retval;
	}

	protected static String prepareNumberString(String string) {
		if (string != null) {
			string = StringFormatters.replaceEach(StringFormatters.replaceEach(string, SEARCH_NUMBER_MINUS, "-"), SEARCH_NUMBER_REMOVE, "");
		}
		return string;
	}

	public static Double evalToDouble(String expression) {
		if (expression == null) {
			return null;
		}
		expression = expression.trim();
		if (expression.isEmpty()) {
			return null;
		}
		expression = expression.
				replaceAll("x", "*"). // allow multiplication using x
				replaceAll("[a-zA-Z]\\s", ""). // prevent function calls
				replaceAll(",", ".").
				replaceAll("%", "*0.01");
		try {
			return safeDoubleWithNull(JS_ENGINE.eval(expression));
		} catch (ScriptException ex) {
			// fail silently
		}
		return null;
		/*
		 * Problem with integer division, 2/100 => 0.0 // use python for eval
		 * return ScriptingSupport.pyEval(eval, expression);
		 * //------------------------------------------ Interpreter bsh = new
		 * Interpreter(); Object result_obj; try { result_obj =
		 * bsh.eval(expression); } catch (EvalError ex) {
		 * Logger.getLogger(NumFormatters.class.getName()).log(Level.SEVERE,
		 * null, ex); return null; } if (result_obj instanceof Integer) { return
		 * Double.valueOf((Integer) result_obj); } if (result_obj instanceof
		 * Double) { return (Double) result_obj; } return null; // return
		 * safeDoubleWithNull(result_obj.toString());
		 *
		 */
	}

	public static double safeDouble(Object number) { // returns double value or 0.0
		Double safeDoubleWithNull = safeDoubleWithNull(number);
		return (safeDoubleWithNull == null ? 0 : safeDoubleWithNull);
	}

	/**
	 * null-safe interpretation of a Double as double.
	 *
	 * @param number
	 * @return
	 */
	public static double safeDouble(Double number) { // returns double value or 0.0
		if (number != null && !number.equals(Double.NaN)) {
			return number;
		}
		return 0.0;
	}

	/**
	 * null-safe interpretation of an Integer as double.
	 *
	 * @param number
	 * @return
	 */
	public static double safeDouble(Integer number) { // returns double value or 0
		if (number != null) {
			return number;
		}
		return 0.0;
	}

	public static Short safeShortWithNull(Object number) {
		Integer retval = safeIntWithNull(number);
		if (retval == null) {
			return null;
		}
		return retval.shortValue();
	}

	public static short safeShort(Object number) {
		Short safeShortWithNull = safeShortWithNull(number);
		return (safeShortWithNull == null ? 0 : safeShortWithNull);
	}

	/**
	 * safely convert a String into a Double.
	 *
	 * @param string
	 * @return
	 */
	public static double safeDouble(String string) { // returns double value or 0
		if (string == null || string.isEmpty()) {
			return 0.0;
		}
		Double safeDoubleWithNull = safeDoubleWithNull(string);
		return (safeDoubleWithNull == null ? 0 : safeDoubleWithNull);
	}

	public static Number constrainValue(Number value, Number min, Number max) {
		if (min != null && max != null && min.doubleValue() > max.doubleValue()) {
			return constrainValueBackend(value, max, min); // swapped
		}
		return constrainValueBackend(value, min, max);
	}

	public static Number constrainValueStrict(Number value, Number min, Number max) {
		if (value == null || (min != null && max != null && min.doubleValue() > max.doubleValue())) { // paradox condition leads to null
			return null;
		}
		return constrainValueBackend(value, min, max);
	}

	private static Number constrainValueBackend(Number value, Number min, Number max) {
		if (min != null && value.doubleValue() < min.doubleValue()) {
			return min;
		}
		if (max != null && value.doubleValue() > max.doubleValue()) {
			return max;
		}
		return value;
	}
}
