package org.sbml.jsbml.cdplugin.test;

import jp.sbi.celldesigner.plugin.CellDesignerPlugin;
import jp.sbi.celldesigner.plugin.PluginSBase;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.cdplugin.PluginChangeListener;

/**
 * This class is used to test the synchronization between Celldesigner and JSBML.
 * 
 * @author Alexander Peltzer
 *
 */

public class CDPluginTester extends CellDesignerPlugin {
	SBMLDocument doc = new SBMLDocument();
	PluginChangeListener plugChangeListener = new PluginChangeListener(doc , this);
	/*
	 * Constructors
	 */
	
	public CDPluginTester(){
		super();
	}

	/* (non-Javadoc)
	 * @see jp.sbi.celldesigner.plugin.CellDesignerPlug#SBaseAdded(jp.sbi.celldesigner.plugin.PluginSBase)
	 */
	public void SBaseAdded(PluginSBase arg0) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see jp.sbi.celldesigner.plugin.CellDesignerPlug#SBaseChanged(jp.sbi.celldesigner.plugin.PluginSBase)
	 */
	public void SBaseChanged(PluginSBase arg0) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see jp.sbi.celldesigner.plugin.CellDesignerPlug#SBaseDeleted(jp.sbi.celldesigner.plugin.PluginSBase)
	 */
	public void SBaseDeleted(PluginSBase arg0) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see jp.sbi.celldesigner.plugin.CellDesignerPlug#addPluginMenu()
	 */
	public void addPluginMenu() {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see jp.sbi.celldesigner.plugin.CellDesignerPlug#modelClosed(jp.sbi.celldesigner.plugin.PluginSBase)
	 */
	public void modelClosed(PluginSBase arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see jp.sbi.celldesigner.plugin.CellDesignerPlug#modelOpened(jp.sbi.celldesigner.plugin.PluginSBase)
	 */
	public void modelOpened(PluginSBase arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see jp.sbi.celldesigner.plugin.CellDesignerPlug#modelSelectChanged(jp.sbi.celldesigner.plugin.PluginSBase)
	 */
	public void modelSelectChanged(PluginSBase arg0) {
		// TODO Auto-generated method stub
		
	}
}
