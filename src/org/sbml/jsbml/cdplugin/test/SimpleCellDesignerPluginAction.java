package org.sbml.jsbml.cdplugin.test;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;

import jp.sbi.celldesigner.plugin.PluginAction;

public class SimpleCellDesignerPluginAction extends PluginAction{

	/**
   * Generated serial version identifier.
   */
  private static final long serialVersionUID = 2080508810969190318L;
  
  private CDPluginTester plugin;
	
	public SimpleCellDesignerPluginAction(CDPluginTester plugTest) {
		this.plugin = plugTest;
	}

	/* (non-Javadoc)
	 * @see jp.sbi.celldesigner.plugin.PluginActionListener#myActionPerformed(java.awt.event.ActionEvent)
	 */
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
