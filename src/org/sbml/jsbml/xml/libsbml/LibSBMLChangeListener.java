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

import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.AbstractMathContainer;
import org.sbml.jsbml.AbstractNamedSBase;
import org.sbml.jsbml.AbstractNamedSBaseWithUnit;
import org.sbml.jsbml.AbstractSBase;
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
import org.sbml.jsbml.FunctionDefinition;
import org.sbml.jsbml.History;
import org.sbml.jsbml.InitialAssignment;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.MathContainer;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.NamedSBase;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.Priority;
import org.sbml.jsbml.QuantityWithUnit;
import org.sbml.jsbml.RateRule;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Rule;
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
import org.sbml.jsbml.util.TreeNodeAdapter;
import org.sbml.jsbml.util.TreeNodeChangeEvent;
import org.sbml.jsbml.util.TreeNodeChangeListener;
import org.sbml.jsbml.xml.XMLToken;
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


	/* 
	 * (non-Javadoc)
	 * @see org.sbml.jsbml.util.TreeNodeChangeListener#nodeAdded(javax.swing.tree.TreeNode)
	 */
	public void nodeAdded(TreeNode node) {
		if (node instanceof AbstractSBase){
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
			if (sbase.isSetNotes()){
				libSbase.setNotes(sbase.getNotesString());
			}

			if (sbase instanceof org.sbml.jsbml.AbstractNamedSBase){
				//asking if name and id are set, because every named sbase has these attributes
				if (((org.sbml.jsbml.NamedSBase) sbase).isSetName()){
					libSbase.setName(((org.sbml.jsbml.NamedSBase) sbase).getName());
				}
				if (((org.sbml.jsbml.NamedSBase) sbase).isSetId()){
					libSbase.setId(((org.sbml.jsbml.NamedSBase) sbase).getId());
				}

				if (sbase instanceof CompartmentType) {
					CompartmentType ct = (CompartmentType) sbase;
					org.sbml.libsbml.CompartmentType libCt = (org.sbml.libsbml.CompartmentType) libSbase;
					if (ct.isSetNotes()){
						libCt.setNotes(ct.getNotesString());
					}
				} else if (sbase instanceof UnitDefinition){
					UnitDefinition udef = (UnitDefinition) sbase;
					org.sbml.libsbml.UnitDefinition libUdef = (org.sbml.libsbml.UnitDefinition) libSbase;
					if (udef.isSetListOfUnits()){
						for (org.sbml.jsbml.Unit u : udef.getListOfUnits()){
							//make a recursion for every unit u in the list of units
						}
					}
				} else if (sbase instanceof Model){
					Model model = (Model) sbase;
					org.sbml.libsbml.Model libModel = (org.sbml.libsbml.Model) libSbase;
					if (model.isSetAreaUnits()){
						libModel.setAreaUnits(model.getAreaUnits());
					}
					if (model.isSetConversionFactor()){
						libModel.setConversionFactor(model.getConversionFactor());
					}
					if (model.isSetExtentUnits()){
						libModel.setExtentUnits(model.getExtentUnits());
					}
					if (model.isSetLengthUnits()){
						libModel.setLengthUnits(model.getLengthUnits());
					}
					if (model.isSetListOfCompartments()){
						for (Compartment comp : model.getListOfCompartments()){
							//make recursion 
						}
					}
					if (model.isSetListOfCompartmentTypes()){
						for (CompartmentType comptype : model.getListOfCompartmentTypes()){
							//make recursion
						}
					}
					if (model.isSetListOfConstraints()){
						for (Constraint constr : model.getListOfConstraints()){
							//make recursion
						}
					}
					if (model.isSetListOfEvents()){
						for (Event event : model.getListOfEvents()){
							//make recursion
						}
					}
					if (model.isSetListOfFunctionDefinitions()){
						for (FunctionDefinition funcdef : model.getListOfFunctionDefinitions()){
							//make recursion
						}
					}
					if (model.isSetListOfInitialAssignments());
					if (model.isSetListOfParameters());
					if (model.isSetListOfReactions());
					if (model.isSetListOfRules());
					if (model.isSetListOfSpecies());
					if (model.isSetListOfSpeciesTypes());
					if (model.isSetListOfUnitDefinitions());
					if (model.isSetSubstanceUnits()){
						libModel.setSubstanceUnits(model.getSubstanceUnits());
					}
					if (model.isSetTimeUnits()){
						libModel.setTimeUnits(model.getTimeUnits());
					}
					if (model.isSetVolumeUnits()){
						libModel.setVolumeUnits(model.getVolumeUnits());
					}
				} else if (sbase instanceof Reaction){
					Reaction reac = (Reaction) sbase;
					org.sbml.libsbml.Reaction libReac = (org.sbml.libsbml.Reaction) libSbase;
					if (reac.isSetCompartment()){
						libReac.setCompartment(reac.getCompartment());
					}
					if (reac.isSetFast()){
						libReac.setFast(reac.getFast());
					}
					if (reac.isSetKineticLaw()){
						//libReac.setKineticLaw(reac.getKineticLaw());
					}
					if (reac.isSetListOfModifiers()){
						for (ModifierSpeciesReference modi : reac.getListOfModifiers()){
							//make recursion
						}
					}
					if (reac.isSetListOfProducts()){
						for (SpeciesReference product : reac.getListOfProducts()){
							//make recursion
						}
					}
					if (reac.isSetListOfReactants()){
						for (SpeciesReference reactant : reac.getListOfReactants()){
							//make recursion
						}
					}
					if (reac.isSetReversible()){
						libReac.setReversible(reac.getReversible());
					}
				} else if (sbase instanceof SimpleSpeciesReference){
					SimpleSpeciesReference siSpecRef = (SimpleSpeciesReference) sbase;
					org.sbml.libsbml.SimpleSpeciesReference libSiSpecRef = (org.sbml.libsbml.SimpleSpeciesReference) libSbase;
					if (siSpecRef.isSetSpecies()){
						libSiSpecRef.setSpecies(siSpecRef.getSpecies());
					}
					if (sbase instanceof SpeciesReference){
						SpeciesReference specRef = (SpeciesReference) sbase;
						org.sbml.libsbml.SpeciesReference libSpecRef = (org.sbml.libsbml.SpeciesReference) libSbase;
						if (specRef.isSetConstant()){
							libSpecRef.setConstant(specRef.getConstant());
						}	
						if (specRef.isSetStoichiometry()){
							libSpecRef.setStoichiometry(specRef.getStoichiometry());
						}
					}
				} else if (sbase instanceof AbstractNamedSBaseWithUnit){
					if (sbase instanceof Event){

					}
					else if (sbase instanceof QuantityWithUnit){
						if (sbase instanceof LocalParameter);

						else if (sbase instanceof Symbol){
							if (sbase instanceof Compartment){
								Compartment c = (Compartment) sbase;
								org.sbml.libsbml.Compartment libC = (org.sbml.libsbml.Compartment) libSbase;
								if (c.isSetCompartmentType()){
									libC.setCompartmentType(c.getCompartmentType());
								}
								if (c.isSetConstant()){
									libC.setConstant(c.getConstant());
								}
								if (c.isSetOutside()){
									libC.setOutside(c.getOutside());
								}
								if (c.isSetSize()){
									libC.setSize(c.getSize());
								}
								if (c.isSetSpatialDimensions()){
									libC.setSpatialDimensions(c.getSpatialDimensions());
								}
								if (c.isSetUnits()){
									libC.setUnits(c.getUnits());
								}
								if (c.isSetVolume()){
									libC.setVolume(c.getVolume());
								}
							} else if (sbase instanceof Species){
								Species spec = (Species) sbase;
								org.sbml.libsbml.Species libS = (org.sbml.libsbml.Species) libSbase;
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
								if (spec.isSetInitialAmount()){
									libS.setInitialAmount(spec.getInitialAmount());
								}
								if (spec.isSetInitialConcentration()){
									libS.setInitialConcentration(spec.getInitialConcentration());
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
							} else if (sbase instanceof Parameter){
								Parameter param = (Parameter) sbase;
								org.sbml.libsbml.Parameter libParam = (org.sbml.libsbml.Parameter) libSbase;
								if (param.isSetConstant()){
									libParam.setConstant(param.getConstant());
								}
								if (param.isSetUnits()){
									libParam.setUnits(param.getUnits());
								}
								if (param.isSetValue()){
									libParam.setValue(param.getValue());
								}
							} 
						}
					}
				}
			} else if (node instanceof Unit){
				Unit unit = (Unit) sbase;
				org.sbml.libsbml.Unit libUnit = (org.sbml.libsbml.Unit) libSbase;
				if (unit.isSetExponent()){
					libUnit.setExponent(unit.getExponent());
				}
				if (unit.isSetMultiplier()){
					libUnit.setMultiplier(unit.getMultiplier());
				}
				if (unit.isSetOffset()){
					libUnit.setOffset(unit.getOffset());
				}
				if (unit.isSetScale()){
					libUnit.setScale(unit.getScale());
				}
				if (unit.isSetKind()){
					//libUnit.setKind(unit.getKind());
				}

			} else if (node instanceof SBMLDocument){
				
			} else if (node instanceof ListOf<?>){
				
			} else if (node instanceof AbstractMathContainer){
				if (sbase instanceof FunctionDefinition);
				else if (sbase instanceof KineticLaw);
				else if (sbase instanceof InitialAssignment);
				else if (sbase instanceof EventAssignment);
				else if (sbase instanceof StoichiometryMath);
				else if (sbase instanceof Trigger);
				else if (sbase instanceof Rule){
					if (sbase instanceof AlgebraicRule);
					else{
						if (sbase instanceof RateRule);
						else if (sbase instanceof AssignmentRule);
					}
				}
				else if (sbase instanceof Constraint);
				else if (sbase instanceof Delay);
				else if (sbase instanceof Priority);
			}
		} 
		else if (node instanceof AnnotationElement){
			if (node instanceof CVTerm);
			else if (node instanceof History);
			else if (node instanceof Annotation);
			else if (node instanceof Creator);
		}
		else if (node instanceof ASTNode){
			
		}
		else if (node instanceof TreeNodeAdapter){
			
		}
		else if (node instanceof XMLToken){
			
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
