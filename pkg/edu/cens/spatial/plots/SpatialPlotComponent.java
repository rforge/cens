package edu.cens.spatial.plots;

import edu.cens.spatial.plots.components.AbstractComponentPanel;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Neal
 * Date: 1/13/11
 * Time: 8:31 AM
 */
public class SpatialPlotComponent implements IPlotComponent<SpatialPlotComponentType>{

    private final SpatialPlotComponentType _type;
    private final Map<String, String> _args = new HashMap<String, String>();
    private boolean _active = true;




    public SpatialPlotComponent(SpatialPlotComponentType type) {
        _type = type;
    }

    public String getCall() {
        return _type.getCall(getArgs());
    }

    public SpatialPlotComponentType getType() {
        return _type;
    }

    public Map<String, String> getArgs() {
        return new HashMap<String, String>(_args);
    }

    public void setArgs(Map<String, String> args) {
        _args.clear();
        _args.putAll(args);
    }

    public boolean isActive() {
        return _active;
    }

    public void setActive(boolean active) {
        _active = active;
    }

    public AbstractComponentPanel  getPanel() {
        return _type.getPanel(getArgs());
    }

    public JPanel renderForList() {
        return _type.renderForList(_active);
    }

    //**************************
    // Transferable methods
    //**************************

    private static final DataFlavor[] _flavor
            = new DataFlavor[]{new DataFlavor(SpatialPlotComponent.class, "Spatial Plot Component")};

    public static final DataFlavor getFlavor() { return _flavor[0]; }

    public DataFlavor[] getTransferDataFlavors() {
        return _flavor;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return _flavor[0].equals(flavor);
    }

    public SpatialPlotComponent getTransferData(DataFlavor flavor)  {
        return this;
    }


    @Override
    public String toString() {
        return super.toString() + "[" + getType().name()+ "]";
    }
}
