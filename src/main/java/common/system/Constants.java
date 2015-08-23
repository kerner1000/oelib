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
