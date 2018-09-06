package de.smahoo.homeos.remote.connection;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteConnection {

	URL url = null;
	
	public void setURL(URL url){
		this.url = url;
	}
	
	public synchronized String sendCommand(String cmd) throws IOException{	
		return sendCommand(cmd,20000);
	}
	
	public synchronized String sendCommand(String cmd, int timeout) throws IOException{	
		
		if (cmd == null) return null;
				
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();	
			connection.setConnectTimeout(timeout);
			connection.setRequestMethod( "POST" );			
			connection.setDoInput( true );
			connection.setDoOutput( true );		
			connection.setRequestProperty( "Content-Type","text/xml" );
			connection.setRequestProperty( "Content-Length", String.valueOf(cmd.length()) );
						
			OutputStreamWriter writer = new OutputStreamWriter( connection.getOutputStream() );
			writer.write(cmd);
			writer.flush();			
			
			InputStreamReader in = new InputStreamReader(connection.getInputStream());
			StringBuffer buffer = new StringBuffer();
			int read = 0;
			
			while ((read = in.read()) !=-1 ){
				buffer.append((char)read);
			}		
			
			String str =buffer.toString();
			writer.close();
			in.close();
			//connection.disconnect();		
			return str;
			
	
		
	}
	
}
