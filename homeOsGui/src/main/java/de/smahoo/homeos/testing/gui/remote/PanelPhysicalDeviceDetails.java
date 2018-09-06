package de.smahoo.homeos.testing.gui.remote;

import org.w3c.dom.Element;

import de.smahoo.homeos.common.Function;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.remote.physical.RemotePhysicalDevice;
import de.smahoo.homeos.testing.gui.PanelDetails;

import javax.swing.JSplitPane;
import java.awt.CardLayout;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Font;
import java.util.List;

import javax.swing.LayoutStyle.ComponentPlacement;


public class PanelPhysicalDeviceDetails extends PanelDetails{

	RemotePhysicalDevice device;
	
	
	public PanelPhysicalDeviceDetails(RemotePhysicalDevice device){
		this.device = device;
		initGui();		
		update();
	}
	
	public void update(){
		if (device == null){
			
		} else {
			this.lbDriverCompany.setText(device.getDriverCompany());
			this.lbDriverName.setText(device.getDriverName()+" "+device.getDriverVersion());			
		}
		
		
		
		updatePropertyTable();
		updateFunctionList();
	}
	
	private void updatePropertyTable(){
		if (device == null) return;
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("name");
		model.addColumn("value");
		List<Property> props = device.getProperties();
		int rowCnt = 0;
		String[] row;
		for (Property prop : props){
			row = new String[2];
			row[0] = prop.getName();
			if (prop.isValueSet()){
				row[1] = prop.getValue().toString();
				if (prop.hasUnit()){
					row[1] = row[1]+" "+prop.getUnit();
				}
			} else {
				row[1] = "---";
			}
			model.insertRow(rowCnt++, row);
		}
		this.tableProperties.setModel(model);
	}
	
	private void updateFunctionList(){
		if (device == null) return;
		DefaultListModel model = new DefaultListModel();
		List<Function> functions = device.getFunctions();
		
		for (Function function : functions){		
			model.addElement(function.getName());
		}
		
		listFunctions.setModel(model);
	}
	
	private void initGui(){
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane);
		
		JPanel pnlProperties = new JPanel();
		pnlProperties.setPreferredSize(new Dimension(10, 200));
		splitPane.setLeftComponent(pnlProperties);
		pnlProperties.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlPropertiesCaption = new JPanel();
		pnlPropertiesCaption.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlPropertiesCaption.setPreferredSize(new Dimension(10, 25));
		pnlProperties.add(pnlPropertiesCaption, BorderLayout.NORTH);
		pnlPropertiesCaption.setLayout(new CardLayout(0, 0));
		
		lbCaptionProperties = new JLabel("Properties");
		lbCaptionProperties.setHorizontalAlignment(SwingConstants.CENTER);
		pnlPropertiesCaption.add(lbCaptionProperties, "name_15775768040072");
		
		JScrollPane scrollPaneProperties = new JScrollPane();
		scrollPaneProperties.setBorder(null);
		pnlProperties.add(scrollPaneProperties, BorderLayout.CENTER);
		
		tableProperties = new JTable();
		scrollPaneProperties.setViewportView(tableProperties);
		
		JPanel pnlFunctions = new JPanel();
		splitPane.setRightComponent(pnlFunctions);
		pnlFunctions.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlCaptionFunctions = new JPanel();
		pnlCaptionFunctions.setPreferredSize(new Dimension(10, 25));
		pnlCaptionFunctions.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlFunctions.add(pnlCaptionFunctions, BorderLayout.NORTH);
		
		JLabel lblNewLabel = new JLabel("Functions");
		pnlCaptionFunctions.add(lblNewLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		pnlFunctions.add(scrollPane, BorderLayout.CENTER);
		
		listFunctions = new JList();
		scrollPane.setViewportView(listFunctions);
		
		JPanel pnlDetails = new JPanel();
		pnlDetails.setPreferredSize(new Dimension(10, 200));
		add(pnlDetails, BorderLayout.NORTH);
		
		lbDeviceId = new JLabel("New label");
		lbDeviceId.setFont(new Font("Tahoma", Font.BOLD, 14));
		lbDeviceId.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblNewLabel_1 = new JLabel("Driver");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lbDriverCompany = new JLabel("New label");
		
		lbDriverName = new JLabel("New label");
		GroupLayout gl_pnlDetails = new GroupLayout(pnlDetails);
		gl_pnlDetails.setHorizontalGroup(
			gl_pnlDetails.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlDetails.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlDetails.createParallelGroup(Alignment.LEADING)
						.addComponent(lbDeviceId, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addGroup(gl_pnlDetails.createSequentialGroup()
							.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addGroup(gl_pnlDetails.createParallelGroup(Alignment.LEADING)
								.addComponent(lbDriverCompany, GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
								.addComponent(lbDriverName, GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE))))
					.addContainerGap())
		);
		gl_pnlDetails.setVerticalGroup(
			gl_pnlDetails.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlDetails.createSequentialGroup()
					.addContainerGap()
					.addComponent(lbDeviceId)
					.addGap(18)
					.addGroup(gl_pnlDetails.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1)
						.addComponent(lbDriverCompany, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lbDriverName, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(120, Short.MAX_VALUE))
		);
		pnlDetails.setLayout(gl_pnlDetails);
	}
	
	private JTable tableProperties;
	private JLabel lbCaptionProperties;
	private JList listFunctions;
	private JLabel lbDeviceId;
	private JLabel lbDriverCompany;
	private JLabel lbDriverName;
}
