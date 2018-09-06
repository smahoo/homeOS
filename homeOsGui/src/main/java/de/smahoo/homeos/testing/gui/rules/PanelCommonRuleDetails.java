package de.smahoo.homeos.testing.gui.rules;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.w3c.dom.Element;

import de.smahoo.homeos.automation.Rule;
import de.smahoo.homeos.automation.RuleFactory;
import de.smahoo.homeos.testing.gui.PanelDetails;

import java.awt.Font;
import javax.swing.JEditorPane;
import javax.swing.JCheckBox;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelCommonRuleDetails extends PanelDetails{
	
	private Rule rule = null;
	
	public void update(){
		setDetails();
	}
	
	public void setRule(Rule rule){
		this.rule = rule;
		update();
	}
	
	private void setDetails(){
		if (rule == null){
			textFieldRuleName.setText("");
			edtDescription.setText("");
			chkEnabled.setSelected(false);
			txtXmlSource.setText("");
			lbApplicable.setText("");
			
		} else {
			textFieldRuleName.setText(rule.getName());
			edtDescription.setText(rule.getDescription());
			chkEnabled.setSelected(rule.isEnabled());
			txtXmlSource.setText(getString(rule.getXmlSource()));
			if (rule.isApplicable()){
				lbApplicable.setText("true");
			} else {
				lbApplicable.setText("false");
			}
		}
		btnCheckAppliance.setEnabled(rule != null);
		textFieldRuleName.setEnabled(rule != null);
		edtDescription.setEditable(rule != null);
		chkEnabled.setEnabled(rule != null);
	}
	
	private String getString(Element elem){
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(buffer);

			DOMSource source = new DOMSource(elem);
			TransformerFactory.newInstance().newTransformer().transform(source, result);

			String res = new String(buffer.toByteArray());
			res = res.replace("\t", "");
			res = res.replace("?>", "?>\r\n");
			return res;
			
		} catch (Exception exc){
			return "unable to parse xmlSource\r\n"+exc.getMessage();
		}

	}
	
	public PanelCommonRuleDetails() {		
		initGui();
		update();
	}
	
	private void setRuleName(){
		if (rule == null) return;
		rule.setName(textFieldRuleName.getText());
	}
	
	private void setRuleDescription(){
		if (rule == null) return;
		rule.setDescription(edtDescription.getText());
	}
	
	private void setRuleEnabled(){
		if (rule == null) return;
		rule.setEnabled(chkEnabled.isSelected());
	}
	
	private void checkRuleAppliance(){
		RuleFactory.getInstance().checkForApplicability(rule);		
	}
	
	private void initGui(){
		JLabel lblNewLabel = new JLabel("Rule Name");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textFieldRuleName = new JTextField();
		textFieldRuleName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					setRuleName();
				}
			}
		});
		textFieldRuleName.setText("<ruleName>");
		textFieldRuleName.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Rule");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblNewLabel_2 = new JLabel("Description");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		
		edtDescription = new JEditorPane();
		edtDescription.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER);
				setRuleDescription();
			}
		});
		
		chkEnabled = new JCheckBox("Enabled");
		chkEnabled.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				setRuleEnabled();
			}
		});
		
		chkEnabled.setHorizontalAlignment(SwingConstants.LEFT);
		
		JLabel lblNewLabel_3 = new JLabel("is applicable");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lbApplicable = new JLabel("<applicable>");
		
		btnCheckAppliance = new JButton("check again");
		btnCheckAppliance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				checkRuleAppliance();
			}
		});
		
		JLabel lblNewLabel_5 = new JLabel("XML Source");
		lblNewLabel_5.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(lblNewLabel_2, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNewLabel_3, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNewLabel_5, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
								.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
									.addComponent(lbApplicable, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnCheckAppliance, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE))
								.addComponent(edtDescription)
								.addComponent(textFieldRuleName, GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
								.addComponent(chkEnabled, GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE))))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(7)
					.addComponent(lblNewLabel_1)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(textFieldRuleName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_2)
						.addComponent(edtDescription, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE))
					.addGap(10)
					.addComponent(chkEnabled, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
					.addGap(7)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_3)
						.addComponent(lbApplicable)
						.addComponent(btnCheckAppliance))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_5)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		txtXmlSource = new JTextArea();
		scrollPane.setViewportView(txtXmlSource);
		setLayout(groupLayout);
	}
	
	private JTextField textFieldRuleName;
	private JEditorPane edtDescription;
	private JCheckBox chkEnabled;
	private JLabel lbApplicable;
	private JButton btnCheckAppliance;
	private JTextArea txtXmlSource;
}
