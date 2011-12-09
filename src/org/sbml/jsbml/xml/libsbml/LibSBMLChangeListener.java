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

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.AbstractMathContainer;
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
import org.sbml.jsbml.ExplicitRule;
import org.sbml.jsbml.FunctionDefinition;
import org.sbml.jsbml.History;
import org.sbml.jsbml.InitialAssignment;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.ListOf.Type;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.MathContainer;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.ModifierSpeciesReference;
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
 * @author Meike Aichele
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
					libDoc.getModel().addCompartmentType(libCt);
				} else if (sbase instanceof UnitDefinition){
					UnitDefinition udef = (UnitDefinition) sbase;
					org.sbml.libsbml.UnitDefinition libUdef = (org.sbml.libsbml.UnitDefinition) libSbase;
					libDoc.getModel().addUnitDefinition(libUdef);
					if (udef.isSetListOfUnits()){
						for (org.sbml.jsbml.Unit u : udef.getListOfUnits()){
							//makes a recursion for every unit u in the list of units
							DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(u);
							nodeAdded(newnode);
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
						// makes a recursion for all compartments in the list
						for (Compartment comp : model.getListOfCompartments()){
							DefaultMutableTreeNode compnode = new DefaultMutableTreeNode(comp);
							nodeAdded(compnode);
						}
					}
					if (model.isSetListOfCompartmentTypes()){
						// makes a recursion for all compartment types in the list
						for (CompartmentType comptype : model.getListOfCompartmentTypes()){
							DefaultMutableTreeNode comptypenode = new DefaultMutableTreeNode(comptype);
							nodeAdded(comptypenode);
						}
					}
					if (model.isSetListOfConstraints()){
						// makes a recursion for all constraints in the list
						for (Constraint constr : model.getListOfConstraints()){
							DefaultMutableTreeNode constrnode = new DefaultMutableTreeNode(constr);
							nodeAdded(constrnode);
						}
					}
					if (model.isSetListOfEvents()){
						// makes a recursion for all events in the list
						for (Event event : model.getListOfEvents()){
							DefaultMutableTreeNode eventnode = new DefaultMutableTreeNode(event);
							nodeAdded(eventnode);
						}
					}
					if (model.isSetListOfFunctionDefinitions()){
						// makes a recursion for all function definitions in the list
						for (FunctionDefinition funcdef : model.getListOfFunctionDefinitions()){
							DefaultMutableTreeNode funcdefnode = new DefaultMutableTreeNode(funcdef);
							nodeAdded(funcdefnode);
						}
					}
					if (model.isSetListOfInitialAssignments()){
						// makes a recursion for all initial assignments in the list
						for (InitialAssignment initassign : model.getListOfInitialAssignments()){
							DefaultMutableTreeNode initassignnode = new DefaultMutableTreeNode(initassign);
							nodeAdded(initassignnode);
						}
					}
					if (model.isSetListOfParameters()){
						// makes a recursion for all parameters in the list
						for (Parameter param : model.getListOfParameters()){
							DefaultMutableTreeNode paramnode = new DefaultMutableTreeNode(param);
							nodeAdded(paramnode);
						}
					}
					if (model.isSetListOfReactions()){
						// makes a recursion for all reactions in the list
						for (Reaction reac : model.getListOfReactions()){
							DefaultMutableTreeNode reacnode = new DefaultMutableTreeNode(reac);
							nodeAdded(reacnode);
						}
					}
					if (model.isSetListOfRules()){
						// makes a recursion for all rules in the list
						for (Rule rule : model.getListOfRules()){
							DefaultMutableTreeNode rulenode = new DefaultMutableTreeNode(rule);
							nodeAdded(rulenode);
						}
					}
					if (model.isSetListOfSpecies()){
						// makes a recursion for all species in the list
						for (Species spec : model.getListOfSpecies()){
							DefaultMutableTreeNode specnode = new DefaultMutableTreeNode(spec);
							nodeAdded(specnode);
						}
					}
					if (model.isSetListOfSpeciesTypes()){
						// makes a recursion for all species types in the list
						for (SpeciesType spectype : model.getListOfSpeciesTypes()){
							DefaultMutableTreeNode spectypenode = new DefaultMutableTreeNode(spectype);
							nodeAdded(spectypenode);
						}
					}
					if (model.isSetListOfUnitDefinitions()){
						// makes a recursion for all unit definitions in the list
						for (UnitDefinition unitdef : model.getListOfUnitDefinitions()){
							DefaultMutableTreeNode unitdefnode = new DefaultMutableTreeNode(unitdef);
							nodeAdded(unitdefnode);
						}
					}
					if (model.isSetSubstanceUnits()){
						libModel.setSubstanceUnits(model.getSubstanceUnits());
					}
					if (model.isSetTimeUnits()){
						libModel.setTimeUnits(model.getTimeUnits());
					}
					if (model.isSetVolumeUnits()){
						libModel.setVolumeUnits(model.getVolumeUnits());
					}
					libDoc.setModel(libModel);
				} else if (sbase instanceof Reaction){
					Reaction reac = (Reaction) sbase;
					org.sbml.libsbml.Reaction libReac = (org.sbml.libsbml.Reaction) libSbase;
					libDoc.getModel().addReaction(libReac);
					if (reac.isSetCompartment()){
						libReac.setCompartment(reac.getCompartment());
					}
					if (reac.isSetFast()){
						libReac.setFast(reac.getFast());
					}
					if (reac.isSetKineticLaw()){
						DefaultMutableTreeNode kinlawnode = new DefaultMutableTreeNode(reac.getKineticLaw());
						nodeAdded(kinlawnode);
					}
					if (reac.isSetListOfModifiers()){
						// makes a recursion for all modifiers in the list
						for (ModifierSpeciesReference modi : reac.getListOfModifiers()){
							DefaultMutableTreeNode modinode = new DefaultMutableTreeNode(modi);
							nodeAdded(modinode);
						}
					}
					if (reac.isSetListOfProducts()){
						// makes a recursion for all products in the list
						for (SpeciesReference product : reac.getListOfProducts()){
							DefaultMutableTreeNode productnode = new DefaultMutableTreeNode(product);
							nodeAdded(productnode);
						}
					}
					if (reac.isSetListOfReactants()){
						// makes a recursion for all reactants in the list
						for (SpeciesReference reactant : reac.getListOfReactants()){
							DefaultMutableTreeNode reactantnode = new DefaultMutableTreeNode(reactant);
							nodeAdded(reactantnode);
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
					if (sbase instanceof ModifierSpeciesReference){
						// search the associated reaction to add the modifier
						Reaction parentreac = (Reaction)sbase.getParentSBMLObject().getParentSBMLObject();
						libDoc.getModel().getReaction(parentreac.getId()).addModifier((org.sbml.libsbml.ModifierSpeciesReference) libSiSpecRef);
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
						// search the associated reaction
						Reaction parentreac = (Reaction)sbase.getParentSBMLObject().getParentSBMLObject();
						// ask if its a product or a reactant and add it to the corresponding reaction in libDoc
						if (specRef.getParentSBMLObject().equals(Type.listOfProducts)){
							libDoc.getModel().getReaction(parentreac.getId()).addProduct(libSpecRef);
						} else if (specRef.getParentSBMLObject().equals(Type.listOfReactants)){
							libDoc.getModel().getReaction(parentreac.getId()).addReactant(libSpecRef);
						}
					}
				} else if (sbase instanceof AbstractNamedSBaseWithUnit){
					if (sbase instanceof Event){
						Event event = (Event) sbase;
						org.sbml.libsbml.Event libEvent = (org.sbml.libsbml.Event) libSbase;
						if (event.isSetDelay()){
							//libEvent.setDelay(event.getDelay());
						}
						if (event.isSetListOfEventAssignments()){
							for (EventAssignment eventassign : event.getListOfEventAssignments()){
								DefaultMutableTreeNode eventassignnode = new DefaultMutableTreeNode(eventassign);
								nodeAdded(eventassignnode);
							}
						}
						if (event.isSetPriority()){
							//libEvent.setPriority(event.getPriority());
						}
						if (event.isSetTimeUnits()){
							libEvent.setTimeUnits(event.getTimeUnits());
						}
						if (event.isSetTrigger()){
							//libEvent.setTrigger(event.getTrigger());
						}
						if (event.isSetUnits()){
							// are there units in events of libsbml?
						}
						if (event.isSetUseValuesFromTriggerTime()){
							libEvent.setUseValuesFromTriggerTime(event.getUseValuesFromTriggerTime());
						}
						libDoc.getModel().addEvent(libEvent);
					}
					else if (sbase instanceof QuantityWithUnit){
						if (sbase instanceof LocalParameter){
							LocalParameter locParam = (LocalParameter) sbase;
							org.sbml.libsbml.LocalParameter libLocParam = (org.sbml.libsbml.LocalParameter) libSbase;
							if (locParam.isSetValue()){
								libLocParam.setValue(locParam.getValue());
							}
							// search the associated kinetic law
							KineticLaw kinLaw =(KineticLaw)locParam.getParentSBMLObject().getParentSBMLObject();
							// search the reaction where the kinetic law is in
							Reaction reac = (Reaction) kinLaw.getParentSBMLObject();
							// find the corresponding reaction in libDoc and the KineticLaw in it and add the local parameter
							libDoc.getModel().getReaction(reac.getId()).getKineticLaw().addLocalParameter(libLocParam);
						}
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
								libDoc.getModel().addCompartment(libC);
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
								libDoc.getModel().addSpecies(libS);
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
								libDoc.getModel().addParameter(libParam);
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
				if (unit.getParent().getParent() instanceof UnitDefinition){
					//search the associated unit definition
					UnitDefinition udef = (UnitDefinition) unit.getParent().getParent();
					//search the corresponding unit definition in libsbml and add the new unit
					libDoc.getModel().getUnitDefinition(udef.getId()).addUnit(libUnit);
				}

			} else if (node instanceof SBMLDocument){
				//is this case really needed?
			} else if (node instanceof ListOf<?>){
				for (SBase sb : (ListOf<?>) node){
					DefaultMutableTreeNode sbasenode = new DefaultMutableTreeNode(sb);
					nodeAdded(sbasenode);
				}	
			
			} else if (sbase instanceof AbstractMathContainer){
				if (sbase instanceof FunctionDefinition){
					libDoc.getModel().addFunctionDefinition((org.sbml.libsbml.FunctionDefinition)libSbase);
				}
				else if (sbase instanceof KineticLaw){
					KineticLaw kinLaw = (KineticLaw) sbase;
					org.sbml.libsbml.KineticLaw libKinLaw = (org.sbml.libsbml.KineticLaw) libSbase;
					if (kinLaw.isSetMath()){
						//libKinLaw.setMath(kinLaw.getMath());
					}
					if (kinLaw.isSetListOfLocalParameters()){
						//makes a recursion 
						for (LocalParameter locParam : kinLaw.getListOfLocalParameters()){
							DefaultMutableTreeNode locParamnode = new DefaultMutableTreeNode(locParam);
							nodeAdded(locParamnode);
						}
					}
					if (kinLaw.isSetListOfParameters()){
						//recursion? 
					}
					if (kinLaw.isSetSubstanceUnits()){
						libKinLaw.setSubstanceUnits(kinLaw.getSubstanceUnits());
					}
					if (kinLaw.isSetTimeUnits()){
						libKinLaw.setTimeUnits(kinLaw.getTimeUnits());
					}
					if (kinLaw.isSetUnits()){
						// are there units in KineticLaw in libsbml?
					}
					Reaction reac = kinLaw.getParentSBMLObject();
					libDoc.getModel().getReaction(reac.getId()).setKineticLaw(libKinLaw);
				}
				else if (sbase instanceof InitialAssignment){
					InitialAssignment initAssign = (InitialAssignment) sbase;
					org.sbml.libsbml.InitialAssignment libInitAssign = (org.sbml.libsbml.InitialAssignment) libSbase;
					if (initAssign.isSetSymbol()){
						libInitAssign.setSymbol(initAssign.getSymbol());
					}
					if (initAssign.isSetVariable()){
						// no corresponding method in libSBML? 
					}
					if (initAssign.isSetMath()){
						//libInitAssign.setMath(initAssign.getMath());
					}
					libDoc.getModel().addInitialAssignment(libInitAssign);
				}
				else if (sbase instanceof EventAssignment){
					EventAssignment eventAssign = (EventAssignment) sbase;
					org.sbml.libsbml.EventAssignment libEventAssign = (org.sbml.libsbml.EventAssignment) libSbase;
					if (eventAssign.isSetVariable()){
						libEventAssign.setVariable(eventAssign.getVariable());
					}
					if (eventAssign.isSetMath()){
						//libEventAssign.setMath(eventAssign.getMath());
					}
					libDoc.getModel().getEvent(((Event)eventAssign.getParent().getParent()).getId()).addEventAssignment(libEventAssign);
				}
				else if (sbase instanceof StoichiometryMath){
					StoichiometryMath sMath = (StoichiometryMath) sbase;
					org.sbml.libsbml.StoichiometryMath libSMath = (org.sbml.libsbml.StoichiometryMath) libSbase;
					if (sMath.isSetMath()){
						//libSMath.setMath(sMath.getMath());
					}
					libDoc.getModel().getSpeciesReference(sMath.getParent().getId()).setStoichiometryMath(libSMath);
				}
				else if (sbase instanceof Trigger){
					Trigger trig = (Trigger) sbase;
					org.sbml.libsbml.Trigger LibTrig = (org.sbml.libsbml.Trigger) libSbase;
					if (trig.isSetInitialValue()){
						LibTrig.setInitialValue(trig.getInitialValue());
					}
					if (trig.isSetPersistent()){
						LibTrig.setPersistent(trig.getPersistent());
					}
					if (trig.isSetMath()){
						//LibTrig.setMath(trig.getMath());
					}
					libDoc.getModel().getEvent(trig.getParent().getId()).setTrigger(LibTrig);
				}
				else if (sbase instanceof Rule){
					Rule rule = (Rule) sbase;
					org.sbml.libsbml.Rule libRule = (org.sbml.libsbml.Rule) libSbase;
					if (rule.isSetMath()){
						//LibRule.setMath(rule.getMath());
					}
					if (sbase instanceof AlgebraicRule){
						//there is nothing more to do
					}
					else{
						ExplicitRule expRule = (ExplicitRule) rule;
						if (sbase instanceof RateRule){
							org.sbml.libsbml.RateRule libRateRule = (org.sbml.libsbml.RateRule) libRule;
							if (expRule.isSetVariable()){
								libRateRule.setVariable(expRule.getVariable());
							}
							if (expRule.isSetUnits()){
								libRateRule.setUnits(expRule.getUnits());
							}
						}
						else if (sbase instanceof AssignmentRule){
							org.sbml.libsbml.AssignmentRule libAssignRule = (org.sbml.libsbml.AssignmentRule) libRule;
							if (expRule.isSetVariable()){
								libAssignRule.setVariable(expRule.getVariable());
							}
							if (expRule.isSetUnits()){
								libAssignRule.setUnits(expRule.getUnits());
							}
						}
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
