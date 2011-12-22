package org.sbml.jsbml.cdplugin.test;

import java.beans.PropertyChangeEvent;

import org.sbml.jsbml.Event;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.cdplugin.PluginChangeListener;

import jp.sbi.celldesigner.plugin.CellDesignerPlugin;
import jp.sbi.celldesigner.plugin.PluginSBase;

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
		
		
	}

	@Override
	public void SBaseAdded(PluginSBase arg0) {
		// TODO Auto-generated method stub

		
	}

	@Override
	public void SBaseChanged(PluginSBase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void SBaseDeleted(PluginSBase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPluginMenu() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modelClosed(PluginSBase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modelOpened(PluginSBase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modelSelectChanged(PluginSBase arg0) {
		// TODO Auto-generated method stub
		
	}
}
