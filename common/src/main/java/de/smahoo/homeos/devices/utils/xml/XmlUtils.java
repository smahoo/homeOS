package de.smahoo.homeos.utils.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;



//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
//import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;



public class XmlUtils {
	
	
	static public Document loadXml(File file) throws IOException{		
      
		
        	BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            
            br.close();
            String everything = sb.toString();
            
            return XmlUtils.parseDoc(everything);
	}
	
	static public void saveXml(File file, Document doc) throws IOException{
		File path = new File(file.getCanonicalPath());
		
		if (!path.exists()){
			path.mkdirs();
		}
		
		String txt = xml2String(doc);
		if (file.exists()){
			file.delete();
		}
		
		 

		
		if (file.createNewFile()){
			OutputStreamWriter char_output = new OutputStreamWriter(
				     new FileOutputStream(file),
				     Charset.forName("UTF-8").newEncoder() 
				 );
			
			char_output.write(txt);
			char_output.flush();
			char_output.close();
			
		} else {
			throw new IOException("Unable to create file '"+file.getAbsolutePath()+"'");
		}
		
	}
	
	
	// is not working for java 1.6 :(	 
//	static public String formatXml(Document doc){
//	
//			String response = null;
//	
//			ByteArrayOutputStream bao = new ByteArrayOutputStream();
//			XMLSerializer serializer = new XMLSerializer(bao, new OutputFormat(doc,"UTF-8", true));			
//			try {
//				serializer.serialize(doc);
//				response = bao.toString();
//			} catch (Exception exc){
//				response = exc.getMessage();
//			}
//			
//			
//			return response;
//		
//	}
	
	static public String xml2String(Document doc){
		 try
		    {
		       DOMSource domSource = new DOMSource(doc);
		       StringWriter writer = new StringWriter();
		       StreamResult result = new StreamResult(writer);
		       TransformerFactory tf = TransformerFactory.newInstance();
		       Transformer transformer = tf.newTransformer();
		       transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		       transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT,"4");
		       transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		       transformer.transform(domSource, result);
		       return writer.toString();
		    }
		    catch(TransformerException ex)
		    {
		       ex.printStackTrace();
		       return null;
		    }
		 	catch (Exception exc){
		 		exc.printStackTrace();
		 		return null;
		 	}
	}
	
	static public Document parseDoc(String data){
		Document result = null;		
		DocumentBuilder docBuilder;
		DocumentBuilderFactory docBFac;
		StringReader inStream;
		InputSource inSource;	
		
		try {
			inStream = new StringReader(data);
			inSource = new InputSource(inStream);
			docBFac = DocumentBuilderFactory.newInstance();
			docBuilder = docBFac.newDocumentBuilder();			
			result = docBuilder.parse(inSource);
		} catch (Exception exc){
			exc.printStackTrace();
			result = null;
		}		
		return result;
	}
	
	static public Document createDocument(){
		Document result = null;
		DocumentBuilder docBuilder;
		DocumentBuilderFactory docBFac;
		try {
			docBFac = DocumentBuilderFactory.newInstance();
			docBuilder = docBFac.newDocumentBuilder();
			result = docBuilder.newDocument();
		} catch (Exception exc){
			exc.printStackTrace();
		}
		result.setXmlVersion("1.0");
		return result;
	}
}
