package de.smahoo.homeos.testing.remote;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import de.smahoo.homeos.testing.gui.remote.PanelRemoteDetails;

public class TestRemoteApi implements Runnable{

	public void run(){
		PanelRemoteDetails panel = new PanelRemoteDetails();
		JFrame frameMain = new JFrame();
		frameMain.setContentPane(panel);
		frameMain.setSize(1200, 480);
		frameMain.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);
			}
			
			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		frameMain.setVisible(true);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Runnable app = new TestRemoteApi();	        
	    try {
	        SwingUtilities.invokeAndWait(app);
	    } catch (InvocationTargetException ex) {
	       ex.printStackTrace();
	    } catch (InterruptedException ex) {
	       ex.printStackTrace();
	    }
	}

}
