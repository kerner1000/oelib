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
