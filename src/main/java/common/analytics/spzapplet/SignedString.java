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
