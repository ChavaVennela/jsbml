/*
 * $Id$
 * $URL$
 * ---------------------------------------------------------------------
 * This file is part of the SBMLeditor API library.
 *
 * Copyright (C) 2011 by the University of Tuebingen, Germany.
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation. A copy of the license
 * agreement is provided in the file named "LICENSE.txt" included with
 * this software distribution and also available online as
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 * ---------------------------------------------------------------------
 */
package org.sbml.jsbml.cdplugin;

import java.beans.PropertyChangeEvent;

import javax.swing.tree.TreeNode;

import jp.sbi.celldesigner.plugin.CellDesignerPlugin;
import jp.sbi.celldesigner.plugin.PluginCompartment;
import jp.sbi.celldesigner.plugin.PluginCompartmentType;
import jp.sbi.celldesigner.plugin.PluginFunctionDefinition;
import jp.sbi.celldesigner.plugin.PluginModel;
import jp.sbi.celldesigner.plugin.PluginParameter;
import jp.sbi.celldesigner.plugin.PluginReaction;
import jp.sbi.celldesigner.plugin.PluginSimpleSpeciesReference;
import jp.sbi.celldesigner.plugin.PluginSpecies;
import jp.sbi.celldesigner.plugin.PluginSpeciesType;
import jp.sbi.celldesigner.plugin.PluginUnitDefinition;

import org.sbml.jsbml.JSBML;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.util.TreeNodeChangeEvent;
import org.sbml.jsbml.util.TreeNodeChangeListener;

/**
 * @author Andreas Dr&auml;ger
 * @version $Rev$
 * @date 10:50:22
 */
public class PluginChangeListener implements TreeNodeChangeListener {

	private CellDesignerPlugin plugin;
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		Object eventsource = event.getSource();

		String prop = event.getPropertyName();
		if (prop.equals(TreeNodeChangeEvent.about)) {

		} else if (prop.equals(TreeNodeChangeEvent.addCVTerm)) {

		} else if (prop.equals(TreeNodeChangeEvent.addDeclaredNamespace)) {

		}
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sbml.jsbml.util.TreeNodeChangeListener#nodeAdded(javax.swing.tree
	 * .TreeNode)
	 */
	public void nodeAdded(TreeNode node) {
		if (node instanceof PluginCompartmentType) {
			PluginCompartmentType pt = ((PluginCompartmentType) node);
			plugin.notifySBaseAdded(pt);
			
		} else if (node instanceof PluginModel) {
			PluginModel pt = (PluginModel) node;
			plugin.notifySBaseAdded(pt);
			
		} else if (node instanceof PluginReaction) {
			PluginReaction pt = (PluginReaction) node;
			plugin.notifySBaseAdded(pt);
			
		} else if (node instanceof PluginSimpleSpeciesReference) {
			PluginSimpleSpeciesReference pt = (PluginSimpleSpeciesReference) node;
			plugin.notifySBaseAdded(pt);
		} else if (node instanceof PluginSpeciesType) {
			PluginSpeciesType pt = (PluginSpeciesType) node;
			plugin.notifySBaseAdded(pt);
		} else if (node instanceof PluginCompartment) {
			PluginCompartment pt = (PluginCompartment) node;
			plugin.notifySBaseAdded(pt);
		} else if (node instanceof PluginParameter) {
			PluginParameter pt = (PluginParameter) node;
			plugin.notifySBaseAdded(pt);
		} else if (node instanceof PluginSpecies) {
			PluginSpecies pt = (PluginSpecies) node;
			plugin.notifySBaseAdded(pt);
		} else if (node instanceof PluginUnitDefinition) {
			PluginUnitDefinition pt = (PluginUnitDefinition) node;
			plugin.notifySBaseAdded(pt);
		} else if (node instanceof PluginFunctionDefinition) {
			PluginFunctionDefinition pt = (PluginFunctionDefinition) node;
			plugin.notifySBaseAdded(pt);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sbml.jsbml.util.TreeNodeChangeListener#nodeRemoved(javax.swing.tree
	 * .TreeNode)
	 */
	public void nodeRemoved(TreeNode node) {
		if (node instanceof PluginCompartmentType) {
			PluginCompartmentType pt = ((PluginCompartmentType) node);
			plugin.notifySBaseDeleted(pt);
			
		} else if (node instanceof PluginModel) {
			PluginModel pt = (PluginModel) node;
			plugin.notifySBaseDeleted(pt);
			
		} else if (node instanceof PluginReaction) {
			PluginReaction pt = (PluginReaction) node;
			plugin.notifySBaseDeleted(pt);
			
		} else if (node instanceof PluginSimpleSpeciesReference) {
			PluginSimpleSpeciesReference pt = (PluginSimpleSpeciesReference) node;
			plugin.notifySBaseDeleted(pt);
		} else if (node instanceof PluginSpeciesType) {
			PluginSpeciesType pt = (PluginSpeciesType) node;
			plugin.notifySBaseDeleted(pt);
		} else if (node instanceof PluginCompartment) {
			PluginCompartment pt = (PluginCompartment) node;
			plugin.notifySBaseDeleted(pt);
		} else if (node instanceof PluginParameter) {
			PluginParameter pt = (PluginParameter) node;
			plugin.notifySBaseDeleted(pt);
		} else if (node instanceof PluginSpecies) {
			PluginSpecies pt = (PluginSpecies) node;
			plugin.notifySBaseDeleted(pt);
		} else if (node instanceof PluginUnitDefinition) {
			PluginUnitDefinition pt = (PluginUnitDefinition) node;
			plugin.notifySBaseDeleted(pt);
		} else if (node instanceof PluginFunctionDefinition) {
			PluginFunctionDefinition pt = (PluginFunctionDefinition) node;
			plugin.notifySBaseDeleted(pt);
		}
	}
}
