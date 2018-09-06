package de.smahoo.homeos.service;

import de.smahoo.homeos.utils.JarResources;
import de.smahoo.homeos.utils.MultiClassLoader;

public class ServiceLoader extends MultiClassLoader{
private JarResources    jarResources;
    
    public ServiceLoader(){
    	
    }
    
    public Service loadService(String fileName, String className){
    	//Create the JarResource and suck in the jar file.
    	jarResources = new JarResources(fileName);
    	Class<?> c;    	
    	try {
    		c = loadClass(className, true);
    		Object o = c.newInstance();
    		if (o instanceof Service){
    			return (Service)o;
    		}
    	} catch (Exception exc){
    		exc.printStackTrace();
    	}    	
    	return null;
    }
    
    
    
    
    protected byte[] loadClassBytes (String className)  {
    	// Support the MultiClassLoader's class name munging facility.
    	className = formatClassName(className);
    	// Attempt to get the class data from the JarResource.
    	return (jarResources.getResource(className));
    }  
}
/*

public class JarClassLoader extends MultiClassLoader {
    private JarResources    jarResources;
    
    public JarClassLoader (String jarName)  {
    	//Create the JarResource and suck in the jar file.
    	jarResources = new JarResources (jarName);
    }
    
    
    protected byte[] loadClassBytes (String className)
    {
    	// Support the MultiClassLoader's class name munging facility.
    	className = formatClassName (className);
    	// Attempt to get the class data from the JarResource.
    	return (jarResources.getResource (className));
    }  
	
}

*/