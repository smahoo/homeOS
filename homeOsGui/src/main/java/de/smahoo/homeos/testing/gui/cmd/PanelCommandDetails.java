package de.smahoo.homeos.testing.gui.cmd;

import javax.swing.JPanel;

import de.smahoo.homeos.utils.xml.XmlUtils;
import org.w3c.dom.Document;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;

import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JTextArea;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class PanelCommandDetails extends JPanel{	
	
	public PanelCommandDetails() {		
		initGui();
		setCmdFileList();
	}
			
	private void loadCmd(){
		textAreaResult.setText("");
		textAreaCmd.setText("");
		String cmdFile;
		cmdFile = (String)listCmd.getSelectedValue();
		
		String xmlText = readFile(System.getProperty("user.dir")+System.getProperty("file.separator")+"testdata"+System.getProperty("file.separator")+"cmd"+System.getProperty("file.separator")+cmdFile);
		xmlText = formatXML(xmlText);
		textAreaCmd.setText(xmlText);
	}
	
	private String getAddress(){
		if (rbtnUseLocal.isSelected()){
			return "127.0.0.1";
		} else {
			return txtAddress.getText();
		}
	}
	
	private String sendCommand(String cmd){		
		textAreaResult.setText("");		
		if (cmd == null) return null;
		try {
			URL url = new URL( "http://"+getAddress()+":2020/homeos/remote" );
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod( "POST" );
			connection.setDoInput( true );
			connection.setDoOutput( true );
			connection.setUseCaches( false );
		//	connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8\n\n");
			connection.setRequestProperty( "Content-Type","application/x-www-form-urlencoded" );
			connection.setRequestProperty( "Content-Length", String.valueOf(cmd.length()) );

			OutputStreamWriter writer = new OutputStreamWriter( connection.getOutputStream() );
			writer.write(cmd);
			writer.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));		
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while((line = reader.readLine()) != null){
				//this.textAreaResult.append(line);
				buffer.append(line);
			}
			writer.close();
			reader.close();
			System.out.println("das ist angekommen : "+buffer.toString());
			return buffer.toString();
			
		} catch (Exception exc){
			exc.printStackTrace();
			return exc.getMessage();
			
		}
		
	}
	
	private void setCmdFileList(){
		DefaultListModel listmodel = new DefaultListModel();
		String fileName = System.getProperty("user.dir")+System.getProperty("file.separator")+"testdata"+System.getProperty("file.separator")+"cmd";
		File f = new File(fileName);
		if (f.exists()) {
			File[] files = f.listFiles();

			for (int i = 0; i < files.length; i++) {
				listmodel.addElement(files[i].getName());
			}
		}
		this.listCmd.setModel(listmodel);
	}
	
	private void executeCommand(){
		textAreaResult.setText("");
		String cmd = textAreaCmd.getText();
		String response = sendCommand(cmd);
		
		response = formatXML(response);
	//	response = XmlUtils.parseDoc(response);
		textAreaResult.setText(response);
		
	}
	
	private String formatXML(String text){
		if (text == null) return null;
		String response = null;
		Document doc;
		try {
			doc = XmlUtils.parseDoc(text);
			System.out.println("das angekommene DOC "+ XmlUtils.xml2String(doc));
		} catch (Exception exc){
			exc.printStackTrace();
			return null;
		}
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		XMLSerializer outserializer = new XMLSerializer(System.out, new OutputFormat(doc,"utf-8",true));		
			
		XMLSerializer serializer = new XMLSerializer(bao, new OutputFormat(doc,"UTF-8", true));		
		try {
			serializer.serialize(doc);
			outserializer.serialize(doc);
			response = bao.toString();
		} catch (Exception exc){
			response = exc.getMessage();
		}
		
		
		return response;
	}
	
	/*private Document parseDoc(String data){
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
	}*/
	
	private String readFile(String filename){
		File f = new File(filename);		
		if (!f.exists()) return null;
		StringBuffer buff = new StringBuffer();
		try {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String line = null;			
			while ((line = br.readLine())!=null){
				buff.append(line);
			}
			br.close();
		} catch (Exception exc){
			exc.printStackTrace();
		}
		return buff.toString();
	}
	
	private void initGui(){
		setLayout(new BorderLayout(0, 0));		
		
		JSplitPane splitPane = new JSplitPane();
		add(splitPane, BorderLayout.CENTER);
		
		scrollPaneCmdList = new JScrollPane();
		splitPane.setLeftComponent(scrollPaneCmdList);
		
		listCmd = new JList();
		listCmd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {			
					loadCmd();			
			}
		});
		
		scrollPaneCmdList.setViewportView(listCmd);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setRightComponent(splitPane_1);
		
		JScrollPane scrollPaneResult = new JScrollPane();
		splitPane_1.setRightComponent(scrollPaneResult);
		
		textAreaResult = new JTextArea();
		scrollPaneResult.setViewportView(textAreaResult);
		
		JPanel pnlCmd = new JPanel();
		pnlCmd.setPreferredSize(new Dimension(10, 150));
		splitPane_1.setLeftComponent(pnlCmd);
		pnlCmd.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(200, 10));
		pnlCmd.add(panel, BorderLayout.EAST);
		
		JButton btnNewButton = new JButton("send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				executeCommand();
			}
		});
		
		rbtnUseLocal = new JRadioButton("use local server");
		rbtnUseLocal.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				rdbtnNewRadioButton.setSelected(false);
				lbAddress.setEnabled(false);
				txtAddress.setEnabled(false);
				rbtnUseLocal.setSelected(true);
			}
		});
	
		rbtnUseLocal.setSelected(true);
		
		rdbtnNewRadioButton = new JRadioButton("use other");
		rdbtnNewRadioButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				rbtnUseLocal.setSelected(false);
				rdbtnNewRadioButton.setSelected(true);
				lbAddress.setEnabled(true);
				txtAddress.setEnabled(true);
			}
		});
		
		txtAddress = new JTextField();
		txtAddress.setEnabled(false);
		txtAddress.setColumns(10);
		
		lbAddress = new JLabel("Address");
		lbAddress.setEnabled(false);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(btnNewButton, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
								.addComponent(rbtnUseLocal, GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
								.addComponent(rdbtnNewRadioButton, GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(27)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(lbAddress)
								.addComponent(txtAddress, GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(rbtnUseLocal)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(rdbtnNewRadioButton)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lbAddress)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(txtAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnNewButton)
					.addContainerGap())
		);
		panel.setLayout(gl_panel);
		
		JScrollPane scrollPaneCmd = new JScrollPane();
		pnlCmd.add(scrollPaneCmd, BorderLayout.CENTER);
		
		textAreaCmd = new JTextArea();
		scrollPaneCmd.setViewportView(textAreaCmd);
		
		JPanel panelStatus = new JPanel();
		panelStatus.setPreferredSize(new Dimension(10, 30));
		add(panelStatus, BorderLayout.SOUTH);
		
		JLabel lblNewLabel = new JLabel("Status");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lblStatus = new JLabel("<status>");
		GroupLayout gl_panelStatus = new GroupLayout(panelStatus);
		gl_panelStatus.setHorizontalGroup(
			gl_panelStatus.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelStatus.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblStatus)
					.addContainerGap(304, Short.MAX_VALUE))
		);
		gl_panelStatus.setVerticalGroup(
			gl_panelStatus.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelStatus.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelStatus.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(lblStatus))
					.addContainerGap(50, Short.MAX_VALUE))
		);
		panelStatus.setLayout(gl_panelStatus);
	}	
	
	private JScrollPane scrollPaneCmdList;
	private JLabel lblStatus;
	private JList listCmd;
	private JTextArea textAreaCmd;
	private JTextArea textAreaResult;
	private JTextField txtAddress;
	private JRadioButton rdbtnNewRadioButton;
	private JRadioButton rbtnUseLocal;
	private JLabel lbAddress;
}
