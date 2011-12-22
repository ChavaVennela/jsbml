package org.sbml.jsbml.cdplugin.test;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;

import jp.sbi.celldesigner.plugin.PluginAction;

public class SimpleCellDesignerPluginAction extends PluginAction{

	private CDPluginTester plugin;
	
	public SimpleCellDesignerPluginAction(CDPluginTester plugTest) {
		this.plugin = plugTest;
	}

	@Override
	public void myActionPerformed(ActionEvent ae) {
		if (ae.getSource() instanceof JMenuItem){
			String itemText = ((JMenuItem) ae.getSource()).getText();
			if (itemText.equals(CDPluginTester.ACTION)) {
				plugin.startPlugin();
			}
		} else {
			System.err.printf("Unsupported source of action %s \n", ae.getSource().getClass().getName());
		}
		
	}
	
	
}
