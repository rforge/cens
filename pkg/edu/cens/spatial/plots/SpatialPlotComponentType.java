package edu.cens.spatial.plots;

import edu.cens.spatial.plots.components.*;
import org.rosuda.deducer.Deducer;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.util.Map;

/**
* Created by IntelliJ IDEA.
* User: Neal
* Date: 1/13/11
* Time: 8:11 AM
*/
public enum SpatialPlotComponentType  implements IPlotComponentType {

//    hello() {
//        @Override
//        public AbstractComponentPanel getPanel(Map<String, String> args) {
//            return new AbstractComponentPanel(args) {
//                @Override
//                public boolean isOk() {
//                    return true;
//                }
//            };
//        }
//    },


    points("Points", "icons/geo_point.png") {
        @Override
        public PointComponentPanel getPanel(Map<String, String> args) {
            return new PointComponentPanel(args);
        }

        @Override
        public String getCall(Map<String, String> args) {
            String lat = args.remove("lat");
            String lon = args.remove("lon");

            String dat = args.remove("dat");
            if(dat != null){
                lat = fix(dat, lat);
                lon = fix(dat, lon);
            }

            return String.format("plot_points(%1s,%2s %3s)", lon, lat, argsToString(args));
        }
    },

    paths("Paths", "icons/geo_path.png") {
        @Override
        public PathComponentPanel getPanel(Map<String, String> args) {
            return new PathComponentPanel(args);
        }

        @Override
        public String getCall(Map<String, String> args) {

            String lat = args.remove("lat");
            String lon = args.remove("lon");
            String subj = args.remove("subj");
            String time = args.remove("time");

            String dat = args.remove("dat");
            if(dat != null){
                lat = fix(dat, lat);
                lon = fix(dat, lon);
                subj = fix(dat, subj);
                time = fix(dat, time);
            }

            return String.format("plot_paths_on_map(%1s,%2s,%3s,%4s %5s);\n", lat, lon, subj, time, argsToString(args));

        }
    },

//    dist("Distribution", "icons/geo_distribution.png") {
//        @Override
//        public String getCall(Map<String, String> args) {
//            if("not-installed".equals(Deducer.requirePackage("ks"))) return "print('Can\\'t load ks');";
//
//            String lat = args.remove("lat");
//            String lon = args.remove("lon");
//
//            String dat = args.remove("dat");
//            if(dat != null){
//                lat = fix(dat, lat);
//                lon = fix(dat, lon);
//            }
//            return String.format("plot_distribution_on_map(%1s, %2s %3s);", lat, lon, argsToString(args));
//        }
//
//        @Override
//        public DistributionComponentPanel getPanel(Map<String, String> args) {
//            return new DistributionComponentPanel(args);
//        }
//    },
//
//    surface("Surface", "icons/geo_smooth.png") {
//        @Override
//        public String getCall(Map<String, String> args) {
//
//            String lat = args.remove("lat");
//            String lon = args.remove("lon");
//            String z = args.remove("z");
//            String smooth = args.remove("smooth");
//
//            String dat = args.remove("dat");
//            if(dat != null){
//                lat = fix(dat, lat);
//                lon = fix(dat, lon);
//                z = fix(dat, z);
//            }
//
//            return String.format("plot_smooth_on_map(%1s, %2s, %3s, %4s %5s);", lat, lon, z, smooth, argsToString(args));
//        }
//
//        @Override
//        public AbstractComponentPanel getPanel(Map<String, String> args) {
//            return new SmoothComponentPanel(args);
//        }
//    },

