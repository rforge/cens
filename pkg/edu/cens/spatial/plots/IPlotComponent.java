package edu.cens.spatial.plots;

import javax.swing.*;
import java.awt.datatransfer.Transferable;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Neal
 * Date: 1/13/11
 * Time: 8:54 AM
 * To change this template use File | Settings | File Templates.
 */


public interface IPlotComponent<T extends IPlotComponentType> extends Transferable {

    public abstract String getCall();

    public abstract T getType();

    public abstract Map<String, String> getArgs();
    public abstract void setArgs(Map<String, String> args);

    public boolean isActive();
    public void setActive(boolean b);

}
