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
package org.sbml.jsbml.xml.libsbml;

import java.beans.PropertyChangeEvent;

import javax.swing.tree.TreeNode;

import org.sbml.jsbml.AbstractNamedSBase;
import org.sbml.jsbml.AbstractSBase;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.CompartmentType;
import org.sbml.jsbml.Constraint;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.MathContainer;
import org.sbml.jsbml.NamedSBase;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBase;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.UnitDefinition;
import org.sbml.jsbml.util.TreeNodeChangeEvent;
import org.sbml.jsbml.util.TreeNodeChangeListener;
import org.sbml.libsbml.SBMLDocument;

/**
 * @author Andreas Dr&auml;ger
 * @version $Rev$
 * @since 0.8
 */
@SuppressWarnings("deprecation")
public class LibSBMLChangeListener implements TreeNodeChangeListener {

	/**
	 * 
	 */
	private SBMLDocument libDoc;

	/**
	 * 
	 * @param doc
	 * @param libDoc
	 */
	public LibSBMLChangeListener(org.sbml.jsbml.SBMLDocument doc,
			org.sbml.libsbml.SBMLDocument libDoc) {
		this.libDoc = libDoc;
		// TODO: hash all elements without id. JSBML --> libSBML
		// all kinds of Rules, initialAssignment, stoichiometryMath (but this is inside of reactions), constraint
	}


	/* (non-Javadoc)
	 * @see org.sbml.jsbml.util.TreeNodeChangeListener#nodeAdded(javax.swing.tree.TreeNode)
	 */
	public void nodeAdded(TreeNode node) {
		if (node instanceof SBase){
			org.sbml.jsbml.AbstractSBase sbase = (AbstractSBase) node;
			org.sbml.libsbml.SBase libSbase = (org.sbml.libsbml.SBase) new Object();
			if (sbase.isSetAnnotation()){
				libSbase.setAnnotation(sbase.getAnnotationString());
			}
			if (sbase.isSetMetaId()){
				libSbase.setMetaId(sbase.getMetaId());
			}
			if (sbase.isSetSBOTerm()){
				libSbase.setSBOTerm(sbase.getSBOTerm());
			}
			if (sbase instanceof org.sbml.jsbml.NamedSBase) {
				if (((org.sbml.jsbml.NamedSBase) sbase).isSetName()){
					libSbase.setName(((org.sbml.jsbml.NamedSBase) sbase).getName());
				}
				if (sbase instanceof CompartmentType) {
					CompartmentType ct = (CompartmentType) sbase;
					org.sbml.libsbml.CompartmentType libCt = (org.sbml.libsbml.CompartmentType) libSbase;
					if (ct.isSetId()) {
						libCt.setId(ct.getId());
					}
					if (ct.isSetName()) {
						libCt.setName(ct.getName());
					}
					if (ct.isSetSBOTerm()){
						libCt.setSBOTerm(ct.getSBOTerm());
					}
					if (ct.isSetAnnotation()){
						libCt.setAnnotation(ct.getAnnotationString());
					}
					if (ct.isSetMetaId()){
						libCt.setMetaId(ct.getMetaId());
					}
					if (ct.isSetNotes()){
						libCt.setNotes(ct.getNotesString());
					}
				} else if (node instanceof Compartment){
					Compartment c = (Compartment) node;
					org.sbml.libsbml.Compartment libC = libDoc.getModel().createCompartment();
					if (c.isSetCompartmentType()){
						libC.setCompartmentType(c.getCompartmentType());
					}
					if (c.isSetConstant()){
						libC.setConstant(c.getConstant());
					}
					if (c.isSetId()){
						libC.setId(c.getId());
					}
					if (c.isSetName()){
						libC.setName(c.getName());
					}
					if (c.isSetOutside()){
						libC.setOutside(c.getOutside());
					}
					if (c.isSetSize()){
						libC.setSize(c.getSize());
					}
					if (c.isSetAnnotation()){
						libC.setAnnotation(c.getAnnotationString());
					}
					if (c.isSetSpatialDimensions()){
						libC.setSpatialDimensions(c.getSpatialDimensions());
					}
					if (c.isSetUnits()){
						libC.setUnits(c.getUnits());
					}
					if (c.isSetSBOTerm()){
						libC.setSBOTerm(c.getSBOTerm());
					}
					if (c.isSetVolume()){
						libC.setVolume(c.getVolume());
					}
					if (c.isSetMetaId()){
						libC.setMetaId(c.getMetaId());
					}
				} else if (node instanceof Species){
					Species spec = (Species) node;
					org.sbml.libsbml.Species libS = libDoc.getModel().createSpecies();
					if (spec.isSetName()){
						libS.setName(spec.getName());
					}
					if (spec.isSetAnnotation()){
						libS.setAnnotation(spec.getAnnotationString());
					}
					if (spec.isSetBoundaryCondition()){
						libS.setBoundaryCondition(spec.getBoundaryCondition());
					}
					if (spec.isSetCharge()){
						libS.setCharge(spec.getCharge());
					}
					if (spec.isSetCompartment()){
						libS.setCompartment(spec.getCompartment());
					}
					if (spec.isSetConstant()){
						libS.setConstant(spec.getConstant());
					}
					if (spec.isSetConversionFactor()){
						libS.setConversionFactor(spec.getConversionFactor());
					}
					if (spec.isSetHasOnlySubstanceUnits()){
						libS.setHasOnlySubstanceUnits(spec.getHasOnlySubstanceUnits());
					}
					if (spec.isSetId()){
						libS.setId(spec.getId());
					}
					if (spec.isSetInitialAmount()){
						libS.setInitialAmount(spec.getInitialAmount());
					}
					if (spec.isSetInitialConcentration()){
						libS.setInitialConcentration(spec.getInitialConcentration());
					}
					if (spec.isSetMetaId()){
						libS.setMetaId(spec.getMetaId());
					}
					if (spec.isSetSBOTerm()){
						libS.setSBOTerm(spec.getSBOTerm());
					}
					if (spec.isSetSpatialSizeUnits()){
						libS.setSpatialSizeUnits(spec.getSpatialSizeUnits());
					}
					if (spec.isSetSpeciesType()){
						libS.setSpeciesType(spec.getSpeciesType());
					}
					if (spec.isSetSubstanceUnits()){
						libS.setSubstanceUnits(spec.getSubstanceUnits());
					}
					if (spec.isSetUnits()){
						libS.setUnits(spec.getUnits());
					}
				} else if (node instanceof Parameter){
					Parameter param = (Parameter) node;
					org.sbml.libsbml.Parameter libParam = libDoc.getModel().createParameter();
					if (param.isSetAnnotation()){
						libParam.setAnnotation(param.getAnnotationString());
					}
					if (param.isSetConstant()){
						libParam.setConstant(param.getConstant());
					}
					if (param.isSetId()){
						libParam.setId(param.getId());
					}
					if (param.isSetMetaId()){
						libParam.setMetaId(param.getMetaId());
					}
					if (param.isSetName()){
						libParam.setName(param.getName());
					}
					if (param.isSetSBOTerm()){
						libParam.setSBOTerm(param.getSBOTerm());
					}
					if (param.isSetUnits()){
						libParam.setUnits(param.getUnits());
					}
					if (param.isSetValue()){
						libParam.setValue(param.getValue());
					}
				} else if (node instanceof UnitDefinition){
					UnitDefinition udef = (UnitDefinition) node;
					org.sbml.libsbml.UnitDefinition libUdef = libDoc.getModel().createUnitDefinition();
					if (udef.isSetAnnotation()){
						libUdef.setAnnotation(udef.getAnnotationString());
					}
					if (udef.isSetMetaId()){
						libUdef.setMetaId(udef.getMetaId());
					}
					if (udef.isSetName()){
						libUdef.setName(udef.getName());
					}
					if (udef.isSetSBOTerm()){

					}
				}
			}

		} 
	}

