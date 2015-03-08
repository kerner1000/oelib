/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common.system.utils;

/**
 *
 * @author fr
 */
public class UrlSupport {

	public static String addParameter(String url, String parameterPart) {
		if (url != null && parameterPart != null) {
			url += (url.contains("?") ? "&" : "?") + parameterPart;
		}
		return url;
	}
}
