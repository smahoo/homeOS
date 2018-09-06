package de.smahoo.homeos.testing.remote;

import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

import javax.swing.SwingUtilities;


public class DummyCallbackPrinter implements Runnable{
	
	public void run(){
		try {
			Socket socket;
			socket = new Socket("127.0.0.1",2021);
			int read;
			int lastRead = 0;
			StringBuffer buffer = new StringBuffer();
			while(true){				
				read = socket.getInputStream().read();
				buffer.append((char)read);				
				if ((((char)read)=='%')&&(read == lastRead)){						
					System.out.println(buffer.toString());
					buffer = new StringBuffer();
				}
				lastRead = read;
			}
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	static public void main(String[] args){
		Runnable app = new DummyCallbackPrinter();        
        try {
            SwingUtilities.invokeAndWait(app);
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
	}
}
