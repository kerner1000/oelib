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
package common.analytics.spzapplet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;
import java.util.List;

/**
 * Class to hold the config for @SPZApplet.
 * @author fr
 */
public class SPZAppletConfig implements Serializable {

    public boolean preferShell;
    public List<String> shellOpenFiles;
    public List<String> patterns;
    public SignedString bshPrepare;
    public SignedString bshFinish;
}
