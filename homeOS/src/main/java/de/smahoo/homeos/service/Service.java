package de.smahoo.homeos.service;

import de.smahoo.homeos.device.DeviceManager;
import de.smahoo.homeos.kernel.EventBus;
import de.smahoo.homeos.kernel.HomeOs;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public abstract class Service {
	
	private List<ServiceListener> listeners;	
	private ServiceState currentState = ServiceState.NOT_INITIALIZED;
	private boolean enabled;
	
	public Service(){	
		listeners = new ArrayList<ServiceListener>();
		enabled = false;
	}
		
	
	public final void addServiceListener(final ServiceListener listener){
		if (listeners.contains(listener)) return;
		listeners.add(listener);
	}
	
	public final void removeServiceListener(final ServiceListener listener){
		if (listeners.isEmpty()) return;
		if (listeners.contains(listener)){
			listeners.remove(listener);
		}
	}

	protected EventBus getEventBus(){
		return HomeOs.getInstance().getEventBus();
	}

	protected DeviceManager getDeviceManager(){
		return HomeOs.getInstance().getDeviceManager();
	}

	final void dispatchServiceEvent(final ServiceEvent event){
		if (listeners.isEmpty()) return;
		for (ServiceListener listener : listeners){
			listener.onServiceEvent(event);
		}
	}
	
	final void startIt(){
		setState(ServiceState.STARTING);
		if (onStart()){
			setState(ServiceState.STARTED);
		} else {
			setErrorState("Unable to start service \'"+getName()+"\'");
		}
	}
	
	final void stopIt(){
		setState(ServiceState.STOPPING);
		if (onStop()){
			setState(ServiceState.STOPPED);
		} else {
			setErrorState("Unable to stop service \'"+getName()+"\'");
		}
	}
	
	final void pauseIt(){
		setState(ServiceState.PAUSING);
		if (onPause()){
			setState(ServiceState.PAUSED);
		} else {
			setErrorState("Unable to pause service \'"+getName()+"\'");
		}
	}
	
	final protected void setErrorState(final String msg){
		this.currentState = ServiceState.ERROR;
		dispatchServiceEvent(new ServiceEvent(ServiceState.ERROR,this,msg));
	}
	
	final protected void setState(final ServiceState state){
		this.currentState = state;
		dispatchServiceEvent(new ServiceEvent(state,this));		
	}
	
	final public ServiceState getState(){		
		return this.currentState;
	}


	abstract public String getVersion();
	abstract public String getName();
	abstract public String getDescription();
	abstract public boolean init(Element elem);
	abstract public Element toXmlElement(Document doc);
	abstract protected boolean onPause();
	abstract protected boolean onStart();
	abstract protected boolean onStop();
}
