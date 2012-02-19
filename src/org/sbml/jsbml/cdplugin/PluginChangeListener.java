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
import jp.sbi.celldesigner.plugin.PluginAlgebraicRule;
import jp.sbi.celldesigner.plugin.PluginAntiSenseRNA;
import jp.sbi.celldesigner.plugin.PluginAssignmentRule;
import jp.sbi.celldesigner.plugin.PluginCompartment;
import jp.sbi.celldesigner.plugin.PluginCompartmentType;
import jp.sbi.celldesigner.plugin.PluginConstraint;
import jp.sbi.celldesigner.plugin.PluginDoSthAbstractAction;
import jp.sbi.celldesigner.plugin.PluginEvent;
import jp.sbi.celldesigner.plugin.PluginEventAssignment;
import jp.sbi.celldesigner.plugin.PluginFunctionDefinition;
import jp.sbi.celldesigner.plugin.PluginInitialAssignment;
import jp.sbi.celldesigner.plugin.PluginKineticLaw;
import jp.sbi.celldesigner.plugin.PluginListOf;
import jp.sbi.celldesigner.plugin.PluginModel;
import jp.sbi.celldesigner.plugin.PluginModifierSpeciesReference;
import jp.sbi.celldesigner.plugin.PluginParameter;
import jp.sbi.celldesigner.plugin.PluginRateRule;
import jp.sbi.celldesigner.plugin.PluginReaction;
import jp.sbi.celldesigner.plugin.PluginRule;
import jp.sbi.celldesigner.plugin.PluginSBase;
import jp.sbi.celldesigner.plugin.PluginSimpleSpeciesReference;
import jp.sbi.celldesigner.plugin.PluginSpecies;
import jp.sbi.celldesigner.plugin.PluginSpeciesAlias;
import jp.sbi.celldesigner.plugin.PluginSpeciesReference;
import jp.sbi.celldesigner.plugin.PluginSpeciesType;
import jp.sbi.celldesigner.plugin.PluginStructuralState;
import jp.sbi.celldesigner.plugin.PluginUnit;
import jp.sbi.celldesigner.plugin.PluginUnitDefinition;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.AbstractMathContainer;
import org.sbml.jsbml.AbstractNamedSBase;
import org.sbml.jsbml.AbstractNamedSBaseWithUnit;
import org.sbml.jsbml.AbstractSBase;
import org.sbml.jsbml.AbstractTreeNode;
import org.sbml.jsbml.AlgebraicRule;
import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.AnnotationElement;
import org.sbml.jsbml.AssignmentRule;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.CompartmentType;
import org.sbml.jsbml.Constraint;
import org.sbml.jsbml.Creator;
import org.sbml.jsbml.Delay;
import org.sbml.jsbml.Event;
import org.sbml.jsbml.EventAssignment;
import org.sbml.jsbml.ExplicitRule;
import org.sbml.jsbml.FunctionDefinition;
import org.sbml.jsbml.History;
import org.sbml.jsbml.InitialAssignment;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.MathContainer;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.Priority;
import org.sbml.jsbml.QuantityWithUnit;
import org.sbml.jsbml.RateRule;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Rule;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBO;
import org.sbml.jsbml.SBase;
import org.sbml.jsbml.SimpleSpeciesReference;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.SpeciesType;
import org.sbml.jsbml.StoichiometryMath;
import org.sbml.jsbml.Symbol;
import org.sbml.jsbml.Trigger;
import org.sbml.jsbml.Unit;
import org.sbml.jsbml.UnitDefinition;
import org.sbml.jsbml.util.TreeNodeChangeEvent;
import org.sbml.jsbml.util.TreeNodeChangeListener;
import org.sbml.jsbml.xml.XMLToken;
import org.sbml.libsbml.ListOfCompartments;
import org.sbml.libsbml.XMLNode;
import org.sbml.libsbml.libsbml;
import org.sbml.libsbml.libsbmlConstants;

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
		//TODO Each of these values have to be crosschecked whether they are used or not
		//     If they are used, we have to parse them properly, else we can ignore them safely.
		/**
		 * Some of these TreeNodeChangeEvents are either not applicable for propertyChange or
		 * not used since other methods are doing the work, therefore they're only listed here for 
		 * documentation purposes:
		 * - about
		 * - addDeclaredNameSpace
		 * - addExtension (no class in Plugin*)
		 * - addNameSpace (no class in Plugin*)
		 * - addNameSpace (no class in Plugin*)
		 * - areaUnits (no class in Plugin*)
		 * - baseListType (unclear how to access List<Of>)
		 */
		
		if (prop.equals(TreeNodeChangeEvent.addCVTerm)) {
			CVTerm cv = (CVTerm) event.getSource();
			//TODO PluginCVTerm ?
		} else if (prop.equals(TreeNodeChangeEvent.addExtension)) {
		} else if (prop.equals(TreeNodeChangeEvent.addNamespace)) {
		} else if (prop.equals(TreeNodeChangeEvent.annotation)) {
		} else if (prop.equals(TreeNodeChangeEvent.annotationNameSpaces)) {
		} else if (prop.equals(TreeNodeChangeEvent.areaUnits)) {
		} else if (prop.equals(TreeNodeChangeEvent.baseListType)) {
		} else if (prop.equals(TreeNodeChangeEvent.boundaryCondition)) {
			Species species = (Species) event.getSource();
			PluginSpecies plugSpec = plugModel.getSpecies(species.getId());
			plugSpec.setBoundaryCondition(species.getBoundaryCondition());
			plugin.notifySBaseChanged(plugSpec);
		} else if (prop.equals(TreeNodeChangeEvent.charge)) {
			Species species = (Species) event.getSource();
			PluginSpecies plugSpec = plugModel.getSpecies(species.getId());
			plugSpec.setCharge(species.getCharge());
			plugin.notifySBaseChanged(plugSpec);
		} else if (prop.equals(TreeNodeChangeEvent.childNode)) {
			//only called in ASTNode swapChilden(this,that) method
			//TODO necessary to implement this ?
		} else if (prop.equals(TreeNodeChangeEvent.className)) {
			//ASTNode related
		} else if (prop.equals(TreeNodeChangeEvent.compartment)) {
			if (event.getSource() instanceof Reaction){
				Reaction r = (Reaction) event.getSource();
				PluginReaction plugR = plugModel.getReaction(r.getId());
				//there is no setCompartment() for PluginReaction - TODO 
			} else if (event.getSource() instanceof Species){
				Species spec = (Species) event.getSource();
				PluginSpecies plugSpecies = plugModel.getSpecies(spec.getId());
				plugSpecies.setCompartment(spec.getCompartment());
				plugin.notifySBaseChanged(plugSpecies);
			}
		} else if (prop.equals(TreeNodeChangeEvent.compartmentType)) {
			CompartmentType compType = (CompartmentType) event.getSource();
			PluginCompartmentType plugCompType = plugModel.getCompartmentType(compType.getId());
			//no setCompartmentType available for Compartment or CompartmentType
		} else if (prop.equals(TreeNodeChangeEvent.constant)) {
			//either symbol or speciesreference
			if (event.getSource() instanceof SpeciesReference){
				SpeciesReference specRef = (SpeciesReference) event.getSource();
				//there is no PluginSpeciesReference class 
			} else if (event.getSource() instanceof Symbol){
				Symbol sym = (Symbol) event.getSource();
				//there is no PluginSymbol class available
			}
		} else if (prop.equals(TreeNodeChangeEvent.conversionFactor)) {
			//either Model or Species
			if (event.getSource() instanceof Model){
				Model mod = (Model) event.getSource();
				//no function setConversionFactor...
			} else if (event.getSource() instanceof Species){
				Species spec = (Species) event.getSource();
				PluginSpecies plugSpec = plugModel.getSpecies(spec.getId());
				// no setConversionFactor method available
			}
		} else if (prop.equals(TreeNodeChangeEvent.created)) {
			//must be history object
			History hist = (History) event.getSource();
			//there is no pluginhistory
		} else if (prop.equals(TreeNodeChangeEvent.creator)) {
			//there is no history
		} else if (prop.equals(TreeNodeChangeEvent.currentList)) {
			//only avilable in propertyChange
		} else if (prop.equals(TreeNodeChangeEvent.definitionURL)) {
			//ASTNode
		} else if (prop.equals(TreeNodeChangeEvent.denominator)) {
			//ASTNode
		} else if (prop.equals(TreeNodeChangeEvent.email)) {
			//PluginCreator does not exist in CD
		} else if (prop.equals(TreeNodeChangeEvent.encoding)) {
			//ASTNode
		} else if (prop.equals(TreeNodeChangeEvent.exponent)) {
			//ASTNode or Unit
			if (event.getSource() instanceof ASTNode){
				
			} else if (event.getSource() instanceof Unit){
				Unit ut = (Unit) event.getSource();
				//how to get the appropriate element ?
				//TODO
			}
		} else if (prop.equals(TreeNodeChangeEvent.extentUnits)) {
			//PluginModel does not offer setExtentUnits or similar methods
		} else if (prop.equals(TreeNodeChangeEvent.familyName)) {
			//no PluginCreator
		} else if (prop.equals(TreeNodeChangeEvent.fast)) {
			Reaction r = (Reaction) event.getSource();
			PluginReaction plugR = plugModel.getReaction(r.getId());
			plugR.setFast(r.getFast());
			plugin.notifySBaseChanged(plugR);
		} else if (prop.equals(TreeNodeChangeEvent.formula)) {
			//nowhere only in PluginChangeListener
		} else if (prop.equals(TreeNodeChangeEvent.givenName)) {
			// no PluginCreator class avilable
		} else if (prop.equals(TreeNodeChangeEvent.hasOnlySubstanceUnits)) {
			Species spec = (Species) event.getSource();
			PluginSpecies plugSpec = plugModel.getSpecies(spec.getId());
			plugSpec.setHasOnlySubstanceUnits(spec.getHasOnlySubstanceUnits());
			plugin.notifySBaseChanged(plugSpec);
		} else if (prop.equals(TreeNodeChangeEvent.history)) {
			// no PluginHistory class available
		} else if (prop.equals(TreeNodeChangeEvent.id)) {
			//AbstractNamedSBase has this, as well as ASTNode --> Dont know how to parse this
		} else if (prop.equals(TreeNodeChangeEvent.initialAmount)) {
			Species spec = (Species) event.getSource();
			PluginSpecies plugSpec = plugModel.getSpecies(spec.getId());
			plugSpec.setInitialAmount(spec.getInitialAmount());
			plugSpec.setInitialConcentration(spec.getInitialConcentration());
			//TODO do i have to set both ?
			//Can i distinguish between initialAmount() and/or initialConcentration() ?
		} else if (prop.equals(TreeNodeChangeEvent.initialValue)) {
			//ASTNode or Trigger
			//no PluginTrigger available
		} else if (prop.equals(TreeNodeChangeEvent.isEOF)) {
			//not used at all
		} else if (prop.equals(TreeNodeChangeEvent.isExplicitlySetConstant)) {
			//not LocalParameter in Plugin*
		} else if (prop.equals(TreeNodeChangeEvent.isSetNumberType)) {
			//ASTnode
		} else if (prop.equals(TreeNodeChangeEvent.kind)) {
			//how to get PluginUnits () ? 
		} else if (prop.equals(TreeNodeChangeEvent.kineticLaw)) {
			//only used in this class
		} else if (prop.equals(TreeNodeChangeEvent.lengthUnits)) {
			//no setLengthUnits() or similar method in pluginModel
		} else if (prop.equals(TreeNodeChangeEvent.level)) {
			//how to set in AbstractSBase the level?
		} else if (prop.equals(TreeNodeChangeEvent.listOfUnits)) {
			//no setListOfUnits in plugModel available
		} else if (prop.equals(TreeNodeChangeEvent.mantissa)) {
			//ASTNode setMantissa() uses this - how to convert this ?
		} else if (prop.equals(TreeNodeChangeEvent.math)) {
			MathContainer mathContainer = (MathContainer) event.getSource();
			saveMathContainerProperties(mathContainer);
		} else if (prop.equals(TreeNodeChangeEvent.message)) {
			Constraint c = (Constraint) event.getSource();
			PluginConstraint plugC = plugModel.getConstraint(c.getMathMLString());
			//TODO how to setMessage in plugConstraint?
		} else if (prop.equals(TreeNodeChangeEvent.messageBuffer)) {
			//only used in this class
		} else if (prop.equals(TreeNodeChangeEvent.metaId)) {
			//AbstractSBase
		} else if (prop.equals(TreeNodeChangeEvent.model)) {
			//no SBMLDocument in Plugin*
		} else if (prop.equals(TreeNodeChangeEvent.modified)) {
			//no history in Plugin*
		} else if (prop.equals(TreeNodeChangeEvent.multiplier)) {
			// how to get Units in PluginModel?
		} else if (prop.equals(TreeNodeChangeEvent.name)) {
			if (event.getSource() instanceof FunctionDefinition){
				FunctionDefinition funcDef = (FunctionDefinition) event.getSource();
				PluginFunctionDefinition plugFuncDef = plugModel.getFunctionDefinition(funcDef.getId());
				plugFuncDef.setName(funcDef.getName());
				plugin.notifySBaseChanged(plugFuncDef);
			}
			//TODO ASTNode and AbstractNamedSBase are not used atm
		} else if (prop.equals(TreeNodeChangeEvent.namespace)) {
			//only in this class used
		} else if (prop.equals(TreeNodeChangeEvent.nonRDFAnnotation)) {
			//only used in Annotation, theres no PluginAnnotation
		} else if (prop.equals(TreeNodeChangeEvent.notes)) {
			//AbstractSBase - how to use in Plugin*?
		} else if (prop.equals(TreeNodeChangeEvent.notesBuffer)) {
			//only used in this class
		} else if (prop.equals(TreeNodeChangeEvent.numerator)) {
			//ASTNode
		} else if (prop.equals(TreeNodeChangeEvent.offset)) {
			//How to access PluginUnit
		} else if (prop.equals(TreeNodeChangeEvent.organisation)) {
			//no pluginCreator
		} else if (prop.equals(TreeNodeChangeEvent.outside)) {
			Compartment c = (Compartment) event.getSource();
			PluginCompartment plugC = plugModel.getCompartment(c.getId());
			plugC.setOutside(c.getOutside());
			plugin.notifySBaseChanged(plugC);
		} else if (prop.equals(TreeNodeChangeEvent.parentSBMLObject)) {
			//AbstractSBase
		} else if (prop.equals(TreeNodeChangeEvent.persistent)) {
			//no PlugiNTrigger (how to get ParentObject Event?) TODO
		} else if (prop.equals(TreeNodeChangeEvent.priority)) {
			Event evt = (Event) event.getSource();
			PluginEvent plugEvt = plugModel.getEvent(evt.getId());
			//no setpriority in PluginEvent class
		} else if (prop.equals(TreeNodeChangeEvent.qualifier)) {
			//CVterm - libsbml
		} else if (prop.equals(TreeNodeChangeEvent.rdfAnnotationNamespaces)) {
			//No pluginannotation
		} else if (prop.equals(TreeNodeChangeEvent.resource)) {
			//only used in this class
		} else if (prop.equals(TreeNodeChangeEvent.reversible)) {
			Reaction r = (Reaction) event.getSource();
			PluginReaction plugR = plugModel.getReaction(r.getId());
			plugR.setReversible(r.getReversible());
		} else if (prop.equals(TreeNodeChangeEvent.sboTerm)) {
			//AbstractSbase
		} else if (prop.equals(TreeNodeChangeEvent.scale)) {
			//how to get PluginUnit
		} else if (prop.equals(TreeNodeChangeEvent.setAnnotation)) {
			//abstract SBase
		} else if (prop.equals(TreeNodeChangeEvent.size)) {
			Compartment c = (Compartment) event.getSource();
			PluginCompartment plugC = plugModel.getCompartment(c.getId());
			plugC.setSize(c.getSize());
			plugin.notifySBaseChanged(plugC);
		} else if (prop.equals(TreeNodeChangeEvent.spatialDimensions)) {
			Compartment c = (Compartment) event.getSource();
			PluginCompartment plugC = plugModel.getCompartment(c.getId());
			plugC.setSpatialDimensions((long) c.getSpatialDimensions());
			plugin.notifySBaseChanged(plugC);
		} else if (prop.equals(TreeNodeChangeEvent.spatialSizeUnits)) {
			Species spec = (Species) event.getSource();
			PluginSpecies plugSpec = plugModel.getSpecies(spec.getId());
			plugSpec.setSpatialSizeUnits(spec.getSpatialSizeUnits());
			plugin.notifySBaseChanged(plugSpec);
		} else if (prop.equals(TreeNodeChangeEvent.species)) {
			//dont know how to getSimpleSpeciesReference in Plugin*
		} else if (prop.equals(TreeNodeChangeEvent.speciesType)) {
			Species spec = (Species) event.getSource();
			PluginSpecies plugSpec = plugModel.getSpecies(spec.getId());
			plugSpec.setSpeciesType(spec.getSpeciesType());
			plugin.notifySBaseChanged(plugSpec);
		} else if (prop.equals(TreeNodeChangeEvent.stoichiometry)) {
			//Species Reference undefined in Plugin*
		} else if (prop.equals(TreeNodeChangeEvent.style)) {
			//ASTNode
		} else if (prop.equals(TreeNodeChangeEvent.substanceUnits)) {
			KineticLaw klaw = (KineticLaw) event.getSource();
			PluginReaction plugReac = plugModel.getReaction(klaw.getParent().getId());
			plugReac.getKineticLaw().setSubstanceUnits(klaw.getSubstanceUnits());
			plugin.notifySBaseChanged(plugReac);
		} else if (prop.equals(TreeNodeChangeEvent.symbol)) {
			//only used in this class
		} else if (prop.equals(TreeNodeChangeEvent.SBMLDocumentAttributes)) {
			//only used in this class
		} else if (prop.equals(TreeNodeChangeEvent.text)) {
			//only used in this class
		} else if (prop.equals(TreeNodeChangeEvent.timeUnits)) {
			//here timeUnits can belong to Event, KineticLaw and Model (and as well several subfunctions...)
			//TODO what to do in such a case ?
		} else if (prop.equals(TreeNodeChangeEvent.type)) {
			//ASTNode or CVTerm (libsbml?)
		} else if (prop.equals(TreeNodeChangeEvent.units)) {
			//AbstractNamedSBaseWithUnit, ASTNode, ExplicitRule, KineticLaw and Model
			//see timeUnits. what to do in such a case ?
		} else if (prop.equals(TreeNodeChangeEvent.unsetCVTerms)) {
			//AStnode or Annotation (pluginAnnotation does not exist)
		} else if (prop.equals(TreeNodeChangeEvent.userObject)) {
			//ASTNode
		} else if (prop.equals(TreeNodeChangeEvent.useValuesFromTriggerTime)) {
			Event evt = (Event) event.getSource();
			PluginEvent plugEvt = plugModel.getEvent(evt.getId());
			plugEvt.setUseValuesFromTriggerTime(evt.getUseValuesFromTriggerTime());
			plugin.notifySBaseChanged(plugEvt);
		} else if (prop.equals(TreeNodeChangeEvent.value)) {
			//ASTNode or Quantitywithunit (there is no PluginQuantity or something like that)
			
		} else if (prop.equals(TreeNodeChangeEvent.variable)) {
			// Ambigous Classes -> Event Assignment, ExplicitRule and or Initial Assignment
		} else if (prop.equals(TreeNodeChangeEvent.version)) {
			//AbstractSBase
		} else if (prop.equals(TreeNodeChangeEvent.volume)) {
			Compartment c = (Compartment) event.getSource();
			PluginCompartment plugC = plugModel.getCompartment(c.getId());
			plugC.setVolume(c.getVolume());
			plugin.notifySBaseChanged(plugC);
		} else if (prop.equals(TreeNodeChangeEvent.volumeUnits)) {
			//No method setVolumeUnits in PluginModel found
		} else if (prop.equals(TreeNodeChangeEvent.xmlTriple)) {
			//only used in this class
		}  
	}

	/**
	 * This method saves the properties of a MathContainer input Object.
	 * 
	 * @param mathcontainer
	 */

	private void saveMathContainerProperties(MathContainer mathcontainer){
		if (mathcontainer instanceof FunctionDefinition){
			FunctionDefinition funcDef = (FunctionDefinition) mathcontainer;
			PluginFunctionDefinition plugFuncDef = plugModel.getFunctionDefinition(funcDef.getId());
			boolean equals = (plugFuncDef.getMath() != null) && funcDef.isSetMath() && PluginUtils.equal(funcDef.getMath(), plugFuncDef.getMath());
			if (funcDef.isSetMath() && !equals) {
				plugFuncDef.setMath(PluginUtils.convert(funcDef.getMath()));
			} else {
				logger.log(Level.DEBUG, String.format("Couldn't save math properties of %s", funcDef.getClass().getSimpleName()));
			}
		} else if (mathcontainer instanceof KineticLaw) {
			Reaction r = ((KineticLaw) mathcontainer).getParent();
			PluginReaction plugReac = plugModel.getReaction(r.getId());
			if (plugReac != null) {
				PluginKineticLaw plugKl = plugReac.getKineticLaw();
				plugKl.setMath(PluginUtils.convert(r.getKineticLaw().getMath()));
				plugin.notifySBaseChanged(plugKl);
			} else {
				logger.log(Level.DEBUG, String.format("Couldn't save math properties of %s", r.getClass().getSimpleName()));
			}
		} else if (mathcontainer instanceof InitialAssignment){
			InitialAssignment initAss = (InitialAssignment) mathcontainer;
			PluginInitialAssignment pluginit = plugModel.getInitialAssignment(initAss.getSymbol());
			boolean equals = (initAss.getMath() != null) && initAss.isSetMath() && PluginUtils.equal(initAss.getMath(), libsbml.parseFormula(pluginit.getMath()));
			if (initAss.isSetMath() && !equals){
				pluginit.setMath(libsbml.formulaToString(PluginUtils.convert(initAss.getMath())));
				plugin.notifySBaseChanged(pluginit);
			} else {
				logger.log(Level.DEBUG, String.format("Couldn't save math properties of %s", initAss.getClass().getSimpleName()));
			}
		} else if (mathcontainer instanceof EventAssignment){
			EventAssignment eventAss = (EventAssignment) mathcontainer;
			//TODO Unclear how to get the EventAssignment
		} else if (mathcontainer instanceof StoichiometryMath){
			//TODO Does not exist in Celldesigner or ?
		} else if (mathcontainer instanceof Trigger){
			Trigger trig = (Trigger) mathcontainer;
			PluginEvent plugEvent = plugModel.getEvent(trig.getParent().getId());
			boolean equals = plugEvent.getTrigger().isSetMath() && trig.isSetMath() && PluginUtils.equal(trig.getMath(), plugEvent.getTrigger().getMath());
			if (trig.isSetMath() && !equals){
				plugEvent.getTrigger().setMath(PluginUtils.convert(trig.getMath()));
				plugin.notifySBaseChanged(plugEvent);
			} else {
				logger.log(Level.DEBUG, String.format("Couldn't save math properties of %s", trig.getClass().getSimpleName()));
			}
		} else if (mathcontainer instanceof Rule){
			Rule r = (Rule) mathcontainer;
			if (mathcontainer instanceof AlgebraicRule){
				//TODO how to get the proper Algebraic Rule ?
				
			} else if (mathcontainer instanceof ExplicitRule){
				if (mathcontainer instanceof RateRule){
					//TODO how to get the Right Rate Rule
				}else if (mathcontainer instanceof AssignmentRule){
					//TODO
				}
			}
		} else if (mathcontainer instanceof Constraint) {
			Constraint c = (Constraint) mathcontainer;
			PluginConstraint plugC  = plugModel.getConstraint(c.getMathMLString());
			boolean equals = (plugC.getMath() != null) && c.isSetMath() && PluginUtils.equal(c.getMath(), libsbml.parseFormula(plugC.getMath()));
			if (c.isSetMath() && !equals){
				logger.log(Level.DEBUG, String.format("Couldn't save math properties of %s", c.getClass().getSimpleName()));
			}
		} else if (mathcontainer instanceof Delay){
			Delay d = (Delay) mathcontainer;
			PluginEvent plugEvent = plugModel.getEvent(d.getParent().getId());
			org.sbml.libsbml.Delay plugDelay = plugEvent.getDelay();
			boolean equals = (plugDelay.getMath() != null && d.isSetMath() && PluginUtils.equal(d.getMath(), plugDelay.getMath()));
			if (d.isSetMath() && !equals){
				plugDelay.setMath(PluginUtils.convert(d.getMath()));
				plugin.notifySBaseChanged(plugEvent);
			} else {
				logger.log(Level.DEBUG, String.format("Couldn't save math properties of %s", d.getClass().getSimpleName()));
			}
		} else if (mathcontainer instanceof Priority){
			Priority p = (Priority) mathcontainer;
			PluginEvent plugEvent = plugModel.getEvent(p.getParent().getId());
			org.sbml.libsbml.Delay plugPriority = plugEvent.getDelay();
			boolean equals = (plugPriority.getMath() != null && p.isSetMath() && PluginUtils.equal(p.getMath(), plugPriority.getMath()));
			if (p.isSetMath() && !equals){
				plugPriority.setMath(PluginUtils.convert(p.getMath()));
				plugin.notifySBaseChanged(plugEvent);
			} else {
				logger.log(Level.DEBUG, String.format("Couldn't save math properties of %s", p.getClass().getSimpleName()));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sbml.jsbml.util.TreeNodeChangeListener#nodeAdded(javax.swing.tree
	 * .TreeNode)
	 */
	public void nodeAdded(TreeNode node) {
		if (node instanceof AbstractSBase) {
			if (node instanceof AbstractNamedSBase) {
				if (node instanceof CompartmentType) {
					CompartmentType ct = (CompartmentType) node;
					PluginCompartmentType pt = new PluginCompartmentType(
							ct.getId());
					if (ct.isSetName() && !pt.getName().equals(ct.getName())) {
						pt.setName(ct.getName());
						plugin.notifySBaseAdded(pt);
					} else {
						logger.log(Level.DEBUG, "Cannot add node"
								+ node.getClass().getSimpleName());
					}
				} else if (node instanceof UnitDefinition) {
					UnitDefinition undef = (UnitDefinition) node;
					PluginUnitDefinition plugundef = new PluginUnitDefinition(
							undef.getId());
					if (undef.isSetName()
							&& !plugundef.getName().equals(undef.getName())) {
						plugundef.setName(undef.getName());
						plugin.notifySBaseAdded(plugundef);
					} else {
						logger.log(Level.DEBUG, "Cannot add node"
								+ node.getClass().getSimpleName());
					}
				} else if (node instanceof Reaction) {
					Reaction react = (Reaction) node;
					PluginReaction plugreac = new PluginReaction();
					if (react.isSetName()
							&& !react.getName().equals(plugreac.getName())) {
						plugreac.setName(react.getName());
						plugin.notifySBaseAdded(plugreac);
					} else {
						logger.log(Level.DEBUG, "Cannot add node"
								+ node.getClass().getSimpleName());
					}
				} else if (node instanceof SpeciesType) {
					SpeciesType speciestype = (SpeciesType) node;
					PluginSpeciesType plugspectype = new PluginSpeciesType(
							speciestype.getId());
					if (speciestype.isSetName()
							&& !speciestype.getName().equals(
									plugspectype.getName())) {
						plugspectype.setName(speciestype.getName());
						plugin.notifySBaseAdded(plugspectype);
					} else {
						logger.log(Level.DEBUG, "Cannot add node"
								+ node.getClass().getSimpleName());
					}
				} else if (node instanceof SimpleSpeciesReference) {
					SimpleSpeciesReference simspec = (SimpleSpeciesReference) node;
					String type = SBO.convertSBO2Alias(simspec.getSBOTerm());
					if (node instanceof ModifierSpeciesReference) {
						if (type.length() == 0) {
							// use "unknown"
							int sbo = 285;
							type = SBO.convertSBO2Alias(sbo);
							logger.log(Level.DEBUG, String.format(
									"No SBO term defined for %s, using %d",
									simspec.getElementName(), sbo));
						}
						if (simspec.isSetSpecies()) {
							PluginSpeciesAlias alias = new PluginSpeciesAlias(
									plugModel.getSpecies(simspec.getSpecies()),
									type);
							PluginModifierSpeciesReference plugModRef = new PluginModifierSpeciesReference(
									(PluginReaction) simspec.getParent(), alias);
							plugin.notifySBaseAdded(plugModRef);
						} else {
							logger.log(Level.DEBUG,
									"Cannot create PluginSpeciesReference due to missing species annotation.");
						}

					} else if (node instanceof SpeciesReference) {
						if (type.length() == 0) {
							// use "unknown"
							int sbo = 285;
							type = SBO.convertSBO2Alias(sbo);
							logger.log(Level.DEBUG, String.format(
									"No SBO term defined for %s, using %d",
									simspec.getElementName(), sbo));
						}
						// TODO: use SBML layout extension (later than JSBML
						// 0.8)
						if (simspec.isSetSpecies()) {
							PluginSpeciesAlias alias = new PluginSpeciesAlias(
									plugModel.getSpecies(simspec.getSpecies()),
									type);
							PluginSpeciesReference plugspecRef = new PluginSpeciesReference(
									(PluginReaction) simspec.getParent(), alias);
							plugin.notifySBaseAdded(plugspecRef);
						} else {
							logger.log(Level.DEBUG,
									"Cannot create PluginSpeciesReference due to missing species annotation.");
						}
					}
				} else if (node instanceof AbstractNamedSBaseWithUnit) {
					if (node instanceof Event) {
						Event event = (Event) node;
						PluginEvent plugevent = new PluginEvent(event.getId());
						if (event.isSetName()
								&& !event.getName().equals(plugevent.getName())) {
							plugevent.setName(event.getName());
							plugin.notifySBaseAdded(plugevent);
						} else {
							logger.log(Level.DEBUG, "Cannot add node"
									+ node.getClass().getSimpleName());
						}
					} else if (node instanceof QuantityWithUnit) {
						if (node instanceof LocalParameter) {
							LocalParameter locparam = (LocalParameter) node;
							ListOf<LocalParameter> lop = locparam
									.getParentSBMLObject();
							KineticLaw kl = (KineticLaw) lop
									.getParentSBMLObject();
							/*
							 * TODO Crosscheck if this is okay.
							 */
							for (LocalParameter p : kl
									.getListOfLocalParameters()) {
								if (p.isSetUnits()
										&& !Unit.isUnitKind(p.getUnits(),
												p.getLevel(), p.getVersion())
										&& plugModel.getUnitDefinition(p
												.getUnits()) == null) {
									PluginUnitDefinition plugUnitDefinition = new PluginUnitDefinition(
											p.getUnitsInstance().getId());
									plugModel
											.addUnitDefinition(plugUnitDefinition);
									plugin.notifySBaseAdded(plugUnitDefinition);

								}
							}

						} else if (node instanceof Symbol) {
							if (node instanceof Compartment) {
								Compartment comp = (Compartment) node;
								PluginCompartment plugcomp = new PluginCompartment(
										comp.getCompartmentType());
								if (comp.isSetName()
										&& !plugcomp.getName().equals(
												comp.getName())) {
									plugcomp.setName(comp.getName());
									plugin.notifySBaseAdded(plugcomp);
								} else {
									logger.log(Level.DEBUG, "Cannot add node"
											+ node.getClass().getSimpleName());
								}
							} else if (node instanceof Species) {
								Species sp = (Species) node;
								PluginSpecies plugsp = new PluginSpecies(
										sp.getSpeciesType(), sp.getName());
								if (sp.isSetName()
										&& !sp.getName().equals(
												plugsp.getName())) {
									plugin.notifySBaseAdded(plugsp);
								} else {
									logger.log(Level.DEBUG, "Cannot add node"
											+ node.getClass().getSimpleName());
								}
							} else if (node instanceof org.sbml.jsbml.Parameter) {
								org.sbml.jsbml.Parameter param = (org.sbml.jsbml.Parameter) node;
								if (param.getParent() instanceof KineticLaw) {
									PluginParameter plugparam = new PluginParameter(
											(PluginKineticLaw) param
													.getParent());
									if (param.isSetName()
											&& !param.getName().equals(
													plugparam.getName())) {
										plugparam.setName(param.getName());
										plugin.notifySBaseAdded(plugparam);
									} else {
										logger.log(
												Level.DEBUG,
												"Cannot add node"
														+ node.getClass()
																.getSimpleName());
									}
								} else if (param.getParent() instanceof Model) {
									PluginParameter plugparam = new PluginParameter(
											(PluginModel) param.getParent());
									if (param.isSetName()
											&& !param.getName().equals(
													plugparam.getName())) {
										plugparam.setName(param.getName());
										plugin.notifySBaseAdded(plugparam);
									} else {
										logger.log(
												Level.DEBUG,
												"Cannot add node"
														+ node.getClass()
																.getSimpleName());
									}
								}
							}
						}
					}
				}
			}
			if (node instanceof Unit) {
				/*
				 * TODO This needs to be crosschecked if thats the way it should
				 * work.
				 */
				Unit ut = (Unit) node;
				PluginUnitDefinition plugUnitDef = new PluginUnitDefinition(
						((UnitDefinition) ut.getParentSBMLObject()).getId());
				PluginUnit plugut = new PluginUnit(plugUnitDef);
				switch (ut.getKind()) {
				case AMPERE:
					plugut.setKind(libsbmlConstants.UNIT_KIND_AMPERE);
					break;
				case BECQUEREL:
					plugut.setKind(libsbmlConstants.UNIT_KIND_BECQUEREL);
					break;
				case CANDELA:
					plugut.setKind(libsbmlConstants.UNIT_KIND_CANDELA);
					break;
				case CELSIUS:
					plugut.setKind(libsbmlConstants.UNIT_KIND_CELSIUS);
					break;
				case COULOMB:
					plugut.setKind(libsbmlConstants.UNIT_KIND_COULOMB);
					break;
				case DIMENSIONLESS:
					plugut.setKind(libsbmlConstants.UNIT_KIND_DIMENSIONLESS);
					break;
				case FARAD:
					plugut.setKind(libsbmlConstants.UNIT_KIND_FARAD);
					break;
				case GRAM:
					plugut.setKind(libsbmlConstants.UNIT_KIND_GRAM);
					break;
				case GRAY:
					plugut.setKind(libsbmlConstants.UNIT_KIND_GRAY);
					break;
				case HENRY:
					plugut.setKind(libsbmlConstants.UNIT_KIND_HENRY);
					break;
				case HERTZ:
					plugut.setKind(libsbmlConstants.UNIT_KIND_HERTZ);
					break;
				case INVALID:
					plugut.setKind(libsbmlConstants.UNIT_KIND_INVALID);
					break;
				case ITEM:
					plugut.setKind(libsbmlConstants.UNIT_KIND_ITEM);
					break;
				case JOULE:
					plugut.setKind(libsbmlConstants.UNIT_KIND_JOULE);
					break;
				case KATAL:
					plugut.setKind(libsbmlConstants.UNIT_KIND_KATAL);
					break;
				case KELVIN:
					plugut.setKind(libsbmlConstants.UNIT_KIND_KELVIN);
					break;
				case KILOGRAM:
					plugut.setKind(libsbmlConstants.UNIT_KIND_KILOGRAM);
					break;
				case LITER:
					plugut.setKind(libsbmlConstants.UNIT_KIND_LITER);
					break;
				case LITRE:
					plugut.setKind(libsbmlConstants.UNIT_KIND_LITRE);
					break;
				case LUMEN:
					plugut.setKind(libsbmlConstants.UNIT_KIND_LUMEN);
					break;
				case LUX:
					plugut.setKind(libsbmlConstants.UNIT_KIND_LUX);
					break;
				case METER:
					plugut.setKind(libsbmlConstants.UNIT_KIND_METER);
					break;
				case METRE:
					plugut.setKind(libsbmlConstants.UNIT_KIND_METRE);
					break;
				case MOLE:
					plugut.setKind(libsbmlConstants.UNIT_KIND_MOLE);
					break;
				case NEWTON:
					plugut.setKind(libsbmlConstants.UNIT_KIND_NEWTON);
					break;
				case OHM:
					plugut.setKind(libsbmlConstants.UNIT_KIND_OHM);
					break;
				case PASCAL:
					plugut.setKind(libsbmlConstants.UNIT_KIND_PASCAL);
					break;
				case RADIAN:
					plugut.setKind(libsbmlConstants.UNIT_KIND_RADIAN);
					break;
				case SECOND:
					plugut.setKind(libsbmlConstants.UNIT_KIND_SECOND);
					break;
				case SIEMENS:
					plugut.setKind(libsbmlConstants.UNIT_KIND_SIEMENS);
					break;
				case SIEVERT:
					plugut.setKind(libsbmlConstants.UNIT_KIND_SIEVERT);
					break;
				case STERADIAN:
					plugut.setKind(libsbmlConstants.UNIT_KIND_STERADIAN);
					break;
				case TESLA:
					plugut.setKind(libsbmlConstants.UNIT_KIND_TESLA);
					break;
				case VOLT:
					plugut.setKind(libsbmlConstants.UNIT_KIND_VOLT);
					break;
				case WATT:
					plugut.setKind(libsbmlConstants.UNIT_KIND_WATT);
					break;
				case WEBER:
					plugut.setKind(libsbmlConstants.UNIT_KIND_WEBER);
					break;
				}
				plugut.setExponent((int) Math.round(ut.getExponent()));
				plugut.setMultiplier(ut.getMultiplier());
				plugut.setOffset(ut.getOffset());
				plugut.setScale(ut.getScale());
				plugin.notifySBaseAdded(plugut);

			} else if (node instanceof SBMLDocument) {
				SBMLDocument doc = (SBMLDocument) node;
				logger.log(Level.DEBUG, "No counter class in CellDesigner"
						+ node.getClass().getSimpleName());
				// TODO
			} else if (node instanceof ListOf<?>) {
				ListOf<?> listOf = (ListOf<?>) node;
				PluginListOf pluli = new PluginListOf();
				PluginReaction ro = (PluginReaction) listOf
						.getParentSBMLObject();

				switch (listOf.getSBaseListType()) {
				case listOfCompartments:
//					ListOfCompartments ll = new ListOfCompartments();
					break;
				case listOfCompartmentTypes:
					break;
				case listOfConstraints:
					break;
				case listOfEventAssignments:
					break;
				case listOfEvents:
					break;
				case listOfFunctionDefinitions:
					break;
				case listOfInitialAssignments:
					break;
				case listOfLocalParameters:
					break;
				case listOfModifiers:
					break;
				case listOfParameters:
					break;
				case listOfProducts:
					break;
				case listOfReactants:
					break;
				case listOfReactions:
					break;
				case listOfRules:
					break;
				case listOfSpecies:
					break;
				case listOfSpeciesTypes:
					break;
				case listOfUnitDefinitions:
					break;
				case listOfUnits:
					break;
				case other:
					// TODO for JSBML packages (later than 0.8).
				default:
					// unknown
					break;
				}

			} else if (node instanceof AbstractMathContainer) {
				if (node instanceof FunctionDefinition) {
					FunctionDefinition funcdef = (FunctionDefinition) node;
					PluginFunctionDefinition plugfuncdef = new PluginFunctionDefinition(
							funcdef.getId());
					if (funcdef.isSetName()
							&& !plugfuncdef.getName().equals(funcdef.getName())) {
						plugfuncdef.setName(funcdef.getName());
						plugin.notifySBaseAdded(plugfuncdef);
					} else {
						logger.log(Level.DEBUG, "Cannot add node "
								+ node.getClass().getSimpleName());
					}
				} else if (node instanceof KineticLaw) {
					KineticLaw klaw = (KineticLaw) node;
					Reaction parentreaction = klaw.getParentSBMLObject();
					PluginKineticLaw plugklaw = plugModel.getReaction(
							parentreaction.getId()).getKineticLaw();
					PluginReaction plugreac = plugModel
							.getReaction(parentreaction.getId());
					plugreac.setKineticLaw(plugklaw);
					plugin.notifySBaseAdded(plugreac);
				} else if (node instanceof InitialAssignment) {
					InitialAssignment iAssign = (InitialAssignment) node;
					PluginInitialAssignment plugiassign = new PluginInitialAssignment(
							iAssign.getSymbol());
					plugiassign.setMath(iAssign.getMathMLString());
					plugiassign.setNotes(iAssign.getNotesString());
					plugin.notifySBaseAdded(plugiassign);
				} else if (node instanceof EventAssignment) {
					EventAssignment eassign = (EventAssignment) node;
					// TODO PluginEventAssignemnt requires a new PluginEvent -
					// we do not know this event. What shall we do here ?
				} else if (node instanceof StoichiometryMath) {
					logger.log(Level.DEBUG, String.format(
							"No counter class for %s in CellDesigner.", node
									.getClass().getSimpleName()));
				} else if (node instanceof Trigger) {
					Trigger trig = (Trigger) node;
					PluginEvent plugEvent = new PluginEvent(trig.getParent()
							.getId());
					plugEvent.setTrigger(PluginUtils.convert(trig.getMath()));
					plugin.notifySBaseAdded(plugEvent);
				} else if (node instanceof Constraint) {
					Constraint ct = (Constraint) node;
					PluginConstraint plugct = new PluginConstraint(
							ct.getMathMLString());
					plugct.setMessage(ct.getMessageString());
					plugct.setNotes(ct.getNotesString());
					plugin.notifySBaseAdded(plugct);
				} else if (node instanceof Delay) {
					Delay dl = (Delay) node;
					PluginEvent plugEvent = new PluginEvent(dl.getParent()
							.getId());
					plugEvent.setDelay(PluginUtils.convert(dl.getMath()));
					plugin.notifySBaseAdded(plugEvent);
				} else if (node instanceof Priority) {
					logger.log(Level.DEBUG, String.format(
							"No counter class for %s in CellDesigner.", node
									.getClass().getSimpleName()));
				} else if (node instanceof Rule) {
					if (node instanceof AlgebraicRule) {
						AlgebraicRule alrule = (AlgebraicRule) node;
						PluginAlgebraicRule plugalrule = new PluginAlgebraicRule(
								plugModel);
						plugalrule
								.setMath(PluginUtils.convert(alrule.getMath()));
						plugin.notifySBaseAdded(plugalrule);
					} else if (node instanceof ExplicitRule) {
						if (node instanceof RateRule) {
							RateRule rule = (RateRule) node;
							PluginRateRule plugraterule = new PluginRateRule(
									plugModel);

							plugraterule.setMath(PluginUtils.convert(rule
									.getMath()));
							plugraterule.setVariable(rule.getVariable());
							plugraterule.setNotes(rule.getNotes().getName());
							plugin.notifySBaseAdded(plugraterule);

						} else if (node instanceof AssignmentRule) {
							AssignmentRule assignRule = (AssignmentRule) node;
							PluginAssignmentRule plugassignRule = new PluginAssignmentRule(
									plugModel);

							plugassignRule.setL1TypeCode(assignRule.getLevel());
							plugassignRule.setMath(PluginUtils
									.convert(assignRule.getMath()));
							plugassignRule
									.setVariable(assignRule.getVariable());
							plugassignRule
									.setNotes(assignRule.getNotesString());
							plugin.notifySBaseAdded(plugassignRule);
						}
					} else {
						Rule rule = (Rule) node;
						PluginRule plugrule = new PluginRule();
						plugrule.setMath(PluginUtils.convert(rule.getMath()));
						plugrule.setNotes(rule.getNotesString());
						plugin.notifySBaseAdded(plugrule);
					}
				}
			}
		} else if (node instanceof AbstractTreeNode) {
			if (node instanceof XMLToken) {
				if (node instanceof XMLNode) {
					logger.log(Level.DEBUG, String.format(
							"Parsing of node %s not successful.", node
									.getClass().getSimpleName()));
				}
			} else if (node instanceof ASTNode) {
				logger.log(Level.DEBUG, String.format(
						"Parsing of node %s not successful.", node.getClass()
								.getSimpleName()));
			} else if (node instanceof AnnotationElement) {
				if (node instanceof CVTerm) {

					// TODO This has to be done with the libsbml.CVTerm Class,
					// fix this.
				} else if (node instanceof History) {
					logger.log(Level.DEBUG, "No counter class in CellDesigner"
							+ node.getClass().getSimpleName());
				} else if (node instanceof Creator) {
					logger.log(Level.DEBUG, "No counter class in CellDesigner"
							+ node.getClass().getSimpleName());
				} else {
					logger.warn(String.format("Could not process %s.",
							node.toString()));
				}
			}
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
		if (node instanceof AbstractSBase) {
			if (node instanceof AbstractNamedSBase) {
				if (node instanceof CompartmentType) {
					CompartmentType ct = (CompartmentType) node;
					PluginCompartmentType pt = plugModel.getCompartmentType(ct
							.getId());
					plugModel.removeCompartmentType(ct.getId());
					plugin.notifySBaseDeleted(pt);
				} else if (node instanceof UnitDefinition) {
					UnitDefinition undef = (UnitDefinition) node;
					PluginUnitDefinition plugUndef = plugModel
							.getUnitDefinition(undef.getId());
					plugModel.removeUnitDefinition(undef.getId());
					plugin.notifySBaseDeleted(plugUndef);
				} else if (node instanceof Reaction) {
					Reaction react = (Reaction) node;
					PluginReaction preac = plugModel.getReaction(react.getId());
					plugModel.removeReaction(react.getId());
					plugin.notifySBaseDeleted(preac);
				} else if (node instanceof SpeciesType) {
					SpeciesType speciestype = (SpeciesType) node;
					PluginSpeciesType pspec = plugModel
							.getSpeciesType(speciestype.getId());
					plugModel.removeSpeciesType(pspec);
					plugin.notifySBaseDeleted(pspec);
				} else if (node instanceof SimpleSpeciesReference) {
					SimpleSpeciesReference simspec = (SimpleSpeciesReference) node;
					String type = SBO.convertSBO2Alias(simspec.getSBOTerm());
					if (node instanceof ModifierSpeciesReference) {
						ModifierSpeciesReference modSpecRef = (ModifierSpeciesReference) node;
						PluginSpeciesAlias alias = plugModel.getSpecies(
								modSpecRef.getId()).getSpeciesAlias(type);
						PluginModifierSpeciesReference ref = new PluginModifierSpeciesReference(
								plugModel.getReaction(simspec.getId()), alias);
						plugModel.getReaction(simspec.getId()).removeModifier(
								ref);
						plugin.notifySBaseDeleted(ref);
					} else if (node instanceof SpeciesReference) {
						SpeciesReference specref = (SpeciesReference) node;
						PluginSpeciesAlias alias = plugModel.getSpecies(
								specref.getId()).getSpeciesAlias(type);
						PluginSpeciesReference ref = new PluginSpeciesReference(
								plugModel.getReaction(simspec.getId()), alias);
						plugModel.getReaction(simspec.getId()).removeProduct(
								ref);
						plugin.notifySBaseDeleted(ref);
					}
				} else if (node instanceof AbstractNamedSBaseWithUnit) {
					if (node instanceof Event) {
						Event event = (Event) node;
						PluginEvent plugEvent = plugModel.getEvent(event
								.getId());
						plugModel.removeEvent(event.getId());
						plugin.notifySBaseDeleted(plugEvent);
					} else if (node instanceof QuantityWithUnit) {
						if (node instanceof LocalParameter) {
							/*
							 * TODO: What to do with Localparameters? There are
							 * no LocalParameters in CD available.
							 */
						} else if (node instanceof Symbol) {
							if (node instanceof Compartment) {
								Compartment comp = (Compartment) node;
								PluginCompartment plugComp = plugModel
										.getCompartment(comp.getId());
								plugModel.removeCompartment(comp.getId());
								plugin.notifySBaseDeleted(plugComp);
							} else if (node instanceof Species) {
								Species sp = (Species) node;
								PluginSpecies ps = plugModel.getSpecies(sp
										.getId());
								plugModel.removeSpecies(sp.getId());
								plugin.notifySBaseDeleted(ps);
							} else if (node instanceof org.sbml.jsbml.Parameter) {
								org.sbml.jsbml.Parameter param = (org.sbml.jsbml.Parameter) node;
								PluginParameter plugParam = plugModel
										.getParameter(param.getId());
								plugModel.removeParameter(param.getId());
								plugin.notifySBaseDeleted(plugParam);
							}
						}
					}
				}
			}
			if (node instanceof Unit) {
				Unit ut = (Unit) node;
				// TODO
			} else if (node instanceof SBMLDocument) {
				SBMLDocument doc = (SBMLDocument) node;
				// TODO This needs to be hashed somehow.
			} else if (node instanceof ListOf<?>) {
				ListOf<?> listOf = (ListOf<?>) node;
				switch (listOf.getSBaseListType()) {
				case listOfCompartments:
//					ListOfCompartments ll = new ListOfCompartments();

					break;
				case listOfCompartmentTypes:
					break;
				case listOfConstraints:
					break;
				case listOfEventAssignments:
					break;
				case listOfEvents:
					break;
				case listOfFunctionDefinitions:
					break;
				case listOfInitialAssignments:
					break;
				case listOfLocalParameters:
					break;
				case listOfModifiers:
					break;
				case listOfParameters:
					break;
				case listOfProducts:
					break;
				case listOfReactants:
					break;
				case listOfReactions:
					break;
				case listOfRules:
					break;
				case listOfSpecies:
					break;
				case listOfSpeciesTypes:
					break;
				case listOfUnitDefinitions:
					break;
				case listOfUnits:
					break;
				case other:
					// TODO for JSBML packages (later than 0.8).
				default:
					// unknown
					break;
				}

			} else if (node instanceof AbstractMathContainer) {
				if (node instanceof FunctionDefinition) {
					FunctionDefinition funcdef = (FunctionDefinition) node;
					PluginFunctionDefinition plugFuncdef = plugModel
							.getFunctionDefinition(funcdef.getId());
					plugModel.removeFunctionDefinition(funcdef.getId());
					plugin.notifySBaseDeleted(plugFuncdef);
				} else if (node instanceof KineticLaw) {
					KineticLaw klaw = (KineticLaw) node;
					Reaction parentreaction = klaw.getParentSBMLObject();
					PluginReaction plugReac = plugModel
							.getReaction(parentreaction.getId());
					PluginKineticLaw plugklaw = plugReac.getKineticLaw();
					plugReac.setKineticLaw(null);
					plugin.notifySBaseDeleted(plugklaw);
				} else if (node instanceof InitialAssignment) {
					InitialAssignment iAssign = (InitialAssignment) node;
					PluginInitialAssignment plugiAssign = plugModel
							.getInitialAssignment(iAssign.getSymbol());
					plugModel.removeInitialAssignment(plugiAssign);
					plugin.notifySBaseDeleted(plugiAssign);
				} else if (node instanceof EventAssignment) {
					EventAssignment eAssign = (EventAssignment) node;
					ListOf<EventAssignment> elist = eAssign.getParent();
					Event e = (Event) elist.getParentSBMLObject();
					PluginEventAssignment plugEventAssignment = plugModel
							.getEvent(e.getId()).getEventAssignment(
									eAssign.getIndex(node));
					plugin.notifySBaseDeleted(plugEventAssignment);
				} else if (node instanceof StoichiometryMath) {
					logger.log(Level.DEBUG, String.format(
							"No counter class for %s in CellDesigner.", node
									.getClass().getSimpleName()));
				} else if (node instanceof Trigger) {
					Trigger trig = (Trigger) node;
					PluginEvent plugEvent = plugModel.getEvent(trig.getParent()
							.getId());
					logger.log(
							Level.DEBUG,
							String.format(
									"Trying to remove trigger from event %s, but there is no remove method. Please check the result.",
									plugEvent.getId()));
					plugEvent.setTrigger((org.sbml.libsbml.Trigger) null);
					plugin.notifySBaseChanged(plugEvent);
				} else if (node instanceof Constraint) {
					Constraint ct = (Constraint) node;
					PluginConstraint plugct = plugModel.getConstraint(ct
							.getMathMLString());
					plugModel.removeConstraint(ct.getMathMLString());
					plugin.notifySBaseDeleted(plugct);
				} else if (node instanceof Delay) {
					Delay dl = (Delay) node;
					PluginEvent plugEvent = plugModel.getEvent(dl.getParent()
							.getId());
					Delay dlnew = new Delay();
					plugEvent.setDelay(PluginUtils.convert(dlnew.getMath()));
					plugin.notifySBaseChanged(plugEvent);
				} else if (node instanceof Priority) {
					logger.log(Level.DEBUG, String.format(
							"No counter class for %s in CellDesigner.", node
									.getClass().getSimpleName()));
				} else if (node instanceof Rule) {
					if (node instanceof AlgebraicRule) {
						AlgebraicRule alrule = (AlgebraicRule) node;
						// TODO how to get the right algebraic rule ?
					} else if (node instanceof ExplicitRule) {
						if (node instanceof RateRule) {
							RateRule rrule = (RateRule) node;
							// TODO howto get the right Rate Rule ?
						} else if (node instanceof AssignmentRule) {
							AssignmentRule assignRule = (AssignmentRule) node;
							// TODO howto get the right AssignmentRule?
						}
					} else {
						// TODO case when we only have a "Rule" without anything
						// else
					}
				}
			}
		} else if (node instanceof AbstractTreeNode) {
			if (node instanceof XMLToken) {
				if (node instanceof XMLNode) {
					logger.log(Level.DEBUG, String.format(
							"Parsing of node %s not successful.", node
									.getClass().getSimpleName()));
				}
			} else if (node instanceof ASTNode) {
				logger.log(Level.DEBUG, String.format(
						"Parsing of node %s not successful.", node.getClass()
								.getSimpleName()));
			} else if (node instanceof AnnotationElement) {
				if (node instanceof CVTerm) {
					CVTerm term = (CVTerm) node;
					// TODO Here I dont know how to get the right CVTerm
				} else if (node instanceof History) {
					logger.log(Level.DEBUG, "No counter class in CellDesigner"
							+ node.getClass().getSimpleName());
				} else if (node instanceof Creator) {
					logger.log(Level.DEBUG, "No counter class in CellDesigner"
							+ node.getClass().getSimpleName());
				}
			}
		}
	}

}
