/*******************************************************************************
 * Copyright 2015 Felix Rudolphi
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
