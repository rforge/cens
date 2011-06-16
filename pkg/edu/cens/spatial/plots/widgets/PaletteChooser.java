package edu.cens.spatial.plots.widgets;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Neal
 * Date: 3/25/11
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class PaletteChooser extends JList{

    enum BrewerPalette {
        Blues, BuGn, BuPu, GnBu, Greens, Greys, Oranges,
        OrRd, PuBu, PuBuGn, PuRd, Purples, RdPu, Reds, YlGn, YlGnBu, YlOrBr, YlOrRd,
        BrBG, PiYG, PRGn, PuOr, RdBu, RdGy, RdYlBu, RdYlGn, Spectral,
        Accent,
        Dark2,
        Paired,
        Pastel1,
        Pastel2,
        Set1,
        Set2,
        Set3,
    }

    public PaletteChooser() {
        super(BrewerPalette.values());
        setSelectedValue(null, false);
        setVisibleRowCount(5);
    }

    public String getPalette() {
        Object sel = getSelectedValue();
        return sel == null ? "" : ((BrewerPalette) sel).name();
    }

    public JScrollPane getBPC() {
        JScrollPane jScrollPane = new JScrollPane(this);
        jScrollPane.setBorder(BorderFactory.createTitledBorder("Palette:"));
        return jScrollPane;
    }

}
