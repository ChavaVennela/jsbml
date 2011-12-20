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
			if (node instanceof org.sbml.jsbml.AbstractNamedSBase){
				if (node instanceof CompartmentType) {
					CompartmentType ct = (CompartmentType) node;
					org.sbml.libsbml.CompartmentType libCt = libDoc.getModel().createCompartmentType();
					transferNamedSBaseProperties(ct, libCt);
					if (ct.isSetNotes()){
						libCt.setNotes(ct.getNotesString());
					}
				} else if (node instanceof UnitDefinition){
					UnitDefinition udef = (UnitDefinition) node;
					org.sbml.libsbml.UnitDefinition libUdef = libDoc.getModel().createUnitDefinition();
					transferNamedSBaseProperties(udef, libUdef);
					if (udef.isSetListOfUnits()){
						for (org.sbml.jsbml.Unit u : udef.getListOfUnits()){
							//makes a recursion for every unit u in the list of units
							DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(u);
							nodeAdded(newnode);
						}
					}
				} else if (node instanceof Model){
					Model model = (Model) node;
					LibSBMLWriter writer = new LibSBMLWriter();
					libDoc.setModel((org.sbml.libsbml.Model) writer.writeModel(model));
		
				} else if (node instanceof Reaction){
					Reaction reac = (Reaction) node;
					org.sbml.libsbml.Reaction libReac = libDoc.getModel().createReaction();
					transferNamedSBaseProperties(reac, libReac);
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
				} else if (node instanceof SimpleSpeciesReference){
					SimpleSpeciesReference siSpecRef = (SimpleSpeciesReference) node;
					if (node instanceof ModifierSpeciesReference){
						org.sbml.libsbml.ModifierSpeciesReference libModifier = libDoc.getModel().createModifier();
						transferSimpleSpeciesReferenceProperties(siSpecRef, libModifier);
					}
					if (node instanceof SpeciesReference){
						SpeciesReference specRef = (SpeciesReference) node;
						if (specRef.getParentSBMLObject().equals(Type.listOfProducts)){
							org.sbml.libsbml.SpeciesReference libProduct = libDoc.getModel().createProduct();
							if (specRef.isSetConstant()){
								libProduct.setConstant(specRef.getConstant());
							}	
							if (specRef.isSetStoichiometry()){
								libProduct.setStoichiometry(specRef.getStoichiometry());
							}
							transferSimpleSpeciesReferenceProperties(specRef, libProduct);
						} else if (specRef.getParentSBMLObject().equals(Type.listOfReactants)){
							org.sbml.libsbml.SpeciesReference libReactant = libDoc.getModel().createReactant();
							if (specRef.isSetConstant()){
								libReactant.setConstant(specRef.getConstant());
							}	
							if (specRef.isSetStoichiometry()){
								libReactant.setStoichiometry(specRef.getStoichiometry());
							}
							transferSimpleSpeciesReferenceProperties(specRef, libReactant);
						}
					}
				} else if (node instanceof AbstractNamedSBaseWithUnit){
					if (node instanceof Event){
						Event event = (Event) node;
						org.sbml.libsbml.Event libEvent = libDoc.getModel().createEvent();
						transferNamedSBaseProperties(event, libEvent);
						if (event.isSetDelay()){
							//libEvent.setDelay(event.getDelay());
							//TODO
						}
						if (event.isSetListOfEventAssignments()){
							for (EventAssignment eventassign : event.getListOfEventAssignments()){
								DefaultMutableTreeNode eventassignnode = new DefaultMutableTreeNode(eventassign);
								nodeAdded(eventassignnode);
							}
						}
						if (event.isSetPriority()){
							//libEvent.setPriority(event.getPriority());
							//TODO
						}
						if (event.isSetTimeUnits()){
							libEvent.setTimeUnits(event.getTimeUnits());
						}
						if (event.isSetTrigger()){
							//libEvent.setTrigger(event.getTrigger());
							//TODO
						}
						if (event.isSetUnits()){
							// are there units in events of libsbml?
							//TODO
						}
						if (event.isSetUseValuesFromTriggerTime()){
							libEvent.setUseValuesFromTriggerTime(event.getUseValuesFromTriggerTime());
						}
					}
					else if (node instanceof QuantityWithUnit){
						if (node instanceof LocalParameter){
							LocalParameter locParam = (LocalParameter) node;
							org.sbml.libsbml.LocalParameter libLocParam = libDoc.getModel().createKineticLawLocalParameter();
							transferNamedSBaseProperties(locParam, libLocParam);
							if (locParam.isSetValue()){
								libLocParam.setValue(locParam.getValue());
							}
						}
						else if (node instanceof Symbol){
							if (node instanceof Compartment){
								Compartment c = (Compartment) node;
								org.sbml.libsbml.Compartment libC = libDoc.getModel().createCompartment();
								transferNamedSBaseProperties(c, libC);
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
							} else if (node instanceof Species){
								Species spec = (Species) node;
								org.sbml.libsbml.Species libS = libDoc.getModel().createSpecies();
								transferNamedSBaseProperties(spec, libS);
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
							} else if (node instanceof Parameter){
								Parameter param = (Parameter) node;
								org.sbml.libsbml.Parameter libParam = libDoc.getModel().createParameter();
								transferNamedSBaseProperties(param, libParam);
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
				Unit unit = (Unit) node;
				org.sbml.libsbml.Unit libUnit = libDoc.getModel().createUnit();
				transferSBaseProperties(unit, libUnit);
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
					//TODO
				}

			} else if (node instanceof SBMLDocument){
				//TODO use method from LibSBMLReader
			} else if (node instanceof ListOf<?>){
				for (SBase sb : (ListOf<?>) node){
					DefaultMutableTreeNode sbasenode = new DefaultMutableTreeNode(sb);
					nodeAdded(sbasenode);
				}	
			
			} else if (node instanceof AbstractMathContainer){
				if (node instanceof FunctionDefinition){
					FunctionDefinition funcDef = (FunctionDefinition) node;
					org.sbml.libsbml.FunctionDefinition libFuncDef = libDoc.getModel().createFunctionDefinition();
					transferMathContainerProperties(funcDef, libFuncDef);
				}
				else if (node instanceof KineticLaw){
					KineticLaw kinLaw = (KineticLaw) node;
					org.sbml.libsbml.KineticLaw libKinLaw = libDoc.getModel().createKineticLaw();
					transferMathContainerProperties(kinLaw, libKinLaw);
					if (kinLaw.isSetListOfLocalParameters()){
						//makes a recursion 
						for (LocalParameter locParam : kinLaw.getListOfLocalParameters()){
							DefaultMutableTreeNode locParamnode = new DefaultMutableTreeNode(locParam);
							nodeAdded(locParamnode);
						}
					}
					if (kinLaw.isSetSubstanceUnits()){
						libKinLaw.setSubstanceUnits(kinLaw.getSubstanceUnits());
					}
					if (kinLaw.isSetTimeUnits()){
						libKinLaw.setTimeUnits(kinLaw.getTimeUnits());
					}
					if (kinLaw.isSetUnits()){
						// are there units in KineticLaw in libsbml?
						//TODO
					}					
				}
				else if (node instanceof InitialAssignment){
					InitialAssignment initAssign = (InitialAssignment) node;
					org.sbml.libsbml.InitialAssignment libInitAssign = libDoc.getModel().createInitialAssignment();
					transferMathContainerProperties(initAssign, libInitAssign);
					if (initAssign.isSetSymbol()){
						libInitAssign.setSymbol(initAssign.getSymbol());
					}
					if (initAssign.isSetVariable()){
						// no corresponding method in libSBML? 
						//TODO
					}
				}
				else if (node instanceof EventAssignment){
					EventAssignment eventAssign = (EventAssignment) node;
					org.sbml.libsbml.EventAssignment libEventAssign = libDoc.getModel().createEventAssignment();
					transferMathContainerProperties(eventAssign, libEventAssign);
					if (eventAssign.isSetVariable()){
						libEventAssign.setVariable(eventAssign.getVariable());
					}
				}
				else if (node instanceof StoichiometryMath){
					StoichiometryMath sMath = (StoichiometryMath) node;
					org.sbml.libsbml.StoichiometryMath libSMath = libDoc.getModel().getSpeciesReference(sMath.getParent().getId()).createStoichiometryMath();
					transferMathContainerProperties(sMath, libSMath);
				}
				else if (node instanceof Trigger){
					Trigger trig = (Trigger) node;
					org.sbml.libsbml.Trigger LibTrig = libDoc.getModel().createTrigger();
					transferMathContainerProperties(trig, LibTrig);
					if (trig.isSetInitialValue()){
						LibTrig.setInitialValue(trig.getInitialValue());
					}
					if (trig.isSetPersistent()){
						LibTrig.setPersistent(trig.getPersistent());
					}
				}
				else if (node instanceof Rule){
					Rule rule = (Rule) node;
					if (node instanceof AlgebraicRule){
						org.sbml.libsbml.AlgebraicRule libAlgRule = libDoc.getModel().createAlgebraicRule();
						transferMathContainerProperties(rule, libAlgRule);
					}
					else{
						ExplicitRule expRule = (ExplicitRule) rule;
						if (node instanceof RateRule){
							org.sbml.libsbml.RateRule libRateRule = libDoc.getModel().createRateRule();
							transferMathContainerProperties(expRule, libRateRule);
							if (expRule.isSetVariable()){
								libRateRule.setVariable(expRule.getVariable());
							}
							if (expRule.isSetUnits()){
								libRateRule.setUnits(expRule.getUnits());
							}
						}
						else if (node instanceof AssignmentRule){
							org.sbml.libsbml.AssignmentRule libAssignRule = libDoc.getModel().createAssignmentRule();
							transferMathContainerProperties(expRule, libAssignRule);
							if (expRule.isSetVariable()){
								libAssignRule.setVariable(expRule.getVariable());
							}
							if (expRule.isSetUnits()){
								libAssignRule.setUnits(expRule.getUnits());
							}
						}
					}
				}
				else if (node instanceof Constraint){
					Constraint constr = (Constraint) node;
					org.sbml.libsbml.Constraint libConstr = libDoc.getModel().createConstraint();
					transferMathContainerProperties(constr, libConstr);
					if (constr.isSetMessage()){
						//libConstr.setMessage(constr.getMessage());
						//TODO
					}
				}
				else if (node instanceof Delay){
					Delay delay = (Delay) node;
					org.sbml.libsbml.Delay libDelay = libDoc.getModel().createDelay();
					transferMathContainerProperties(delay, libDelay);
				}
				else if (node instanceof Priority){
					Priority prio = (Priority) node;
					org.sbml.libsbml.Priority libPrio = libDoc.getModel().getEvent(prio.getParent().getId()).createPriority();
					transferMathContainerProperties(prio, libPrio);
				}
			}
		} else if (node instanceof AnnotationElement){
			if (node instanceof CVTerm){
				CVTerm cvt = (CVTerm) node;
				org.sbml.libsbml.CVTerm libCvt = new org.sbml.libsbml.CVTerm(); //TODO create CVTerm in libSBML
				if (cvt.isSetBiologicalQualifierType()){
					libCvt.setBiologicalQualifierType(cvt.getBiologicalQualifierType().getElementNameEquivalent());
				}
				if (cvt.isSetQualifierType()){
					//libCvt.setQualifierType(cvt.getQualifierType());
				}
				if (cvt.isSetType()){
					//TODO
				}
			}
			else if (node instanceof History){
				History his = (History) node;
				//TODO
			}
			else if (node instanceof Annotation){
				Annotation annot = (Annotation) node;
				//TODO				
			}
			else if (node instanceof Creator){
				//TODO
			}
		} else if (node instanceof ASTNode){
			ASTNode astnode = (ASTNode) node;
			convertASTNode(astnode);
			//TODO: set this ASTNode in libDoc
		} else if (node instanceof TreeNodeAdapter){
			TreeNodeAdapter treeNodeAd = (TreeNodeAdapter) node;
			//TODO
		} else if (node instanceof XMLToken){
			XMLToken token = (XMLToken) node;
			org.sbml.libsbml.XMLToken libToken;
			//TODO
		}
	}

	/* (non-Javadoc)
	 * @see org.sbml.jsbml.util.TreeNodeChangeListener#nodeRemoved(javax.swing.tree.TreeNode)
	 */
	public void nodeRemoved(TreeNode node) {
		org.sbml.libsbml.Model libModel = libDoc.getModel();
		if (node instanceof AbstractSBase){
			if (node instanceof org.sbml.jsbml.AbstractNamedSBase){
				if (node instanceof CompartmentType) {
					libModel.removeCompartmentType(((CompartmentType) node).getId());
				} else if (node instanceof UnitDefinition){
					libModel.removeUnitDefinition(((UnitDefinition) node).getId());
				} else if (node instanceof Model){
					libModel.delete();
				} else if (node instanceof Reaction){
					libModel.removeReaction(((Reaction) node).getId());
				} else if (node instanceof SimpleSpeciesReference){
					if (node instanceof ModifierSpeciesReference){
						//get the corresponding reaction-ID
						String reacID =((Reaction) ((ModifierSpeciesReference)node).getParentSBMLObject().getParentSBMLObject()).getId();
						// search the corresponding reaction and remove the modifier
						libModel.getReaction(reacID).removeModifier(((ModifierSpeciesReference) node).getId());
					}
					if (node instanceof SpeciesReference){
						SpeciesReference specRef = (SpeciesReference) node;
						String reacID = ((Reaction)specRef.getParentSBMLObject().getParentSBMLObject()).getId();
						if (specRef.getParentSBMLObject().equals(Type.listOfProducts)){
							//search reaction and remove the product
							libModel.getReaction(reacID).removeProduct(specRef.getId());
						} else if (specRef.getParentSBMLObject().equals(Type.listOfReactants)){
							// search reaction and remove the reactant
							libModel.getReaction(reacID).removeReactant(specRef.getId());
						}
					}
				} else if (node instanceof AbstractNamedSBaseWithUnit){
					if (node instanceof Event){
						libModel.removeEvent(((Event) node).getId());
					}
					else if (node instanceof QuantityWithUnit){
						if (node instanceof LocalParameter){
							// is a LocalParameter a Parameter?
							libModel.removeParameter(((LocalParameter) node).getId());
						}
						else if (node instanceof Symbol){
							if (node instanceof Compartment){
								libModel.removeCompartment(((Compartment) node).getId());
							} else if (node instanceof Species){
								libModel.removeSpecies(((Species) node).getId());
							} else if (node instanceof Parameter){
								libModel.removeParameter(((Parameter) node).getId());
							} 
						}
					}
				}
			} else if (node instanceof Unit){
				// search corresponding UnitDefinition and remove the unit
				Unit unit = (Unit) node;
				UnitDefinition udef = (UnitDefinition) unit.getParentSBMLObject().getParentSBMLObject();
				//TODO: find out number of the unit to remove it
				libModel.getUnitDefinition(udef.getId()).removeUnit(0);
			} else if (node instanceof SBMLDocument){
				libDoc.delete();
			} else if (node instanceof ListOf<?>){
				// make recursion and remove all elements in the list
				for(Object o : (ListOf<?>)node ){
					TreeNode newNode = new DefaultMutableTreeNode(o);
					nodeRemoved(newNode);
				}
			} else if (node instanceof AbstractMathContainer){
				if (node instanceof FunctionDefinition){
					libModel.removeFunctionDefinition(((FunctionDefinition) node).getId());
				}
				else if (node instanceof KineticLaw){
					//TODO: don't know if it works, when kinlaw is set null
					Reaction corresreac = ((KineticLaw) node).getParentSBMLObject();
					libModel.getReaction(corresreac.getId()).setKineticLaw(null);
				}
				else if (node instanceof InitialAssignment){
					// can i use the MetaId here?
					libModel.getInitialAssignment(((InitialAssignment) node).getMetaId()).delete();
				}
				else if (node instanceof EventAssignment){
					// search corresponding event and remove
					Event event = (Event) ((EventAssignment) node).getParentSBMLObject();
					libModel.getEvent(event.getId()).removeEventAssignment(((EventAssignment) node).getMetaId());
				}
				else if (node instanceof StoichiometryMath){
					// search corresponding SpeciesReference and delete the StoichiometryMath of it
					SpeciesReference specRef = (SpeciesReference) ((StoichiometryMath) node).getParentSBMLObject();
					libModel.getSpeciesReference(specRef.getId()).getStoichiometryMath().delete();
				}
				else if (node instanceof Trigger){
					//TODO
				}
				else if (node instanceof Rule){
					//is this correct?!?
					libModel.removeRule(((Rule) node).getMetaId());
				}
				else if (node instanceof Constraint){
					//TODO: removeConstraint() needs a long-argument
					//libModel.removeConstraint(node);
				}
				else if (node instanceof Delay){
					//TODO
				}
				else if (node instanceof Priority){
					//TODO
				}
			}
		} else if (node instanceof AnnotationElement){
			if (node instanceof CVTerm){
				//TODO: search right CVTerm
				libDoc.getCVTerm(0).delete();
			}
			else if (node instanceof History){
				//TODO				
			}
			else if (node instanceof Annotation){
				//TODO				
			}
			else if (node instanceof Creator){
				//TODO
			}
		} else if (node instanceof ASTNode){
			//TODO
			
		} else if (node instanceof TreeNodeAdapter){
			//TODO
		} else if (node instanceof XMLToken){
			//TODO
		}
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
	
	/**
	 * sets MetaId, SBOTerm, Notes and Annotation in the libSBML object, 
	 * if it's set in the JSBML object.
	 * @param sbase
	 * @param libSBase
	 */
	private void transferSBaseProperties(SBase sbase,
			org.sbml.libsbml.SBase libSBase) {
		if (sbase.isSetMetaId()) {
			libSBase.setMetaId(libSBase.getMetaId());
		}
		if (sbase.isSetSBOTerm()) {
			libSBase.setSBOTerm(libSBase.getSBOTerm());
		}
		if (sbase.isSetNotes()) {
			libSBase.setNotes(libSBase.getNotesString());
		}
		if (sbase.isSetAnnotation()){
			libSBase.setAnnotation(sbase.getAnnotationString());
		}
	}
	
	/**
	 * sets the name and the id in the libSBML object, when it's set in the JSBML object
	 * and calls the method transferSBaseProperties(SBase, libSBase).
	 * @param sbase
	 * @param libSBase
	 */
	private void transferNamedSBaseProperties(SBase sbase, org.sbml.libsbml.SBase libSBase){
		if (((org.sbml.jsbml.NamedSBase) sbase).isSetName()){
			libSBase.setName(((org.sbml.jsbml.NamedSBase) sbase).getName());
		}
		if (((org.sbml.jsbml.NamedSBase) sbase).isSetId()){
			libSBase.setId(((org.sbml.jsbml.NamedSBase) sbase).getId());
		}
		transferSBaseProperties(sbase, libSBase);
	}

	/**
	 * sets the species in libSBML object if the species is set in the JSBML object
	 * and calls the method transferNamedSBaseProperties(SBase, libSBase).
	 * @param sbase
	 * @param libSBase
	 */
	private void transferSimpleSpeciesReferenceProperties(SBase sbase, org.sbml.libsbml.SBase libSBase){
		if (((SimpleSpeciesReference) sbase).isSetSpecies()){
			((org.sbml.libsbml.SimpleSpeciesReference) libSBase).setSpecies(((SimpleSpeciesReference) sbase).getSpecies());
		}
		transferNamedSBaseProperties(sbase, libSBase);
	}
	
	/**
	 * sets the math ASTNode in the libSBML object if it's set in the JSBML object
	 * and calls the convert-method for the ASTNodes.
	 * @param mathCont
	 * @param libMathCont
	 */
	private void transferMathContainerProperties(MathContainer mathCont, org.sbml.libsbml.SBase libMathCont){
		transferSBaseProperties(mathCont, libMathCont);
		if (mathCont.isSetMath()){
			if (libMathCont instanceof org.sbml.libsbml.FunctionDefinition){
				((org.sbml.libsbml.FunctionDefinition) libMathCont).setMath(convertASTNode(mathCont.getMath()));
			}else if (libMathCont instanceof org.sbml.libsbml.KineticLaw){
				((org.sbml.libsbml.KineticLaw) libMathCont).setMath(convertASTNode(mathCont.getMath()));
			}else if (libMathCont instanceof org.sbml.libsbml.Rule){
				((org.sbml.libsbml.Rule) libMathCont).setMath(convertASTNode(mathCont.getMath()));
			}else if (libMathCont instanceof org.sbml.libsbml.StoichiometryMath){
				((org.sbml.libsbml.StoichiometryMath) libMathCont).setMath(convertASTNode(mathCont.getMath()));
			}else if (libMathCont instanceof org.sbml.libsbml.Trigger){
				((org.sbml.libsbml.Trigger) libMathCont).setMath(convertASTNode(mathCont.getMath()));
			}else if (libMathCont instanceof org.sbml.libsbml.EventAssignment){
				((org.sbml.libsbml.EventAssignment) libMathCont).setMath(convertASTNode(mathCont.getMath()));
			}else if (libMathCont instanceof org.sbml.libsbml.InitialAssignment){
				((org.sbml.libsbml.InitialAssignment) libMathCont).setMath(convertASTNode(mathCont.getMath()));
			}else if (libMathCont instanceof org.sbml.libsbml.Constraint){
				((org.sbml.libsbml.Constraint) libMathCont).setMath(convertASTNode(mathCont.getMath()));
			}else if (libMathCont instanceof org.sbml.libsbml.Delay){
				((org.sbml.libsbml.Delay) libMathCont).setMath(convertASTNode(mathCont.getMath()));
			}else if (libMathCont instanceof org.sbml.libsbml.Priority){
				((org.sbml.libsbml.Priority) libMathCont).setMath(convertASTNode(mathCont.getMath()));
			}
		}
		
	}

	/**
	 * 
	 * @param math
	 * @return
	 */
	private org.sbml.libsbml.ASTNode convertASTNode(ASTNode math) {
		org.sbml.libsbml.ASTNode libASTNode = new org.sbml.libsbml.ASTNode();
		//TODO: fill libASTNode with Properties of ASTNode math
		return libASTNode;
	}
}
