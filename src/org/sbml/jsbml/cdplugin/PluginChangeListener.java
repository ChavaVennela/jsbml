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
import jp.sbi.celldesigner.plugin.PluginConstraint;
import jp.sbi.celldesigner.plugin.PluginEvent;
import jp.sbi.celldesigner.plugin.PluginEventAssignment;
import jp.sbi.celldesigner.plugin.PluginFunctionDefinition;
import jp.sbi.celldesigner.plugin.PluginInitialAssignment;
import jp.sbi.celldesigner.plugin.PluginKineticLaw;
import jp.sbi.celldesigner.plugin.PluginListOf;
import jp.sbi.celldesigner.plugin.PluginModel;
import jp.sbi.celldesigner.plugin.PluginParameter;
import jp.sbi.celldesigner.plugin.PluginReaction;
import jp.sbi.celldesigner.plugin.PluginSimpleSpeciesReference;
import jp.sbi.celldesigner.plugin.PluginSpecies;
import jp.sbi.celldesigner.plugin.PluginSpeciesReference;
import jp.sbi.celldesigner.plugin.PluginSpeciesType;
import jp.sbi.celldesigner.plugin.PluginUnitDefinition;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.sbml.jsbml.AlgebraicRule;
import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.AssignmentRule;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.CompartmentType;
import org.sbml.jsbml.Constraint;
import org.sbml.jsbml.Creator;
import org.sbml.jsbml.Delay;
import org.sbml.jsbml.Event;
import org.sbml.jsbml.EventAssignment;
import org.sbml.jsbml.FunctionDefinition;
import org.sbml.jsbml.History;
import org.sbml.jsbml.InitialAssignment;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.MathContainer;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Priority;
import org.sbml.jsbml.RateRule;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Rule;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SimpleSpeciesReference;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.SpeciesType;
import org.sbml.jsbml.StoichiometryMath;
import org.sbml.jsbml.Trigger;
import org.sbml.jsbml.Unit;
import org.sbml.jsbml.UnitDefinition;
import org.sbml.jsbml.util.TreeNodeChangeEvent;
import org.sbml.jsbml.util.TreeNodeChangeListener;
import org.sbml.libsbml.SBase;

/**
 * @author Alexander Peltzer
 * @author Andreas Dr&auml;ger
 * @version $Rev$
 * @date 10:50:22
 */
@SuppressWarnings("deprecation")
public class PluginChangeListener implements TreeNodeChangeListener {

	/**
   * 
   */
	private CellDesignerPlugin plugin;

	/**
	 * 
	 */
	private static final transient Logger logger = Logger
			.getLogger(PluginChangeListener.class);

	/**
	 * 
	 */
	private PluginModel plugModel;

