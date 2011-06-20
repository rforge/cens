package org.rosuda.javaGD;

import org.rosuda.REngine.REXP;
import org.rosuda.deducer.Deducer;
import org.rosuda.javaGD.GDObject;
import org.rosuda.javaGD.JGDBufferedPanel;

import java.awt.*;

/**
* Created by IntelliJ IDEA.
* User: Neal
* Date: 1/10/11
* Time: 8:55 PM
* To change this template use File | Settings | File Templates.
*/
public class PlotPanel extends JGDBufferedPanel {
    Dimension _lastSize;

    public PlotPanel(int w, int h) {
        super(w, h);
        _lastSize = getSize();
    }

    public void devOff(){
        Deducer.eval("dev.off(" + (this.devNr + 1) + ")");
    }
    
    public void initRefresh() {
        Deducer.idleEval("try(.C(\"javaGDresize\",as.integer("+devNr+")),silent=TRUE)");
    }

    public synchronized void paintComponent(Graphics g) {
        Dimension d=getSize();
        if (!d.equals(_lastSize)) {
            REXP exp = Deducer.idleEval("try(.C(\"javaGDresize\",as.integer("+devNr+")),silent=TRUE)");
            if(exp!=null)
                _lastSize =d;
            return;
        }
        //super.paintComponent(g);
        d=getSize();
        if (!d.equals(lastSize)) {
            initRefresh();
            lastSize=d;
            return;
        }

        if (forceAntiAliasing) {
            Graphics2D g2=(Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        int i=0, j=l.size();
        g.setFont(gs.f);
        g.setClip(0,0,d.width,d.height); // reset clipping rect
        g.setColor(new Color(255,0,0,0));
        //g.fillRect(0,0,d.width,d.height);
        while (i<j) {
            GDObject o=(GDObject) l.elementAt(i++);
            o.paint(this, gs, g);
            //System.out.println(o.toString());
        }
    }

}
