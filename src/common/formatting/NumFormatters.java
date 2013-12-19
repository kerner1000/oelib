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

/**
 * Tools for easily getting numbers out of most anything that can be interpreted
 * as number. If not, functions return 0 or 0.0, respectively.
 * <p/>
 * @author fr
 */
public class NumFormatters {

	protected static ScriptEngine JS_ENGINE = ScriptingSupport.getJavascriptInterpreter();
	protected static Pattern EXPR_INT = Pattern.compile("[\\-\\d]+", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL);
	protected static Pattern EXPR_DOUBLE = Pattern.compile("\\-?\\d*[\\.\\d]\\d*[eE]?\\d*", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL);

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
		if (obj instanceof Double) {
			return safeBool((Double) obj);
		}
		if (obj instanceof Integer) {
			return safeBool((Integer) obj);
		}
		if (obj instanceof Boolean) {
			return (Boolean) obj;
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
		if (integer == null || Boolean.FALSE.equals(integer)) {
			return 0;
		} else if (Boolean.TRUE.equals(integer)) {
			return 1;
		} else {
			return safeInt(String.valueOf(integer));
		}
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
		Integer retval = 0;
		try {
			retval = Integer.parseInt(string.trim());
		} catch (Exception e) {
			try {
				// try slow and difficult way using RE
				Matcher int_matcher = EXPR_INT.matcher(string);
				if (int_matcher.find()) {
					retval = Integer.parseInt(int_matcher.group(0));
				}
			} catch (Exception e2) {
				// irrelevant
			}
		}
		return safeInt(retval);
	}

	public static Integer safeIntWithNull(Object number) {
		if (number == null) {
			return null;
		}
		if (number instanceof Double) {
			return safeInt((Double) number);
		}
		if (number instanceof Integer) {
			return (Integer) number;
		}
		if (number instanceof Boolean) {
			return (((Boolean) number) == true ? 1 : 0);
		}
		return safeIntWithNull(number.toString());
	}

	/**
	 * return Integer with value or null.
	 *
	 * @param string
	 * @return
	 */
	public static Integer safeIntWithNull(String string) {
		Integer retval = null;
		try {
			// try simple way
			retval = Integer.parseInt(string);
		} catch (Exception e) {
			try {
				// try slow and difficult way using RE
				Matcher int_matcher = EXPR_INT.matcher(string);
				if (int_matcher.find()) {
					retval = Integer.parseInt(int_matcher.group(0));
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
		if (longNum == null) {
			return 0;
		} else {
			return safeLong(String.valueOf(longNum));
		}
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
		Long retval = 0L;
		try {
			retval = Long.parseLong(string.trim());
		} catch (Exception e) {
			try {
				// try slow and difficult way using RE
				Matcher long_matcher = EXPR_INT.matcher(string);
				if (long_matcher.find()) {
					retval = Long.parseLong(long_matcher.group(0));
				}
			} catch (Exception e2) {
				// irrelevant
			}
		}
		return safeLong(retval);
	}

	public static Long safeLongWithNull(Object number) {
		if (number == null) {
			return null;
		}
		if (number instanceof Double) {
			return safeLong((Double) number);
		}
		if (number instanceof Long) {
			return (Long) number;
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
		if (number == null) {
			return null;
		}
		if (number instanceof Double) {
			return (Double) number;
		}
		if (number instanceof Integer) {
			return ((Integer) number) + 0.0;
		}
		if (number instanceof Boolean) {
			return (((Boolean) number) == true ? 1.0 : 0.0);
		}
		return safeDoubleWithNull(number.toString());
	}

	/**
	 * return Double with value or null.
	 *
	 * @param string
	 * @return
	 */
	public static Double safeDoubleWithNull(String string) {
		Double retval = null;
		try {
			// try simple way
			retval = Double.valueOf(string);
		} catch (Exception e) {
			try {
				// try slow and difficult way using RE
				string = string.replaceAll(",", (string.contains(".") ? "" : ".")); // if dot is also present, remove comma, otherwise replace by dot
				Matcher doubleMatcher = EXPR_DOUBLE.matcher(string);
				if (doubleMatcher.find()) {
					retval = Double.valueOf(doubleMatcher.group());
				}
			} catch (Exception e2) {
				// irrelevant
			}
		}
		return retval;
	}

	public static Double evalToDouble(String expression) {
		if (expression == null) {
			return null;
		}
		expression = expression.trim();
		if (expression.isEmpty()) {
			return null;
		}
		expression = expression.replaceAll("[a-zA-Z]\\s", ""). // prevent function calls
				replaceAll(",", ".").
				replaceAll("%", "*0.01");
		Object resultObj;
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
		if (number == null) {
			return 0.0;
		}
		if (number instanceof Double) {
			return (Double) number;
		}
		if (number instanceof Integer) {
			return ((Integer) number) + 0.0;
		}
		if (number instanceof Boolean) {
			return (((Boolean) number) == true ? 1.0 : 0.0);
		}
		return safeDouble(number.toString());
	}

	/**
	 * null-safe interpretation of a Double as double.
	 *
	 * @param number
	 * @return
	 */
	public static double safeDouble(Double number) { // returns double value or 0.0
		if (number != null) {
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
		if (number == null) {
			return 0.0;
		}
		try {
			Double retval = Double.valueOf(number);
			return safeDouble(retval);
		} catch (Exception e) {
			// irrelevant
		}
		return 0.0;
	}

	public static Short safeShortWithNull(Object number) {
		Double retval = safeDoubleWithNull(number);
		if (retval == null) {
			return null;
		}
		return retval.shortValue();
	}

	public static short safeShort(Object number) {
		return (short) safeDouble(number);
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
		Double retval = 0.0;
		try {
			retval = Double.valueOf(string.trim());
			return safeDouble(retval);
		} catch (Exception e) {
			try {
				// try slow and difficult way using RE
				string = string.replaceAll(",", ".");
				Matcher doubleMatcher = EXPR_DOUBLE.matcher(string);
				if (doubleMatcher.find()) {
					retval = Double.valueOf(doubleMatcher.group());
				}
			} catch (Exception e2) {
				// irrelevant
			}
		}
		return retval;
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