	/**
	 * 
	 * @param plugin
	 */
	public PluginChangeListener(SBMLDocument doc, CellDesignerPlugin plugin) {
		this.plugin = plugin;
		this.plugModel = plugin.getSelectedModel();
		if (doc != null) {
			Model model = doc.getModel();
			if (model != null) {

			}
		}
	}

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
		else if (prop.equals(TreeNodeChangeEvent.charge)) {
			Species species = (Species) event.getSource();
			PluginSpecies plugSpec = plugModel.getSpecies(species.getId());
			plugSpec.setCharge(species.getCharge());
			plugin.notifySBaseChanged(plugSpec);
		} else if (prop.equals(TreeNodeChangeEvent.math)) {
			MathContainer mathContainer = (MathContainer) event.getSource();
			// TODO check which corresponding element can be found in
			// CellDesigner
			if (mathContainer instanceof Constraint) {
				// TODO
				Constraint c = (Constraint) mathContainer;

			}
			// ...
			else if (mathContainer instanceof KineticLaw) {
				Reaction r = ((KineticLaw) mathContainer).getParent();
				PluginReaction plugReac = plugModel.getReaction(r.getId());
				if (plugReac != null) {
					PluginKineticLaw plugKl = plugReac.getKineticLaw();
					// plugKl.setMath(); // see PluginSBMLWriter

				}
			}
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
		if (node instanceof CompartmentType) {
			CompartmentType ct = (CompartmentType) node;
			PluginCompartmentType pt = new PluginCompartmentType(ct.getId());
			if (ct.isSetName() && !pt.getName().equals(ct.getName())) {
				pt.setName(ct.getName());
				plugin.notifySBaseAdded(pt); 
			}
			logger.log(Level.DEBUG, "Cannot parse node" + node.getClass().getSimpleName());
		} else if (node instanceof Species) {
			Species sp = (Species) node;
			//TODO Is species type right here in the constructor ?
			PluginSpecies plugsp = new PluginSpecies(sp.getSpeciesType(), sp.getName());
			if (sp.isSetName() && !sp.getName().equals(plugsp.getName())) {
				//TODO PluginSpecies.setName() is not visible. Is this intended ? The other classes allow a setName() call.
				plugsp.setNotes(sp.getName());
				plugin.notifySBaseAdded(plugsp);
			}
			logger.log(Level.DEBUG, "Cannot parse node" + node.getClass().getSimpleName());
		} else if (node instanceof Reaction) {
			Reaction react = (Reaction) node;
			PluginReaction plugreac = new PluginReaction();
			if (react.isSetName() && !react.getName().equals(plugreac.getName())){
				plugreac.setName(react.getName());
				plugin.notifySBaseAdded(plugreac);
			}
			logger.log(Level.DEBUG, "Cannot parse node" + node.getClass().getSimpleName());
			
		} else if (node instanceof SpeciesType) {
			SpeciesType speciestype = (SpeciesType) node;
			PluginSpeciesType plugspectype = new PluginSpeciesType(speciestype.getId());
			if (speciestype.isSetName() && !speciestype.getName().equals(plugspectype.getName())){
				plugspectype.setName(speciestype.getName());
				plugin.notifySBaseAdded(plugspectype);
			}
			logger.log(Level.DEBUG, "Cannot parse node" + node.getClass().getSimpleName());
		} else if (node instanceof org.sbml.jsbml.Parameter) {
			org.sbml.jsbml.Parameter param = (org.sbml.jsbml.Parameter) node;
			if (param.getParent() instanceof KineticLaw){
				PluginParameter plugparam = new PluginParameter((PluginKineticLaw) param.getParent());
				if (param.isSetName() && !param.getName().equals(plugparam.getName())){
					plugparam.setName(param.getName());
					plugin.notifySBaseAdded(plugparam);
				}
				logger.log(Level.DEBUG, "Cannot parse node" + node.getClass().getSimpleName());
				
			} else if (param.getParent() instanceof Model){
				PluginParameter plugparam = new PluginParameter((PluginModel) param.getParent());
				if (param.isSetName() && !param.getName().equals(plugparam.getName())){
					plugparam.setName(param.getName());
					plugin.notifySBaseAdded(plugparam);
				}
				logger.log(Level.DEBUG, "Cannot parse node" + node.getClass().getSimpleName());
			}
			
		} else if (node instanceof FunctionDefinition) {
			FunctionDefinition funcdef = (FunctionDefinition) node;
			PluginFunctionDefinition plugfuncdef = new PluginFunctionDefinition(funcdef.getId());
			if (funcdef.isSetName() && !plugfuncdef.getName().equals(funcdef.getName())) {
				plugfuncdef.setName(funcdef.getName());
				plugin.notifySBaseAdded(plugfuncdef);
			}
			logger.log(Level.DEBUG, "Cannot parse node" + node.getClass().getSimpleName());

		} else if (node instanceof Compartment) {
			Compartment comp = (Compartment) node;
			
		} else if (node instanceof SpeciesReference) {
			SpeciesReference specRef = (SpeciesReference) node;
			SBase sbase = (SBase) specRef.getParent();
			// TODO What do we do with such an SBase Type ?
		} else if (node instanceof LocalParameter) {
			LocalParameter locparam = (LocalParameter) node;
			ListOf<LocalParameter> lop = locparam.getParentSBMLObject();
			KineticLaw kl = (KineticLaw) lop.getParentSBMLObject();
			Reaction r = kl.getParentSBMLObject();
			
		} else if (node instanceof SimpleSpeciesReference) {
			SimpleSpeciesReference simspec = (SimpleSpeciesReference) node;
			// What to do with Treenode?
			// TODO Has no ID, crosscheck this
		} else if (node instanceof UnitDefinition) {
			UnitDefinition undef = (UnitDefinition) node;
			
		} else if (node instanceof Event) {
			Event event = (Event) node;
			PluginEvent plugevent = new PluginEvent(event.getId());
			if (event.isSetName() && !event.getName().equals(plugevent.getName())){
				plugevent.setName(event.getName());
				plugin.notifySBaseAdded(plugevent);
			}
			logger.log(Level.DEBUG, "Cannot parse node" + node.getClass().getSimpleName());
		} else if (node instanceof RateRule) {
			RateRule rule = (RateRule) node;
			// TODO This has to be hashed somehow
		} else if (node instanceof AssignmentRule) {
			AssignmentRule assignRule = (AssignmentRule) node;
			// TODO This has to be hashed somehow
		} else if (node instanceof KineticLaw) {
			KineticLaw klaw = (KineticLaw) node;
			Reaction parentreaction = klaw.getParentSBMLObject();
			PluginKineticLaw plugklaw = plugModel.getReaction(
					parentreaction.getId()).getKineticLaw();
			PluginReaction plugreac = plugModel.getReaction(parentreaction.getId());
			plugreac.setKineticLaw(plugklaw);
			plugin.notifySBaseAdded(plugreac);
			logger.log(Level.DEBUG, "Cannot parse node" + node.getClass().getSimpleName());
		} else if (node instanceof InitialAssignment) {
			InitialAssignment iAssign = (InitialAssignment) node;
			// TODO This has to be hashed somehow.
		} else if (node instanceof EventAssignment) {
			EventAssignment eAssign = (EventAssignment) node;
		} else if (node instanceof StoichiometryMath) {
			StoichiometryMath stoich = (StoichiometryMath) node;
			// TODO no class in CD for that ?
		} else if (node instanceof Trigger) {
			// TODO no class in CD for that ?
		} else if (node instanceof Rule) {
			Rule rule = (Rule) node;
			// TODO This has to be hashed somehow
		} else if (node instanceof AlgebraicRule) {
			AlgebraicRule alrule = (AlgebraicRule) node;
			// TODO This has to be hashed somehow
		} else if (node instanceof Constraint) {
			Constraint ct = (Constraint) node;
			// TODO This has to be hashed somehow
		} else if (node instanceof Delay) {
			Delay dl = (Delay) node;
			// TODO no counter class in CD available
			// Therefore unnecessary to implement this?
		} else if (node instanceof Priority) {
			Priority prt = (Priority) node;
			// TODO no counter class in CD available
			// Therefore unnecessary to implement this?
		} else if (node instanceof Unit) {
			Unit ut = (Unit) node;
			// TODO no counter class in CD available
			// Therefore unnecessary to implement this?
		} else if (node instanceof SBMLDocument) {
			SBMLDocument doc = (SBMLDocument) node;
			// TODO no counter class in CD available
			// Therefore unnecessary to implement this?
		} else if (node instanceof ListOf) {
			ListOf listof = (ListOf) node;
			// PluginListOf pluglistof = plugModel.getListof???
			// TODO Parse all lists or what has to be done here?
		} else if (node instanceof CVTerm){
			//TODO
		} else if (node instanceof History){
			//TODO
		} else if (node instanceof Annotation){
			//TODO
		} else if (node instanceof Creator){
			//TODO
		
		} else {
			logger.warn(String.format("Could not process %s.", node.toString()));
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
		if (node instanceof CompartmentType) {
			CompartmentType ct = (CompartmentType) node;
			PluginCompartmentType pt = plugModel.getCompartmentType(ct.getId());
			plugModel.removeCompartmentType(ct.getId());
			plugin.notifySBaseDeleted(pt);
		} else if (node instanceof Species) {
			Species sp = (Species) node;
			PluginSpecies ps = plugModel.getSpecies(sp.getId());
			plugModel.removeSpecies(sp.getId());
			plugin.notifySBaseDeleted(ps);
		} else if (node instanceof Reaction) {
			Reaction react = (Reaction) node;
			PluginReaction preac = plugModel.getReaction(react.getId());
			plugModel.removeReaction(react.getId());
			plugin.notifySBaseDeleted(preac);
		} else if (node instanceof SpeciesType) {
			SpeciesType speciestype = (SpeciesType) node;
			PluginSpeciesType pspec = plugModel.getSpeciesType(speciestype
					.getId());
			plugModel.removeSpeciesType(speciestype.getId());
			plugin.notifySBaseDeleted(pspec);
		} else if (node instanceof org.sbml.jsbml.Parameter) {
			org.sbml.jsbml.Parameter param = (org.sbml.jsbml.Parameter) node;
			PluginParameter plugParam = plugModel.getParameter(param.getId());
			plugModel.removeParameter(param.getId());
			plugin.notifySBaseDeleted(plugParam);
		} else if (node instanceof FunctionDefinition) {
			FunctionDefinition funcdef = (FunctionDefinition) node;
			PluginFunctionDefinition plugFuncdef = plugModel
					.getFunctionDefinition(funcdef.getId());
			plugModel.removeFunctionDefinition(funcdef.getId());
			plugin.notifySBaseDeleted(plugFuncdef);
		} else if (node instanceof Compartment) {
			Compartment comp = (Compartment) node;
			PluginCompartment plugComp = plugModel.getCompartment(comp.getId());
			plugModel.removeCompartment(comp.getId());
			plugin.notifySBaseDeleted(plugComp);
		} else if (node instanceof SpeciesReference) {
			SpeciesReference specRef = (SpeciesReference) node;
			SBase sbase = (SBase) specRef.getParent();
			// TODO What do we do with such an SBase Type ?

		} else if (node instanceof LocalParameter) {
			LocalParameter locparam = (LocalParameter) node;
			ListOf<LocalParameter> lop = locparam.getParentSBMLObject();
			KineticLaw kl = (KineticLaw) lop.getParentSBMLObject();
			Reaction r = kl.getParentSBMLObject();
			// PluginKineticLaw plugkl =
			// plugModel.getReaction(r.getId()).getKineticLaw();
			// plugin.notifySBaseDeleted(sbase)
			// TODO USE PARAMETER in PluginKineticLaw.
		} else if (node instanceof SimpleSpeciesReference) {
			SimpleSpeciesReference simspec = (SimpleSpeciesReference) node;
			// What to do with Treenode?
			// TODO Has no ID, crosscheck this
		} else if (node instanceof UnitDefinition) {
			UnitDefinition undef = (UnitDefinition) node;
			PluginUnitDefinition plugUndef = plugModel.getUnitDefinition(undef
					.getId());
			plugModel.removeUnitDefinition(undef.getId());
			plugin.notifySBaseDeleted(plugUndef);
		} else if (node instanceof Event) {
			Event event = (Event) node;
			PluginEvent plugEvent = plugModel.getEvent(event.getId());
			plugModel.removeEvent(event.getId());
			plugin.notifySBaseDeleted(plugEvent);
		} else if (node instanceof RateRule) {
			RateRule rule = (RateRule) node;
			// TODO This has to be hashed somehow
		} else if (node instanceof AssignmentRule) {
			AssignmentRule assignRule = (AssignmentRule) node;
			// TODO This has to be hashed somehow
		} else if (node instanceof KineticLaw) {
			KineticLaw klaw = (KineticLaw) node;
			Reaction parentreaction = klaw.getParentSBMLObject();
			PluginKineticLaw plugklaw = plugModel.getReaction(
					parentreaction.getId()).getKineticLaw();
			// plugModel.removeR
			// Do we have to remove the whole Reaction here or only the
			// KineticLaw ?
			// TODO crosscheck, no ID available
		} else if (node instanceof InitialAssignment) {
			InitialAssignment iAssign = (InitialAssignment) node;
			// TODO This has to be hashed somehow.
		} else if (node instanceof EventAssignment) {
			EventAssignment eAssign = (EventAssignment) node;
			ListOf<EventAssignment> elist = eAssign.getParent();
			Event e = (Event) elist.getParentSBMLObject();
			PluginEventAssignment plugEventAssignment = plugModel.getEvent(
					e.getId()).getEventAssignment(eAssign.getIndex(node));
			plugin.notifySBaseDeleted(plugEventAssignment);
		} else if (node instanceof StoichiometryMath) {
			StoichiometryMath stoich = (StoichiometryMath) node;
			// TODO no class in CD for that ?
		} else if (node instanceof Trigger) {
			// TODO no class in CD for that ?
		} else if (node instanceof Rule) {
			Rule rule = (Rule) node;
			// TODO This has to be hashed somehow
		} else if (node instanceof AlgebraicRule) {
			AlgebraicRule alrule = (AlgebraicRule) node;
			// TODO This has to be hashed somehow
		} else if (node instanceof Constraint) {
			Constraint ct = (Constraint) node;
			// TODO This has to be hashed somehow
		} else if (node instanceof Delay) {
			Delay dl = (Delay) node;
			// TODO no counter class in CD available
			// Therefore unnecessary to implement this?
		} else if (node instanceof Priority) {
			Priority prt = (Priority) node;
			// TODO no counter class in CD available
			// Therefore unnecessary to implement this?
		} else if (node instanceof Unit) {
			Unit ut = (Unit) node;
			// TODO no counter class in CD available
			// Therefore unnecessary to implement this?
		} else if (node instanceof SBMLDocument) {
			SBMLDocument doc = (SBMLDocument) node;
			// TODO no counter class in CD available
			// Therefore unnecessary to implement this?
		} else if (node instanceof ListOf) {
			ListOf listof = (ListOf) node;
			// PluginListOf pluglistof = plugModel.getListof???
			// TODO Parse all lists or what has to be done here?
		} else if (node instanceof CVTerm){
			CVTerm term = (CVTerm) node;
			
			//TODO
		} else if (node instanceof History){
			//TODO
		} else if (node instanceof Annotation){
			//TODO
		} else if (node instanceof Creator){
			//TODO
		} 
	}
}
