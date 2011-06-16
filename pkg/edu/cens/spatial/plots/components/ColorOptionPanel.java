package edu.cens.spatial.plots.components;

import edu.cens.spatial.plots.widgets.PaletteChooser;
import org.rosuda.deducer.widgets.SingleVariableWidget;
import org.rosuda.deducer.widgets.VariableSelectorWidget;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Neal
 * Date: 3/25/11
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ColorOptionPanel extends AbstractComponentPanel{

    private final VariableSelectorWidget _vsw = new VariableSelectorWidget();
    private final SingleVariableWidget _value = new SingleVariableWidget("Color by Variable:",_vsw);

    private final PaletteChooser _bpl= new PaletteChooser();


    private final JSlider _transparency = new JSlider(0,255,255){{
        setBorder(BorderFactory.createTitledBorder("Transparency:"));
    }};


    @Override
    public void initDefault() {

    }

    @Override
    public void initFromArgs() {
        _vsw.setSelectedData(_args.get(k.dat.name()));
    }



    public static enum k { dat, palette, colValue }

    public ColorOptionPanel(Map<String, String> args) {
        super(args);
        initFields();

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);

        _vsw.setBorder(BorderFactory.createLoweredBevelBorder());
        c.anchor = GridBagConstraints.WEST;
        c.gridheight=4;
        add(_vsw,c);

        c.gridheight = 1; c.gridx = 1;
        c.gridy = 0; c.gridwidth=1;
        c.weightx = c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        add(_value,c);

        c.gridy++;
        add(_bpl.getBPC(),c);

        c.gridy++;
        add(new JLabel(""),c);

        c.gridy++;
        add(_transparency,c);

        c.gridy++;
        addOkPanel(c, 2);



    }


    @Override
    boolean doOK() {

        String pallet = _bpl.getPalette();
        String value = _value.getSelectedVariable();
        String dat = _vsw.getSelectedData();
        int alpha = _transparency.getValue();

        //why am i doing this here :(
        // ick

        if(value == null) {
            value = "1";
        }else {
            value = dat + "$" + value;
        }


        putArg(k.colValue, value);
        putArg(k.palette, String.format("addTsp(bPal(%s,12,'%s'),%s)", value, pallet, alpha));

        return true;
    }
}