    bubbles("Bubble", "icons/geo_bubble.png") {
        @Override
        public String getCall(Map<String, String> args) {

            String lat = args.remove("lat");
            String lon = args.remove("lon");
            String z = args.remove("z");

            String dat = args.remove("dat");
            if(dat != null){
                lat = fix(dat, lat);
                lon = fix(dat, lon);
                z = fix(dat, z);
            }

            return String.format("plot_bubble_on_map(%1s,%2s,%3s %4s) ;",lat, lon, z, argsToString(args));
        }

        @Override
        public BubbleComponentPanel getPanel(Map<String, String> args) {
            return new BubbleComponentPanel(args);
        }

    },

//    hmc("Heatmap - Count", "icons/geo_distribution.png") {
//
//        public String getCall(Map<String, String> args) {
//            String lat = args.remove("lat");
//            String lon = args.remove("lon");
//
//            String dat = args.remove("dat");
//            if(dat != null){
//                lat = fix(dat, lat);
//                lon = fix(dat, lon);
//            }
//            return String.format("plot_heatmap_count(%1s, %2s %3s);", lat, lon, argsToString(args));
//        }
//
//
//        @Override
//        public AbstractComponentPanel getPanel(Map<String, String> args) {
//            return new HeatCountComponentPanel(args);
//        }
//    },
//
//    hmm("Heatmap - Mean", "icons/geo_distribution.png") {
//
//        public String getCall(Map<String, String> args) {
//            String lat = args.remove("lat");
//            String lon = args.remove("lon");
//            String z = args.remove("z");
//
//            String dat = args.remove("dat");
//            if(dat != null){
//                lat = fix(dat, lat);
//                lon = fix(dat, lon);
//                z   = fix(dat, z);
//            }
//            return String.format("plot_heatmap_mean(%1s, %2s, %3s %4s);", lat, lon, z, argsToString(args));
//        }
//
//
//        @Override
//        public AbstractComponentPanel getPanel(Map<String, String> args) {
//            return new HeatMeanComponentPanel(args);
//        }
//    },


    shape("Shapes", "icons/geo_choropleth.png"){

        public String getCall(Map<String, String> args) {
            String shape = args.remove("shapefile");
            return String.format("plot_shapes(%1s %2s);",shape, argsToString(args));
        }


        @Override
        public AbstractComponentPanel getPanel(Map<String, String> args) {
            return new ShapeComponentPanel(args);
        }
    },

    ;

    private static String fix(String dat, String lat) {
        return dat + "$" + lat;
    }

    private static final String _spatialCat = "Spatial";

    private final String _category;

    private final String _label;
    private final String _iconPath;
    private final String _helpURL = ""; //todo

    SpatialPlotComponentType() {
        _label = name();
        _iconPath = "icons/geo_default.png";
        _category = _spatialCat;
    }

    SpatialPlotComponentType(String label, String iconPath) {
        this(label, iconPath, _spatialCat);
    }

    SpatialPlotComponentType(String label, String iconPath, String category) {
        _label = label;
        _iconPath = iconPath;
        _category = category;
    }

    public final ImageIcon getIcon() {
    	System.out.println(_iconPath);
        //return new ImageIcon(_iconPath);
    	return new ImageIcon(getClass().getResource(_iconPath));
    }


    private static final ImageIcon _inactiveLabel =
            new ImageIcon(SpatialPlotComponentType.class.getResource("/icons/edit_remove_32.png"));

    public final JPanel renderForList(boolean enabled) {

        JLabel iconLabel = new JLabel(enabled? getIcon(): _inactiveLabel);
        iconLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel label = new JLabel(this._label.replace('_', '\n'));
        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        label.setFont(new Font("Dialog", Font.PLAIN, 8) );

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(iconLabel);
        panel.add(label);
        panel.setBorder(new EtchedBorder());

        return panel;

    }

    public abstract AbstractComponentPanel getPanel(Map<String, String> args);


    public String getCall(Map<String, String> args) {
        return "";
    }

    public final String getHelpURL() {
        return _helpURL;
    }

    private static String argsToString(Map<String, String> args) {
        StringBuilder sb = new StringBuilder();

        // Copy over, remove anything obviously broken

        for(Map.Entry<String, String> e : args.entrySet())
            sb.append(',').append(e.getKey()).append('=').append(e.getValue());

        // remove last ','
        return sb.toString();
    }

    public String getCategory() {
        return _category;
    }

    //**************************
    // Transferable methods
    //**************************

    private static final DataFlavor[] _flavor
            = new DataFlavor[]{new DataFlavor(SpatialPlotComponentType.class, "Spatial Plot Component Type")};

    public static final DataFlavor getFlavor() { return _flavor[0]; }

    public final DataFlavor[] getTransferDataFlavors() {
        return _flavor;
    }

    public final boolean isDataFlavorSupported(DataFlavor flavor) {
        return _flavor[0].equals(flavor);
    }

    public final SpatialPlotComponentType getTransferData(DataFlavor flavor)  {
        return this;
    }
}
