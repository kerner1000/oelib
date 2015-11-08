package common.analytics.spzapplet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Helper class used by servlet classes and the SPZApplet.
 * @author fr
 */
public class SPZSupport {

    public static final String UPLOAD_STAGE_DOWNLOAD = "download";
    public static final String UPLOAD_STAGE_UPLOAD = "upload";
    public static final String UPLOAD_STAGE_CONFIG = "config";
    public static final String UPLOAD_STAGE_CANCEL = "cancel";
    public static final String RESULT_SUCCESS = "success";
    public static final String RESULT_LOCK_MISSING = "lockMissing";
    public static final String RESULT_PERMISSION_DENIED = "permissionDenied";
    public static final String RESULT_ENTITY_NOT_FOUND = "notFound";
    public static final String RESULT_COMPRESS_FAILURE = "compressFailure";
    public static final String RESULT_COMMAND_NOT_FOUND = "commandNotFound";
    public static final String RESULT_FAILURE = "failure"; // reason unspecified
    public static final String SPZFILE = "spzfile";
    public static final String SCRIPT_FILE_ENCODING = "UTF-8";
    public static final String SCRIPT_EXTENSION = "bsh";
    public static final String SIGNATURE_EXTENSION = "sgn";
    public static final String HASH_ALGORITHM = "SHA-256";
}
