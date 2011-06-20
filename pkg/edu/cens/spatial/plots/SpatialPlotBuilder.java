package edu.cens.spatial.plots;


import java.util.*;

import edu.cens.spatial.plots.components.AbstractComponentPanel;
import edu.cens.spatial.plots.components.ColorOptionPanel;
import edu.cens.spatial.plots.components.OptionsComponentPanel;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.OsmFileCacheTileLoader;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;
import org.rosuda.JGR.layout.AnchorConstraint;
import org.rosuda.JGR.layout.AnchorLayout;
import org.rosuda.JGR.toolkit.FileSelector;
import org.rosuda.deducer.Deducer;
import org.rosuda.deducer.WindowTracker;
import org.rosuda.deducer.toolkit.HelpButton;
import org.rosuda.deducer.toolkit.IconButton;
import org.rosuda.deducer.toolkit.OkayCancelPanel;
import org.rosuda.ibase.toolkit.EzMenuSwing;
import org.rosuda.ibase.toolkit.TJFrame;
import org.rosuda.javaGD.PlotPanel;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class SpatialPlotBuilder extends TJFrame implements ActionListener, WindowListener  {

    private final JLayeredPane  _pane = new JLayeredPane();
    private final JPanel _rightPanel = new JPanel();
    private final JPanel _topPanel = new JPanel();
    private final OkayCancelPanel _okayCancel = new OkayCancelPanel(true,true,this);
    private final JTabbedPane _addTabs = new JTabbedPane();

    private final JList _elementsList = new JList();

    private final JPanel _shadow = new JPanel();
    private final JPanel _background = new JPanel();
    private final JPanel _plotHolder = new JPanel();


    private PlotPanel _device;


    private Map<String, ListModel> _addElementListModels = new LinkedHashMap<String, ListModel>();

    private SpatialPlotModel _model;
    private static SpatialPlotModel _lastModel;

    private JCheckBoxMenuItem _axes1 = new JCheckBoxMenuItem("Axes");
    private ButtonGroup _backgroundGroup = new ButtonGroup();
	private ViewPanel _vp;


    public SpatialPlotBuilder() {
        this(_lastModel ==null ? new SpatialPlotModel() : _lastModel.clone());
    }

    public SpatialPlotBuilder(SpatialPlotModel pbm) {
        try{
            setModel(pbm);
            init();
            initGUI();
            updatePlot();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void init(){
        for(SpatialPlotComponentType o : SpatialPlotComponentType.values()) {
            String category = o.getCategory();
            DefaultListModel lm = (DefaultListModel) _addElementListModels.get(category);
            if(lm == null) _addElementListModels.put(category, lm = new DefaultListModel());
            lm.addElement(o);
        }
    }


    private void initGUI() {
        try {
            Toolkit.getDefaultToolkit().setDynamicLayout(true);
            AnchorLayout thisLayout = new AnchorLayout();
            _pane.setLayout(thisLayout);

            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            {


                _topPanel.setLayout(new AnchorLayout());
                _pane.add(_topPanel, new AnchorConstraint(1, 0, 100, 0,
                        AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_ABS,
                        AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_REL));
                _pane.setLayer(_topPanel, 10);
                _topPanel.setPreferredSize(new java.awt.Dimension(683, 102));



                _pane.add(_shadow, new AnchorConstraint(1, 1, 164, 1,
                        AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_ABS,
                        AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_REL));
                _pane.setLayer(_shadow, 9);
                _shadow.setPreferredSize(new java.awt.Dimension(683, 283));
                _shadow.setBackground(new Color(105,105,105));
                _shadow.setVisible(false);

                _pane.add(_background, new AnchorConstraint(0, 0, 0, 0,
                        AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS,
                        AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS));
                _pane.setLayer(_background, -1000);


                {
                    JPanel addPanel = new JPanel();
                    BorderLayout addPanelLayout = new BorderLayout();
                    addPanel.setLayout(addPanelLayout);
                    _topPanel.add(addPanel, new AnchorConstraint(11, 5, 996, 5,
                            AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_ABS,
                            AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_ABS));
                    addPanel.setPreferredSize(new java.awt.Dimension(659, 124));

                    {

                        addPanel.add(_addTabs, BorderLayout.CENTER);
                        _addTabs.setPreferredSize(new java.awt.Dimension(659, 130));


                        {


                            for(Map.Entry<String, ListModel> entry : _addElementListModels.entrySet()){
                                String name = entry.getKey();
                                ListModel mod = entry.getValue();

                                JPanel panel = new JPanel();
                                panel.setLayout(new BorderLayout());

                                _addTabs.add(name, panel);

                                JScrollPane scroller = new JScrollPane();
                                scroller.setHorizontalScrollBarPolicy(
                                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);


                                panel.add(scroller);

                                JList list = new JList();

//                                list.setCellRenderer(new ElementListRenderer());
                                list.setCellRenderer(new DefaultListCellRenderer(){

                                    @Override
                                    public Component getListCellRendererComponent(JList list, Object value, 
                                    		int index, boolean isSelected, boolean cellHasFocus) {
                                        JPanel panel = ((SpatialPlotComponentType) value).renderForList(true);
                                        setPanelBgIfSelected(list, isSelected, panel);
                                        return panel;
                                    }
                                });  //todo

                                list.setVisibleRowCount(mod.getSize()/7);
                                list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
                                list.setModel(mod);
                                list.setDragEnabled(true);
                                list.setTransferHandler(new AddElementTransferHandler());
                                list.addMouseListener(new AddMouseListener());

                                scroller.setViewportView(list);

                            }
                        }
                    }
                }
            }


            {

                _plotHolder.setLayout(new BorderLayout());
                _plotHolder.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                JPanel defaultPlotPanel = new JPanel(); //new DefaultPlotPanel(this);
                _plotHolder.add(defaultPlotPanel);
                _pane.add(_plotHolder, new AnchorConstraint(137, 158, 52, 22,
                        AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS,
                        AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_REL),2);
                _plotHolder.setPreferredSize(new java.awt.Dimension(515, 391));
                _plotHolder.setTransferHandler(new PanelTransferHandler());
                //initMap();
            }


            {
                AnchorLayout rightPanelLayout = new AnchorLayout();
                _rightPanel.setLayout(rightPanelLayout);
                _pane.add(_rightPanel, new AnchorConstraint(135, 1000, 52, 731,
                        AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_REL,
                        AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_NONE),4);
                _rightPanel.setPreferredSize(new java.awt.Dimension(160, 389));
                {
                    JButton removeButton = new IconButton("/icons/stop_16.png","Remove component",this,"remove");
                    _rightPanel.add(removeButton, new AnchorConstraint(872, 921, 925, 540,
                            AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL,
                            AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_NONE));
                    removeButton.setPreferredSize(new java.awt.Dimension(21, 21));
                }

                {
                    JButton disableButton = new IconButton("/icons/reload_16.png","Toggle active",this,"active");
                    _rightPanel.add(disableButton, new AnchorConstraint(872, 578, 913, 240,
                            AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_NONE,
                            AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_REL));
                    disableButton.setPreferredSize(new java.awt.Dimension(21, 21));
                }

                {
                    JButton editButton = new IconButton("/icons/edit_16.png","Edit component",this,"edit");
                    _rightPanel.add(editButton, new AnchorConstraint(872, 190, 904, 100,
                            AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_NONE,
                            AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_REL));
                    editButton.setPreferredSize(new java.awt.Dimension(21, 21));
                }
            
                {
                    JPanel elementsPanel = new JPanel();
                    BorderLayout elementsPanelLayout = new BorderLayout();
                    elementsPanel.setLayout(elementsPanelLayout);
                    _rightPanel.add(elementsPanel, new AnchorConstraint(0, 928, 859, 90,
                            AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_REL,
                            AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
                    elementsPanel.setPreferredSize(new java.awt.Dimension(134, 334));
                    elementsPanel.setBorder(BorderFactory.createTitledBorder("Components"));
                    {
                        JScrollPane elementsScroller = new JScrollPane();
                        elementsPanel.add(elementsScroller, BorderLayout.CENTER);
                        {
                        
                            elementsScroller.setViewportView(_elementsList);
                            _elementsList.setCellRenderer(new DefaultListCellRenderer(){
                                public Component getListCellRendererComponent(JList list, Object value, 
                                		int index, boolean isSelected, boolean cellHasFocus) {
                                    SpatialPlotComponent value1 = (SpatialPlotComponent) value;
                                    JPanel 	panel = value1.renderForList();
                                    if(!value1.isActive()){
                                        panel.setBackground(Color.GRAY);
                                    }
                                    setPanelBgIfSelected(list, isSelected, panel);

                                    return panel;                                
                                }
                            });

                            _elementsList.setDragEnabled(true);
                            _elementsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                            _elementsList.addListSelectionListener(new ElementListListener());
                            _elementsList.setTransferHandler(new ElementTransferHandler());
                            _elementsList.addMouseListener(new MouseListener(){
                                public void mouseClicked(MouseEvent e) {
                                    if(e.getClickCount()==2 && _elementsList.getSelectedIndex() >= 0){
                                        SpatialPlotComponent selectedValue = (SpatialPlotComponent) _elementsList.getSelectedValue();
                                        editElement(selectedValue, SpatialPlotBuilder.this);
                                    }
                                }
                                public void mouseEntered(MouseEvent e) {}
                                public void mouseExited(MouseEvent e) {}
                                public void mousePressed(MouseEvent e) {
                                    maybePopup(e);
                                }
                                public void mouseReleased(MouseEvent e) {
                                    maybePopup(e);
                                }

                                public void maybePopup(MouseEvent e){
                                    if(e.isPopupTrigger()){
                                        int i = _elementsList.locationToIndex(e.getPoint());
                                        if(i<0)
                                            return;
                                        ElementPopupMenu.element =  _model.getElementAt(i);
                                        ElementPopupMenu.elList= _elementsList;
                                        ElementPopupMenu.plot=SpatialPlotBuilder.this;
                                        ElementPopupMenu.getPopup().show(e.getComponent(),
                                                e.getX(),e.getY());
                                    }
                                }

                            });

                        }
                    }
                }
            }

            {
                JPanel bottomPanel = new JPanel();
                AnchorLayout bottomPanelLayout = new AnchorLayout();
                bottomPanel.setLayout(bottomPanelLayout);
                _pane.add(bottomPanel, new AnchorConstraint(870, 1000, 1000, 0,
                        AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_REL,
                        AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
                bottomPanel.setPreferredSize(new java.awt.Dimension(688, 59));
                {
                    HelpButton helpButton = new HelpButton("pmwiki.php?n=Main.PlotBuilder");
                    bottomPanel.add(helpButton, new AnchorConstraint(364, 51, 872, 19,
                            AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_NONE,
                            AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
                    helpButton.setPreferredSize(new java.awt.Dimension(36, 36));
                }
                {

                    bottomPanel.add(_okayCancel, new AnchorConstraint(127, 965, 872, 592,
                            AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_REL,
                            AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_NONE));
                    _okayCancel.setPreferredSize(new java.awt.Dimension(267, 39));
                }

            }

            String[] Menu = { "+", "File", "@N New","new", "-", //"@O Open","open", "@S Save","save","-",
                          "+", "Map types", "Open street map", "osm", "Bing Aerial images", "bing",
                          "+","Tools","View call","call",
                          "~Window","0" };
            JMenuBar ezMenu = EzMenuSwing.getEzMenu(this, new MenuListener(), Menu);

            setContentPane(_pane);
            pack();
            this.addWindowListener(this);

            this.setSize(705, 620);
            this.setTitle("Spatial Plot Builder");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void setPanelBgIfSelected(JList list, boolean isSelected, JPanel panel) {
        if (isSelected) {
            panel.setBackground(list.getSelectionBackground());
            panel.setForeground(list.getSelectionForeground());
        } else {
            panel.setBackground(list.getBackground());
            panel.setForeground(list.getForeground());
        }
    }

    public void setModel(SpatialPlotModel m){
        _model = m;
        _elementsList.setModel(m);
        _axes1.setSelected(m.isAxes());

        Enumeration<AbstractButton> elements = _backgroundGroup.getElements();
        int i = 0;

        _backgroundGroup.clearSelection();
        while(elements.hasMoreElements()) {

            _backgroundGroup.setSelected(elements.nextElement().getModel(), i++ == _model.getMapType().ordinal());

        }

    }

    public SpatialPlotModel getModel(){return _model;}

    // stubs for the expanding side panel (not implemented)

    public void openLayerSheet(SpatialPlotComponent element){

    }

    public void closeLayerSheet(){

    }

    public void plot(final String cmd){
        //if(cmd ==null || "".equals(cmd))
        //    return;
        if(_device ==null){
            _plotHolder.removeAll();
            //initMap();
            _device = new PlotPanel(_plotHolder.getWidth(), _plotHolder.getHeight());
            _device.setTransferHandler(new PanelTransferHandler());
            DeviceInterface.register(_device);
            _vp = new ViewPanel(_device,this);
            _plotHolder.add(_vp);
            _vp.printBBox();
        }

        _okayCancel.getApproveButton().setEnabled(false);
        final JLabel lab = new JLabel("plotting...");
        lab.setHorizontalAlignment(JLabel.CENTER);
        lab.setFont(Font.decode("Arial-BOLD-30"));
        lab.setForeground(Color.green);
        _pane.add(lab, new AnchorConstraint(137, 158, 52, 22,
                AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS,
                AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_REL),100);
        _pane.setLayer(lab, 100);
        _pane.validate();
        _pane.repaint();
        final ViewPanel tmp = _vp;
        new Thread(new Runnable(){
            public void run() {
                try{
                    plotInternal(cmd, SpatialPlotBuilder.this._device.devNr,tmp);
                    tmp.printBBox();
                }catch(Exception e){e.printStackTrace();}
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        _pane.remove(lab);
                        _okayCancel.getApproveButton().setEnabled(true);
                        _device.repaint();
                        _pane.validate();
                    }
                });

            }

        }).start();

    }

    public static void plotInternal(String call, int devNr,ViewPanel vp){
        try{
//            Deducer.eval("print('" + call + "')");
            Deducer.eval("Sys.setenv(\"JAVAGD_CLASS_NAME\"=\"edu/cens/spatial/plots/DeviceInterface\")");
            if(devNr ==-1){
                Deducer.eval("JavaGD()");
                Deducer.eval("par(list(bg=\"transparent\")");
            }else{
                Deducer.eval("dev.set(" + (devNr+1) + ")");
            }
            Vector<Double> ul = vp.getUpperLeftCoordinate();
            Vector<Double> lr = vp.getLowerRightCoordinate();
            String cmd = "plot.new()\npar(mar=c(0,0,0,0))\nplot.window(c(" +ul.get(0)+","+lr.get(0)+"),c("+lr.get(1)+","+ul.get(1) +"), xaxs = 'i', yaxs = 'i')";
            System.out.println(cmd);
            Deducer.eval(cmd);
            Deducer.eval("plot(states,add=TRUE)");
            //String cmd = call; //call.replaceAll("[\\n\\t]", " ");
            //System.out.println(cmd);
            //Deducer.eval(cmd);
            Deducer.eval("Sys.setenv(\"JAVAGD_CLASS_NAME\"=\"org/rosuda/JGR/toolkit/JavaGD\")");
        }catch(Exception e){e.printStackTrace();}

    }


    public void updatePlot(){
        if(_fromMain) return; //eg not in R
        String c = _model.getCall();
        plot(c);
    }

    public void addElement(SpatialPlotComponent pe){

        AbstractComponentPanel panel = pe.getPanel();

        showComponentDialog(panel);


        if(panel.isOk() && _model.validate()){
            pe.setArgs(panel.getArgs());
            _model.add(pe);
            if(!_model.validate())
            {
                _model.remove(_model.getSize());
                return;
            }
            SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                updatePlot();
            }

        });
        }


    }

    private void showComponentDialog(final AbstractComponentPanel panel) {
        final JDialog jDialog = new JDialog(this, "Configure Component", true);
        jDialog.add(panel);
        jDialog.setModal(true);
        jDialog.setResizable(false);
        jDialog.setLocationRelativeTo(this);
        jDialog.pack();

        panel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(panel.isOk() || panel.isCancel())
                    jDialog.dispose();
                else JOptionPane.showMessageDialog(panel, panel.getErrorMsg());
            }
        });

        jDialog.setVisible(true);
    }


    class AddElementTransferHandler extends TransferHandler{

        public boolean canImport(JComponent comp,DataFlavor[] d) {
            return true;
        }

        public boolean importData(JComponent comp, Transferable t) {
            return false;
        }

        public int getSourceActions(JComponent c) {
            return COPY;
        }

        protected Transferable createTransferable(JComponent c) {
            try{
                JList list = (JList)c;
                return (Transferable) list.getSelectedValue();
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    class PanelTransferHandler extends TransferHandler{

        public boolean canImport(JComponent comp,DataFlavor[] d) {
            return d.length == 1 && d[0].equals(SpatialPlotComponentType.getFlavor());
        }

        public int getSourceActions(JComponent c) {
            return COPY;
        }

        public boolean importData(JComponent comp, Transferable t) {
            try {
                final SpatialPlotComponentType  type= (SpatialPlotComponentType) t.getTransferData(
                        SpatialPlotComponentType.getFlavor());

                addElement(new SpatialPlotComponent(type));

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class ElementTransferHandler extends PanelTransferHandler{
        public int lastIndex = -1;

        public boolean canImport(JComponent comp,DataFlavor[] d) {
            return super.canImport(comp, d) || d.length==1 && d[0].equals(SpatialPlotComponent.getFlavor());
        }

        public boolean importData(JComponent comp, Transferable t) {

            if(super.canImport(comp, t.getTransferDataFlavors())) return super.importData(comp, t);

            try {
                final JList l = (JList) comp;
                final SpatialPlotModel model = _model;

                final SpatialPlotComponent p = (SpatialPlotComponent) t.getTransferData(SpatialPlotComponent.getFlavor());
                        if(!model.validate()){
//                            PlottingElementDialog d =
//                                new PlottingElementDialog(SpatialPlotBuilder.this,p);
//                            d.setModal(true);
//                            d.setLocationRelativeTo(SpatialPlotBuilder.this);
//                            d.setVisible(true);
                            //todo
                            if(!model.validate())
                                return false;
                        }

                        int ind = l.getSelectedIndex();
                        //if(ind+1 < lastIndex)
                        //	lastIndex++;
                        for(int i = 0; i < model.getSize(); i++)
                        {
                            if(p == model.getElementAt(i)) {
                                model.remove(i);
                                i--;
                            }
                        }

                    if(ind<0)
                        model.add(p);
                    else
                        model.insertElementAt(p, ind);


                SwingUtilities.invokeLater(new Runnable(){
                    public void run() {
                        updatePlot();
                    }});



                ind = l.getSelectedIndex();

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        protected Transferable createTransferable(JComponent c) {
            JList list = (JList)c;
            lastIndex = list.getSelectedIndex();
            return  lastIndex > -1 ? _model.getElementAt(lastIndex) : null;
        }


         public Icon getVisualRepresentation(Transferable t){
            try {
                SpatialPlotComponent p = (SpatialPlotComponent) t.getTransferData(SpatialPlotComponent.getFlavor());
                return p.getType().getIcon();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    class ElementListListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent arg0) {
        }

    }

    static class ElementPopupMenu {
        private static SpatialPlotComponent element;
        private static JList elList;
        private static SpatialPlotBuilder plot;

        private static JPopupMenu getPopup(){
            JPopupMenu popup = new JPopupMenu();
            JMenuItem menuItem = new JMenuItem("Edit");
            menuItem.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent e) {
                    editElement(element, plot);
                }

            });
            popup.add(menuItem);
            menuItem = new JMenuItem("Toggle active");
            menuItem.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent e) {
                    element.setActive(!element.isActive());
                    elList.validate();
                    elList.repaint();
                    plot.updatePlot();
                }

            });
            popup.add(menuItem);

            menuItem = new JMenuItem("Remove");
            menuItem.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent e) {
                    //todo rewrite this crap
                    SpatialPlotModel model = plot.getModel();
                    for(int i = 0; i < model.getSize(); i++) {
                        if(element == model.getElementAt(i))
                        {
                            model.remove(i);
                            break;
                        }
                    }
                    plot.updatePlot();
                }

            });
            popup.add(menuItem);

            menuItem = new JMenuItem("Color...");
            menuItem.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent e) {
                    //todo rewrite this crap
                    ColorOptionPanel panel = new ColorOptionPanel(element.getArgs());
                    plot.showComponentDialog(panel);
                    if(panel.isOk()) {
                        element.setArgs(panel.getArgs());
                        plot.updatePlot();
                    }
                }

            });

            if(element.getType() != SpatialPlotComponentType.shape)
                popup.add(menuItem);


            return popup;
        }


    }

        private static void editElement(SpatialPlotComponent element, SpatialPlotBuilder plot) {
            AbstractComponentPanel panel = element.getPanel();

            plot.showComponentDialog(panel);


            if(panel.isOk() && plot.getModel().validate()){
                element.setArgs(panel.getArgs());
                plot.updatePlot();
            }
        }

    class AddMouseListener implements MouseListener{
        public void mouseClicked(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {
            maybePopup(e,true);
        }
        public void mouseReleased(MouseEvent e) {
            maybePopup(e,false);
        }

        public void maybePopup(MouseEvent e,boolean pressed){
            JList list = (JList) e.getSource();
            int i = list.locationToIndex(e.getPoint());
            if(e.isPopupTrigger()){
                if(i<0)
                    return;
                AddElementPopupMenu.element = (SpatialPlotComponentType)
                                            list.getModel().getElementAt(i);
                AddElementPopupMenu.pBuilder = SpatialPlotBuilder.this;
                AddElementPopupMenu.getPopup().show(e.getComponent(),
                        e.getX(),e.getY());
            }else if(e.getClickCount() == 2 && pressed){
                SpatialPlotComponentType elementAt = (SpatialPlotComponentType)
                        list.getModel().getElementAt(i);
                addElement(new SpatialPlotComponent(elementAt));
            }
        }

    }

    static class AddElementPopupMenu{
        private static JPopupMenu popup;
        private static SpatialPlotComponentType element;
        private static SpatialPlotBuilder pBuilder;
        private static JPopupMenu getPopup(){
            if(popup==null){
                popup = new JPopupMenu();
//                elementPopupMenuItems=new Vector();
                JMenuItem menuItem = new JMenuItem("Add");
                menuItem.addActionListener(new ActionListener(){

                    public void actionPerformed(ActionEvent e) {
                        pBuilder.addElement(new SpatialPlotComponent(element));
                    }

                });
                popup.add(menuItem);
                menuItem = new JMenuItem("Get info");
                menuItem.addActionListener(new ActionListener(){

                    public void actionPerformed(ActionEvent e) {
                        String url = element.getHelpURL();
                        if(url!=null && url.length()>0)
                            HelpButton.showInBrowser(url);
                    }

                });
                popup.add(menuItem);
            }
            return popup;
        }
    }

    public void actionPerformed(ActionEvent arg0) {
        String cmd = arg0.getActionCommand();

        if("Run".equals(cmd)) {
            String call = _model.getCall();
            if(_model.getSize() == 0 || call==null) {
                JOptionPane.showMessageDialog(this, "Plot contains no components.");
                return;
            }
            _lastModel = _model.clone();

            if(_device !=null){
                _device.devOff();
                _plotHolder.remove(_device);
                _device =null;
            }
            Deducer.execute("dev.new()\n" + call);
            this.dispose();
        } else if("Reset".equals(cmd)) {
            this.setModel(new SpatialPlotModel());
        } else if("Cancel".equals(cmd)) {
            this.dispose();
        } else {
            int i = _elementsList.getSelectedIndex();
            if(i == -1) return;
            SpatialPlotComponent spc = _model.getElementAt(i);

            if("remove".equals(cmd)) {
                    _model.remove(i);
            } else if("edit".equals(cmd)){
                AbstractComponentPanel panel = spc.getPanel();
                showComponentDialog(panel);
                if(panel.isOk() && _model.validate())
                        spc.setArgs(panel.getArgs());
            } else if("active".equals(cmd)){
                spc.setActive(!spc.isActive());
                _model.validate();
                _elementsList.validate();
                _elementsList.repaint();
            }
            updatePlot();
        }
    }


    //************************************************
    // WindowListener Methods
    //************************************************

    public void windowActivated(WindowEvent arg0) {}
    public void windowClosed(WindowEvent arg0) {
        if(_device !=null)
            _device.devOff();
    }
    public void windowClosing(WindowEvent arg0) {}
    public void windowDeactivated(WindowEvent arg0) {}
    public void windowDeiconified(WindowEvent arg0) {}
    public void windowIconified(WindowEvent arg0) {}
    public void windowOpened(WindowEvent arg0) {}

    public class MenuListener implements ActionListener{

        public void actionPerformed(ActionEvent ae) {
            String cmd = ae.getActionCommand();
            if("save".equals(cmd)) {
            } else if("new".equals(cmd)) {
                SpatialPlotBuilder spb = new SpatialPlotBuilder(new SpatialPlotModel());
                spb.setVisible(true);
                WindowTracker.addWindow(spb);
            } else if("call".equals(cmd)) {
            } else if("bing".equals(cmd)){
            	_vp.setTileSource("bing");
            }else if("osm".equals(cmd)){
            	_vp.setTileSource("osm");
            }
        }
    }

    private static boolean _fromMain = false;


}


