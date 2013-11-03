/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common.analytics.spzapplet;

import java.io.Serializable;

/**
 * An object to store a signed BSH script.
 * @author fr
 */
public class SignedString implements Serializable {

    private String code;
    private byte[] signature;

    public SignedString(String code, byte[] signature) {
        this.code = code;
        this.signature = signature;
    }

    public String getCode() {
        return code;
    }

    public byte[] getSignature() {
        return signature;
    }
}
