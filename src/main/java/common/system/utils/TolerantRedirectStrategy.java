/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common.system.utils;

import java.net.URI;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectStrategy;

/**
 * Redirect strategy used in Apache HTTP client, which is more tolerant.
 * @author fr
 */
public class TolerantRedirectStrategy extends DefaultRedirectStrategy {

    @Override
    protected URI createLocationURI(String location) throws ProtocolException {
        return super.createLocationURI(DownloadByServer.encodeURL(location));
    }
}
