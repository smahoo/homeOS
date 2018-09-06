package de.smahoo.homeos.kernel.remote;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;











import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventListener;
import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.kernel.remote.result.RemoteResult;
import de.smahoo.homeos.utils.xml.XmlUtils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class RemoteServer {
	
	static private String CONTEXTPATH = "/homeos/remote";

	private CallBackServer callbackServer = null;
	private HttpServer server;
	private RemoteProcessor cmdProcessor = null;
	private int port = -1;
	private List<EventListener> eventListeners = null;
	
	
	public RemoteServer(){
		eventListeners = new ArrayList<EventListener>();
	}
	
	
	public RemoteServer(RemoteProcessor processor){
		this();
		setRemoteProcessor(processor);
	}
	
	public void start() throws IOException{
		if (port < 25) return;
		InetSocketAddress addr = new InetSocketAddress(port);
		
	    server = HttpServer.create(addr,5);
	    
	    server.createContext(CONTEXTPATH, new ClientConnectionHandler());
	    server.setExecutor(Executors.newCachedThreadPool());	    
	    server.start();
	    System.out.println("RemoteServer is listening on port "+port );
		
	}
	
	public void start(int port) throws IOException{
		setPort(port);
		callbackServer = new CallBackServer();
		callbackServer.setPort(port+1);
		callbackServer.start();
		start();
	}
	
	public void stop(){
		if (server == null) return;
		server.stop(0);
	}
	
	public void setPort(int port){
		this.port = port;
	}
	
	public int getPort(){
		return port;
	}
	
	public void setRemoteProcessor(RemoteProcessor processor){
		this.cmdProcessor = processor;
	}

	/*protected Document parseDoc(String data) throws Exception{
		Document result = null;		
		DocumentBuilder docBuilder;
		DocumentBuilderFactory docBFac;
		StringReader inStream;
		InputSource inSource;	
		
		
			inStream = new StringReader(data);
			inSource = new InputSource(inStream);
			docBFac = DocumentBuilderFactory.newInstance();
			docBuilder = docBFac.newDocumentBuilder();			
			result = docBuilder.parse(inSource);
				
		return result;
	}*/
	
	protected String processXml(String str){		
		Document xmlDoc=null;
		try {
			xmlDoc = XmlUtils.parseDoc(str);	
		} catch (Exception exc){
			return XmlUtils.xml2String(generateXmlErrorMessage(exc.getMessage()));
		}
		if (xmlDoc != null) {
			if (cmdProcessor == null) {
			  cmdProcessor = new RemoteProcessor();	
			}
			RemoteResult cmdResult;
			try {
				cmdResult = cmdProcessor.process(xmlDoc);
				return XmlUtils.xml2String(cmdResult.toXmlDocument());
			} catch (Exception exc){
				return XmlUtils.xml2String(generateXmlErrorMessage("Unexpected Error - processing XML, "+exc.getMessage()));
			}			
		}
		return XmlUtils.xml2String(generateXmlErrorMessage("Unexpected Error - processing XML"));
	}
	
	protected Document generateXmlErrorMessage(String message){
		Document doc = XmlUtils.createDocument();
		Element root = doc.createElement("error");
		root.setTextContent(message);
		doc.appendChild(root);
		return doc;
	}

	
	public static void main(String[] args) throws IOException {	    
	    RemoteServer cs = new RemoteServer();
	    cs.start(8080);	 
	}

	private class ClientConnectionHandler implements HttpHandler {
		
		private String loadHtmlPage(String filename) throws IOException{
			
			File file;
			String sep = System.getProperty("file.separator");
			if (filename == null){
				file = new File(System.getProperty("user.dir")+sep+"html"+sep+"start.html");
			} else {
				file = new File(System.getProperty("user.dir") + sep + "html" + sep + filename);
				// System.out.println("DEBUG: Looking for file " + file.getPath());
			}
			FileReader fr = new FileReader(file);
			
			BufferedReader br = new BufferedReader(fr);
			StringBuffer buffer = new StringBuffer();
			String s;
			while((s = br.readLine()) != null) {
				buffer.append(s);
			} 
			
			br.close();
			return buffer.toString();
		}
		
		private byte[] loadBinaryFile(String filename) throws IOException{
			
			File file;
			String sep = System.getProperty("file.separator");
			if (filename == null){
				file = new File(System.getProperty("user.dir")+sep+"html"+sep+"start.html");
			} else {
				file = new File(System.getProperty("user.dir") + sep + "html" + sep + filename);
				//System.out.println("DEBUG: Looking for file " + file.getPath());
			}
			
			FileInputStream br = new FileInputStream(file);
			byte data[] = new byte[(int)file.length()];
			int bytesread = br.read(data, 0, (int)file.length());
			//System.out.println("DEBUG: Read " + bytesread + " from binary file.");
			br.close();
			return data;
		}
		
		private void evaluateGetRequest(HttpExchange exchange) throws IOException {
			URI uri = exchange.getRequestURI();
			System.out.println("DEBUG: Request for URI " + uri.toString());
			String filename = (uri.getPath()).split(CONTEXTPATH)[1];
			if (filename.startsWith(System.getProperty("file.separator"))) {
				filename = filename.substring(1);
			}
			System.out.println("DEBUG: returning file with name " + filename);
			boolean isbinary = false;
			byte[] filecontent = new byte[0];
	    	String msg = loadHtmlPage(filename);
	    	Headers responseHeaders = exchange.getResponseHeaders();
	    	if (filename.endsWith(".css")){
	    		responseHeaders.add("Content-Type","text/css");
	    	} else if (filename.endsWith(".js")){
	    		responseHeaders.add("Content-Type", "text/javascript");
	    	} else if (filename.endsWith(".woff")){
	    		responseHeaders.add("Content-Type","application/font-woff");
	    	} else if (filename.endsWith(".eot")){
	    		responseHeaders.add("Content-Type","application/vnd.ms-fontobject");
	    	} else if (filename.endsWith(".ttf")){
	    		responseHeaders.add("Content-Type","application/octet-stream");
	    		filecontent = this.loadBinaryFile(filename);
	    		isbinary = true;
	    	} else if (filename.endsWith(".png")){
	    		responseHeaders.add("Content-Type","image/png");
	    		filecontent = this.loadBinaryFile(filename);
	    		isbinary = true;
			} else {
	    		responseHeaders.add("Content-Type","text/html");
	    	}
		    responseHeaders.set("keep-alive","true");
		    
		    OutputStream responseBody = exchange.getResponseBody();
		    if (isbinary) {
			    exchange.sendResponseHeaders(200, filecontent.length);
		    	responseBody.write(filecontent);
		    } else {
			    exchange.sendResponseHeaders(200, msg.length());
		    	responseBody.write(msg.getBytes());
		    }
		    responseBody.flush();
		    responseBody.close();
		}
		
		
		public synchronized void handle(HttpExchange exchange) throws IOException {
		    String requestMethod = exchange.getRequestMethod();
		  
		    if (requestMethod.equalsIgnoreCase("POST")) {
		        int contentLength = -1;
		        
		        try {
	                List<String> values = exchange.getRequestHeaders().get("Content-length");
	            
	                if (values.size() == 1){
	                	contentLength = Integer.parseInt(values.get(0));	                	
	                }
	                
		        } catch (Exception exc){
		        	throw new IOException(exc);
		        }
		        if (contentLength < 0){
		        	throw new IOException("No content-length was given! Unable to read the request.");
		        }
		    	
		    	InputStreamReader in = new InputStreamReader(exchange.getRequestBody(), "UTF-8");
				  StringBuffer strBuff = new StringBuffer();
				  char[] buffer = new char[1024];
				  int bytes = 0;
				  int allReadBytes = 0;
				  boolean doReading = true;
					while (doReading){
						bytes = in.read(buffer);
						if (bytes > 0){							
						  strBuff.append(buffer,0, bytes);
						}
						allReadBytes=allReadBytes+bytes;
						doReading = allReadBytes < contentLength;						 				
					}
				
				
				dispatchEvent(new RemoteEvent(EventType.CONNECTION_ESTABLISHED,""+exchange.getRemoteAddress()));
  				String response = processXml(strBuff.toString());  				
  				Headers responseHeaders = exchange.getResponseHeaders(); 	
			    			    
  				responseHeaders.set("keep-alive","true");
			    responseHeaders.add("Content-Type","application/xml; charset=utf-8");	
			    
			    
			    exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
			    
			    OutputStream responseBody = exchange.getResponseBody();
			    
			 
			    
			    
			    try {

			    	responseBody.write(response.getBytes("UTF-8"));
			     	responseBody.flush();			    
			    	responseBody.close();
			    } catch (Exception exc){
			    	exc.printStackTrace();
			    	//FIXME Handle Exception here
			    	throw new IOException(exc);
			    }
			  
		    } else {
		    	evaluateGetRequest(exchange);		    	
		    }
		    exchange.close();
		  }		  
		}
	
	protected void dispatchEvent(Event event){
		for (EventListener l : eventListeners){
			l.onEvent(event);
		}
	}
	
	public void addEventListener(EventListener listener){
		if (!eventListeners.contains(listener)){
			eventListeners.add(listener);
		}
	}
}
