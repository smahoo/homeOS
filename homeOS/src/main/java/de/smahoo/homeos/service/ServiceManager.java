package de.smahoo.homeos.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServiceManager implements ServiceListener{

	
	protected HashMap<String, Service> serviceList;
	protected List<ServiceListener> listeners;
	
	
	public ServiceManager(){
		serviceList = new HashMap<String, Service>();
		listeners = new ArrayList<ServiceListener>();
	}
	
	public Service loadService(final String filename, final String classname){
		ServiceLoader srvLoader = new ServiceLoader();
		return srvLoader.loadService(filename, classname);
		
	}
	
	public void addService(final String filename, final String classname){
		Service service = loadService(filename, classname);
		if (service != null)  {
			addService(service);
		}
	}
	
	public void addService(final Service service){
		if (serviceList.containsKey(service.getClass().getName())){
			return;
		} 
		service.addServiceListener(this);
		serviceList.put(service.getClass().getName(), service);
	}
	
	protected Service getService(final String classname){
		Service service = null;
		
		if (serviceList.containsKey(classname)){
			service = serviceList.get(classname);
		}
		
		
		return service;
	}

	public List<Service> getServices(){
		List<Service> result = new ArrayList<>();
		result.addAll(serviceList.values());
		return result;
	}

	public void addServiceEventListener(final ServiceListener listener){
		if (listeners.contains(listener)) return;
		listeners.add(listener);
	}
	
	public void removeServiceListener(final ServiceListener listener){
		if (listeners.isEmpty()) return;
		if (listeners.contains(listener)){
			listeners.remove(listener);
		}
	}
	
	protected void dispatchServiceEvent(final ServiceEvent event){
		if (listeners.isEmpty()) return;
		for (ServiceListener listener : listeners){
			listener.onServiceEvent(event);
		}
	}
	
	public void onServiceEvent(final ServiceEvent event){
		System.out.println(event.toString());
		dispatchServiceEvent(event);
	}
	

	public void stopAllServices(){
		for (Service service : serviceList.values()){
				stopService(service);
		}
	}
	
	
	public void startAllServices(){
		for (Service service : serviceList.values()){
			startService(service);
		}
	}
	
	public void startService(final Service service){
		service.startIt();
	}
	
	public void stopService(final Service service){
		service.stopIt();
	}
	
	public void pauseService(final Service service){
		service.pauseIt();
	}
}
