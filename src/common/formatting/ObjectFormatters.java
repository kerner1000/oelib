/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common.formatting;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 * Utility function related to Objects (!= String, Number).
 *
 * @author fr
 */
public class ObjectFormatters {

	/**
	 * Read a serialized object from stream and close it, return the object.
	 *
	 * @param inputStream
	 * @return
	 */
	public static Object unserialize(InputStream inputStream) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(inputStream));
			Object retval = ois.readObject();
			ois.close();
			IOUtils.closeQuietly(inputStream);
			return retval;
		} catch (Exception ex) {
			Logger.getLogger(ObjectFormatters.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * Serialize an object to a byte[].
	 *
	 * @param object
	 * @return
	 */
	public static byte[] serialize(Object object) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(object);
			oos.flush();
			oos.close();
			byte[] retval = bos.toByteArray();
			bos.close();
			return retval;
		} catch (Exception ex) {
			Logger.getLogger(ObjectFormatters.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
}
