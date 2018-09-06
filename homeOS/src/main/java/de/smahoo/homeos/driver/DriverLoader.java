package de.smahoo.homeos.driver;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.utils.JarResources;
import de.smahoo.homeos.utils.MultiClassLoader;

public class DriverLoader extends MultiClassLoader {
	private List<JarResources> jarResourcesList;
//    private JarResources    jarResources;
    
    public DriverLoader(){
    	jarResourcesList = new ArrayList<JarResources>();
    }
    
    public Driver loadDriver(Class<?> c){
    	Driver d = null;
    	
    	try {
    		Object o = c.newInstance();
    		if (o instanceof Driver){
    			d= (Driver)o;
    			d.deviceManager = HomeOs.getInstance().getDeviceManager();
    		}
    	} catch (Exception exc){
    		exc.printStackTrace();
    	}
    	
    	return d;
    }
    
    public Driver loadDriver(String filename, String classname) throws IOException{
    	//Create the JarResource and suck in the jar file.
    	File f = new File(filename);
    	if (!f.exists()){
    		throw new IOException("File \'"+filename+"\' doesn't exists");
    	}
    	JarResources jarRes = new JarResources(filename); 
    	addJarRescources(jarRes);
    	
    	Class<?> c;    	
    	try {
    		c = loadClass(classname, true);
    		Object o = c.newInstance();
    		if (o instanceof Driver){
    			Driver driver = (Driver)o;
    			driver.filename = filename;
    			driver.jarResource = jarRes;
    			driver.deviceManager = HomeOs.getInstance().getDeviceManager();
    			return driver;
    		}
    	} catch (Exception exc){
    		throw new IOException(exc);
    	}    	
    	return null;
    }    
    
    
    protected void addJarRescources(JarResources resources){
    	this.jarResourcesList.add(resources);
    }
    
    protected byte[] loadClassBytes (String className)  {
    	// Support the MultiClassLoader's class name munging facility.
    	className = formatClassName(className);
    	// Attempt to get the class data from the JarResource.
    	byte[] result;
    	for (JarResources res : this.jarResourcesList){
    		result = res.getResource(className);
    		if (result != null){
    			if (result.length >0){
    				return result;
    			}
    		}
    	}
    	return null;
    }  
}
