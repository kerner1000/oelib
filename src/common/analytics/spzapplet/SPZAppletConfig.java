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