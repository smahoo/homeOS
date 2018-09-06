package de.smahoo.homeos.testing;





import de.smahoo.homeos.common.EventListener;
import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.testing.gui.FrameTestHomeOs;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;


public class TestHomeOs implements Runnable {

	public static final String DEFAULT_CONFIG_FILENAME = "config-sample.xml";

	HomeOs homeOs = null;
	FrameTestHomeOs mainFrame;
	String configFileName;
	
	 public static void main(String[] args) {
	 		String configFile = DEFAULT_CONFIG_FILENAME;
	 		if (args.length > 1) {
	 			configFile = args[1];
			}
	        Runnable app = new TestHomeOs(configFile);
	        try {
	            SwingUtilities.invokeAndWait(app);
	        } catch (InvocationTargetException ex) {
	            ex.printStackTrace();
	        } catch (InterruptedException ex) {
	            ex.printStackTrace();
	        }
	 }

		public TestHomeOs(String configFileName){
			this.configFileName = configFileName;
		}


		public void run() {
			homeOs = HomeOs.getInstance();

			EventListener listener;
			String fileName = System.getProperty("user.dir")+System.getProperty("file.separator")+"config"+System.getProperty("file.separator")+configFileName;
			homeOs.start(fileName);
			
			mainFrame = new FrameTestHomeOs();
			mainFrame.addWindowListener(new WindowAdapter() {
		         public void windowClosing(WindowEvent e){
		           mainFrame.dispose();
		      	   homeOs.shutdown();
		         }
		      });
			mainFrame.init();
			mainFrame.setVisible(true);

		}

}
