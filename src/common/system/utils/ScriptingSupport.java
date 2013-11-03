/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common.system.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * This library provides functions to create Python and Javascript interpreters,
 * and helper functions to convert Python objects to Java objects.
 * @author fr
 */
public class ScriptingSupport {

	/**
	 * Call factory method to create a Javascript interpreter. 
	 * @return 
	 */
	public static ScriptEngine getJavascriptInterpreter() {
		ScriptEngineManager mgr = new ScriptEngineManager();
		return mgr.getEngineByName("JavaScript");
	}
}
