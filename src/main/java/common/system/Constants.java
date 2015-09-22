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
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package common.system;

/**
 * Common constants for OE2 and SPZApplet.
 * @author fr
 */
public class Constants {

    public static final String GENERIC = "generic";
	public static final String UTF8 = "UTF-8";
    public static final int STATUS_NOT_YET_DOWNLOADED = 1;
    public static final int STATUS_LOCKED = 2;
    public static final int STATUS_EXTRACTED = 3;
    public static final int STATUS_UPLOADED = 4;
    public static final int STATUS_CANCELED = 5;
    public static final int STATUS_BEFORE_UPLOAD = 6;
    public static final int STATUS_BEFORE_CANCEL = 7;
    public static final String[] DECOMPRESS_EXTENSION_BLACKLIST = new String[]{"odt", "ods", "odp", "odg", "odc", "odf", "odi", "odm",
        "ott", "ots", "otp", "otg",
        "docx", "xlsx", "pptx", // OOXML documents are also ZIP files
        "docm", "xlsm", "pptm"};
}
