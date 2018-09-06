package de.smahoo.homeos.service;

public class ServiceThread extends Thread{
	
	protected Service service = null;
	protected boolean terminate = false;
	
	public ServiceThread(Service service){
		this.service = service;
	}
	
	
	
	
	synchronized protected boolean keepAlive(){
		return !terminate;
	}
		
	public void run(){
		service.onStart();
		while (keepAlive()) {
			try {
				Thread.sleep(100);
			} catch (Exception exc){
				exc.printStackTrace();
			}
		}
	}

}
