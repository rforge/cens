package edu.cens.spatial.plots;

/**
 * Created by IntelliJ IDEA.
 * User: Neal
 * Date: 1/17/11
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */

import org.rosuda.javaGD.GDContainer;
import org.rosuda.javaGD.GDInterface;



public class DeviceInterface extends GDInterface {
	private static GDContainer cont;
	int devNr = -1;

    /** requests a new device of the specified size
     *  @param w width of the device
     *  @param h height of the device */
    public void     gdOpen(double w, double h) {
        open=true;
        c=cont;
    }

    /** create a new, blank page
     *  @param devNr device number assigned to this device by R */
    public void     gdNewPage(int devNr) { // new API: provides the device Nr.
        this.devNr=devNr;
        if (c!=null) {
            c.reset();
            c.setDeviceNumber(devNr);
        }
        //System.out.println("new page called : " + devNr + " = dev number");
    }

	public static void register(GDContainer pl){
		try{
			cont = pl;
		}catch(Exception e){e.printStackTrace();}

	}

}