	/* (non-Javadoc)
	 * @see org.sbml.jsbml.util.TreeNodeChangeListener#nodeRemoved(javax.swing.tree.TreeNode)
	 */
	public void nodeRemoved(TreeNode node) {
		org.sbml.libsbml.Model libModel = libDoc.getModel();
		if (node instanceof CompartmentType) {
			CompartmentType ct = (CompartmentType) node;
			//			org.sbml.libsbml.CompartmentType pt = libModel
			//					.getCompartmentType(ct.getId());
			libModel.removeCompartmentType(ct.getId());
		}
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		Object evtSrc = evt.getSource();
		String prop = evt.getPropertyName();

		// ...
		if (prop.equals(TreeNodeChangeEvent.charge)) {
			Species spec = (Species) evtSrc;
			org.sbml.libsbml.Model libMod = libDoc.getModel();
			libMod.getSpecies(spec.getId()).setCharge(spec.getCharge());
		} else if (prop.equals(TreeNodeChangeEvent.math)) {
			MathContainer mathContainer = (MathContainer) evt.getSource();
			if (mathContainer instanceof KineticLaw) {
				Reaction r = ((KineticLaw) mathContainer).getParent();
				org.sbml.libsbml.KineticLaw libKl = libDoc.getModel().getReaction(r.getId()).getKineticLaw();
				libKl.setFormula(mathContainer.getFormula());
			} else if (mathContainer instanceof Constraint) {

			}
		}

		// TODO Auto-generated method stub

	}

}
