package edu.cens.spatial.plots;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Neal
 * Date: 1/10/11
 * Time: 8:57 PM
 */

public interface IPlotModel<T extends IPlotComponent> extends Cloneable {
    public String getCall();
    public IPlotModel<T> clone();
    public boolean validate();
    public boolean add(T component);
    public void insertElementAt(T component, int index);
    public T remove(int index);
    public T getElementAt(int index);
}
