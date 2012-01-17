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
import java.util.Date;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
import org.sbml.jsbml.StoichiometryMath;
import org.sbml.jsbml.Symbol;
import org.sbml.jsbml.Trigger;
import org.sbml.jsbml.Unit;
import org.sbml.jsbml.UnitDefinition;
import org.sbml.jsbml.util.TreeNodeAdapter;
import org.sbml.jsbml.util.TreeNodeChangeEvent;
import org.sbml.jsbml.util.TreeNodeChangeListener;
import org.sbml.jsbml.xml.XMLToken;
import org.sbml.libsbml.ModelCreator;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.XMLNode;
import org.sbml.libsbml.libsbmlConstants;

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
	private org.sbml.jsbml.SBMLDocument doc;
	
	/**
	 * 
	 */
	private static final transient Logger logger = Logger
			.getLogger(LibSBMLChangeListener.class);

	/**
	 * 
	 * @param doc
	 * @param libDoc
	 */
	public LibSBMLChangeListener(org.sbml.jsbml.SBMLDocument doc,
			org.sbml.libsbml.SBMLDocument libDoc) {
		this.libDoc = libDoc;
		this.doc = doc;
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
							Delay delay = event.getDelay();
							org.sbml.libsbml.Delay libDelay = libEvent.createDelay();
							transferMathContainerProperties(delay, libDelay);
							libEvent.setDelay(libDelay);
						}
						if (event.isSetListOfEventAssignments()){
							for (EventAssignment eventassign : event.getListOfEventAssignments()){
								DefaultMutableTreeNode eventassignnode = new DefaultMutableTreeNode(eventassign);
								nodeAdded(eventassignnode);
							}
						}
						if (event.isSetPriority()){
							Priority prio = event.getPriority();
							org.sbml.libsbml.Priority libPrio = libEvent.createPriority();
							transferMathContainerProperties(prio, libPrio);
							libEvent.setPriority(libPrio);

						}
						if (event.isSetTimeUnits()){
							libEvent.setTimeUnits(event.getTimeUnits());
						}
						if (event.isSetTrigger()){
							DefaultMutableTreeNode triggerNode = new DefaultMutableTreeNode(event.getTrigger());
							nodeAdded(triggerNode);
						}
						// this case can be dropped, because the units were already set with the case of TimoUnits above
						// if (event.isSetUnits());
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
					transferKindProperties(unit, libUnit);
				}

			} else if (node instanceof SBMLDocument){
				//TODO use method from LibSBMLReader
			} else if (node instanceof ListOf<?>){
				// I don't have to ask what type of list the node is, 
				// because of the recursion you get always the right case for every element in the list
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
					//this case can be dropped, because all units are already set with the two cases above
					//if (kinLaw.isSetUnits());					
				}
				else if (node instanceof InitialAssignment){
					InitialAssignment initAssign = (InitialAssignment) node;
					org.sbml.libsbml.InitialAssignment libInitAssign = libDoc.getModel().createInitialAssignment();
					transferMathContainerProperties(initAssign, libInitAssign);
					if (initAssign.isSetSymbol()){
						libInitAssign.setSymbol(initAssign.getSymbol());
					}
					//this case can be dropped, because the Symbol is the same as the Variable in this object
					// if (initAssign.isSetVariable());
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
					org.sbml.libsbml.Trigger LibTrig = libDoc.getModel().getEvent(trig.getParent().getId()).createTrigger();
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
						libConstr.setMessage(XMLNode.convertStringToXMLNode(constr.getMessage().toXMLString()));
					}
				}
				else if (node instanceof Delay){
					Delay delay = (Delay) node;
					// libDoc.getModel().createDelay() would be shorter but can't be used, 
					// because this would create a new Delay inside the last Event object created in this Model
					org.sbml.libsbml.Delay libDelay = libDoc.getModel().getEvent(delay.getParent().getId()).createDelay();
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
				// there is no method to create a CVTerm in libSBML, 
				// so it's set at the and of this case
				org.sbml.libsbml.CVTerm libCvt = new org.sbml.libsbml.CVTerm();
				if (cvt.isSetBiologicalQualifierType()){
					libCvt.setBiologicalQualifierType(cvt.getBiologicalQualifierType().getElementNameEquivalent());
				}
				if (cvt.isSetQualifierType()){
					// libCvt.setQualifierType(cvt.getQualifierType());
				}
				if (cvt.isSetType()){
					//TODO
				}
				libDoc.addCVTerm(libCvt);
			}
			else if (node instanceof History){
				History his = (History) node;
				org.sbml.libsbml.ModelHistory modHis = new org.sbml.libsbml.ModelHistory();
				if (his.isSetCreatedDate()){
					modHis.setCreatedDate(convertDate(his.getCreatedDate()));
				}
				if (his.isSetListOfCreators()){
					for (Creator c : his.getListOfCreators()){
						modHis.addCreator(convertToModelCreator(c));
					}
				}
				if (his.isSetListOfModification()){
					//go throw the list and add all ModifiedDates
					for(int i=0; i<his.getListOfModifiedDates().size() ;i++){
						modHis.addModifiedDate(convertDate(his.getModifiedDate(i)));
					}
				}
				if (his.isSetModifiedDate()){
					modHis.setModifiedDate(convertDate(his.getModifiedDate()));
				}
				libDoc.setModelHistory(modHis);
			}
			else if (node instanceof Annotation){
				Annotation annot = (Annotation) node;
				//TODO: where to save this in libDoc?
				if (annot.isSetAbout()){
					
				}
				if (annot.isSetAnnotation()){
					
				}
				if (annot.isSetHistory()){
					
				}
				if (annot.isSetListOfCVTerms()){
					
				}
				if (annot.isSetNonRDFannotation()){
					
				}
				if (annot.isSetRDFannotation()){
					
				}
				if (annot.isSetOtherAnnotationThanRDF()){
					
				}
			}
			else if (node instanceof Creator){
				Creator creator = (Creator) node;
				org.sbml.libsbml.ModelCreator libCreator = new ModelCreator();
				if (creator.isSetEmail()){
					libCreator.setEmail(creator.getEmail());
				}
				if (creator.isSetFamilyName()){
					libCreator.setFamilyName(creator.getFamilyName());
				}
				if (creator.isSetGivenName()){
					libCreator.setGivenName(creator.getGivenName());
				}
				if (creator.isSetOrganisation()){
					libCreator.setOrganisation(creator.getOrganisation());
				}
				libDoc.getModelHistory().addCreator(libCreator);
			}
		} else if (node instanceof ASTNode){
			ASTNode astnode = (ASTNode) node;
			logger.log(Level.DEBUG, String.format("Cannot add node" + astnode.getClass().getSimpleName()));
		} else if (node instanceof TreeNodeAdapter){
			TreeNodeAdapter treeNodeAd = (TreeNodeAdapter) node;
			logger.log(Level.DEBUG, String.format("Cannot add node" + treeNodeAd.getClass().getSimpleName()));
		} else if (node instanceof XMLToken){
			XMLToken token = (XMLToken) node;
			logger.log(Level.DEBUG, String.format("Cannot add node" + token.getClass().getSimpleName()));
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
				// search the index of this Unit object and remove it
				int index = getUnitIndex(unit, udef);
				libModel.getUnitDefinition(udef.getId()).removeUnit(index);
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
					Reaction corresreac = ((KineticLaw) node).getParentSBMLObject();
					libModel.getReaction(corresreac.getId()).getKineticLaw().delete();
				}
				else if (node instanceof InitialAssignment){
					// get the InitialAssignment object based on the symbol and delete it
					InitialAssignment initAssign = (InitialAssignment) node;
					libModel.getInitialAssignment(initAssign.getSymbol()).delete();
				}
				else if (node instanceof EventAssignment){
					// search corresponding event and remove the EventAssignment indicated by the variable
					Event event = (Event) ((EventAssignment) node).getParentSBMLObject();
					libModel.getEvent(event.getId()).removeEventAssignment(((EventAssignment) node).getVariable());
				}
				else if (node instanceof StoichiometryMath){
					// search corresponding SpeciesReference and delete the StoichiometryMath of it
					SpeciesReference specRef = (SpeciesReference) ((StoichiometryMath) node).getParentSBMLObject();
					libModel.getSpeciesReference(specRef.getId()).getStoichiometryMath().delete();
				}
				else if (node instanceof Trigger){
					//get corresponding event and delete it's trigger
					Event trigEvent = (Event) ((Trigger) node).getParentSBMLObject();
					libModel.getEvent(trigEvent.getId()).getTrigger().delete();
				}
				else if (node instanceof Rule){
					// find the variable in the Rule object to remove it
					if (node instanceof AlgebraicRule){
						AlgebraicRule rule = (AlgebraicRule) node;
						//TODO
						//Problem: there is no Variable-String in AlgebraicRule objects
					} else if (node instanceof AssignmentRule){
						AssignmentRule rule = (AssignmentRule) node;
						libModel.removeRule(rule.getVariable());
					} else if (node instanceof RateRule){
						RateRule rule = (RateRule) node;
						libModel.removeRule(rule.getVariable());
					}
				}
				else if (node instanceof Constraint){
					Constraint con = (Constraint) node;
					// find the index of this Constraint
					for (int k=0; k<con.getParent().size(); k++){
						Constraint c = con.getParent().get(k);
						if (con.equals(c)){
							libModel.removeConstraint(k);
							break;
						}
					}				
				}
				else if (node instanceof Delay){
					// find corresponding Event and delete it's Delay
					Delay delay = (Delay) node;
					libModel.getEvent(delay.getParent().getId()).getDelay().delete();
				}
				else if (node instanceof Priority){
					//find the corresponding Event and delete it's Priority
					Priority prio = (Priority) node;
					Event prioEvent = prio.getParent();
					libModel.getEvent(prioEvent.getId()).getPriority().delete();		
				}
			}
		} else if (node instanceof AnnotationElement){
			if (node instanceof CVTerm){
				//search index of CVTerm object
				CVTerm cvTerm = (CVTerm) node;
				for(int k=0; k<doc.getNumCVTerms();k++){
					CVTerm cv = doc.getCVTerm(k);
					if(cv.equals(cvTerm)){
						libDoc.getCVTerm(k).delete();
						break;
					}
				}
			}
			else if (node instanceof History){
				libDoc.getModelHistory().delete();			
			}
			else if (node instanceof Annotation){
				// it's not possible to find the right sbase in libSBML-Document
				// and when we could find it, there would be no method to delete it
				logger.log(Level.DEBUG, String.format("Cannot remove this %s in the libSBML-Document", node.getClass().getSimpleName()));
			}
			else if (node instanceof Creator){
				libDoc.getModelHistory().getCreator(getCreatorIndex((Creator) node)).delete();
			}
		} else if (node instanceof ASTNode){
			logger.log(Level.DEBUG, String.format("Cannot remove this %s in the libSBML-Document", node.getClass().getSimpleName()));
		} else if (node instanceof TreeNodeAdapter){
			logger.log(Level.DEBUG, String.format("Cannot remove this %s in the libSBML-Document", node.getClass().getSimpleName()));
		} else if (node instanceof XMLToken){
			logger.log(Level.DEBUG, String.format("Cannot remove this %s in the libSBML-Document", node.getClass().getSimpleName()));
		}
	}


	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		Object evtSrc = evt.getSource();
		String prop = evt.getPropertyName();

		if (prop.equals(TreeNodeChangeEvent.about)){	
			//evtSrc is an Annotation
			//TODO: how to find the right annotation in libDoc?
		} else if (prop.equals(TreeNodeChangeEvent.addCVTerm)){	
			//then the evtSrc is a SBase
			if (evtSrc instanceof org.sbml.jsbml.AbstractNamedSBase){
				libDoc.getElementBySId(((org.sbml.jsbml.AbstractNamedSBase) evtSrc).getId()).addCVTerm(convertCVTerm((CVTerm) evt.getNewValue()));
			} else if (evtSrc instanceof Unit){
				Unit u = (Unit) evtSrc;
				UnitDefinition udef = (UnitDefinition) u.getParent().getParent();
				libDoc.getModel().getUnitDefinition(udef.getId()).getUnit(getUnitIndex(u, udef)).addCVTerm(convertCVTerm((CVTerm) evt.getNewValue()));
			} else if (evtSrc instanceof SBMLDocument){
				libDoc.addCVTerm(convertCVTerm((CVTerm) evt.getNewValue()));
			} else if (evtSrc instanceof AbstractMathContainer){
				//TODO
			}
			// case of AnnotationElement?
		} else if (prop.equals(TreeNodeChangeEvent.addDeclaredNamespace)){
			//TODO: this case can concern every SBase		
		} else if (prop.equals(TreeNodeChangeEvent.addExtension)){
			//TODO: Annotation
		} else if (prop.equals(TreeNodeChangeEvent.addNamespace)){
			//TODO: SBase and SBMlDocument
		} else if (prop.equals(TreeNodeChangeEvent.annotation)){
			if (evtSrc instanceof Model){
				libDoc.getModel().setAnnotation(((Model) evtSrc).getAnnotationString());
			}
			// TODO: every SBase has an annotation, UNSET the annotation!!
		} else if (prop.equals(TreeNodeChangeEvent.annotationNameSpaces)){
			//TODO: Annotation
		} else if (prop.equals(TreeNodeChangeEvent.areaUnits)){
			if (evtSrc instanceof Model){
				libDoc.getModel().setAreaUnits(((Model) evtSrc).getAreaUnits());
			}
		} else if (prop.equals(TreeNodeChangeEvent.baseListType)){
			//the element is a list
			//TODO: but what is to do then in libDoc?
		} else if (prop.equals(TreeNodeChangeEvent.boundaryCondition)){
			Species spec = (Species) evtSrc;
			libDoc.getModel().getSpecies(spec.getId()).setBoundaryCondition(spec.getBoundaryCondition());
		} else if (prop.equals(TreeNodeChangeEvent.charge)) {
			Species spec = (Species) evtSrc;
			org.sbml.libsbml.Model libMod = libDoc.getModel();
			libMod.getSpecies(spec.getId()).setCharge(spec.getCharge());
		} else if (prop.equals(TreeNodeChangeEvent.childNode)){
			ASTNode node = (ASTNode) evtSrc;
			logger.log(Level.DEBUG, String.format("Couldn't change the %s in the libSBML-Document", node.getClass().getSimpleName()));
		} else if (prop.equals(TreeNodeChangeEvent.className)){
			ASTNode node = (ASTNode) evtSrc;
			logger.log(Level.DEBUG, String.format("Couldn't change the %s in the libSBML-Document", node.getClass().getSimpleName()));
		} else if (prop.equals(TreeNodeChangeEvent.compartment)){
			if (evtSrc instanceof Species){
				Species spec = (Species) evtSrc;
				libDoc.getModel().getSpecies(spec.getId()).setCompartment(spec.getCompartment());
			} else if (evtSrc instanceof Reaction){
				Reaction reac = (Reaction) evtSrc;
				libDoc.getModel().getReaction(reac.getId()).setCompartment(reac.getCompartment());
			} 
		} else if (prop.equals(TreeNodeChangeEvent.compartmentType)){
			if (evtSrc instanceof Compartment){
				Compartment comp = (Compartment) evtSrc;
				libDoc.getModel().getCompartment(comp.getId()).setCompartmentType(comp.getCompartmentType());
			}
		} else if (prop.equals(TreeNodeChangeEvent.constant)){
			if (evtSrc instanceof Species){
				Species spec = (Species) evtSrc;
				libDoc.getModel().getSpecies(spec.getId()).setConstant(spec.getConstant());
			} else if (evtSrc instanceof Parameter){
				Parameter param = (Parameter) evtSrc;
				libDoc.getModel().getParameter(param.getId()).setConstant(param.getConstant());
			} else if (evtSrc instanceof SpeciesReference){
				SpeciesReference specRef = (SpeciesReference) evtSrc;
				libDoc.getModel().getSpeciesReference(specRef.getId()).setConstant(specRef.getConstant());
			}
			// ...
		} else if (prop.equals(TreeNodeChangeEvent.conversionFactor)){
			if (evtSrc instanceof Species){
				Species spec = (Species) evtSrc;
				libDoc.getModel().getSpecies(spec.getId()).setConversionFactor(spec.getConversionFactor());
			} else if (evtSrc instanceof Model){
				libDoc.getModel().setConversionFactor(((Model) evtSrc).getConversionFactor());
			}
		} else if (prop.equals(TreeNodeChangeEvent.created)){
			// evtSrc is a History-element
			libDoc.getModelHistory().setCreatedDate(convertDate(((History) evtSrc).getCreatedDate()));
		} else if (prop.equals(TreeNodeChangeEvent.creator)){
			// evtSrc is a History-element
			libDoc.getModelHistory().addCreator(convertToModelCreator((Creator)evt.getNewValue()));
		} else if (prop.equals(TreeNodeChangeEvent.definitionURL)){
			ASTNode node = (ASTNode) evtSrc;
			logger.log(Level.DEBUG, String.format("Couldn't find the %s in the libSBML-Document", node.getClass().getSimpleName()));
		} else if (prop.equals(TreeNodeChangeEvent.denominator)){
			if (evtSrc instanceof ASTNode){
				ASTNode node = (ASTNode) evtSrc;
				logger.log(Level.DEBUG, String.format("Couldn't change the %s in the libSBML-Document", node.getClass().getSimpleName()));
			} else if (evtSrc instanceof SpeciesReference){
				SpeciesReference specRef = (SpeciesReference) evtSrc;
				libDoc.getModel().getSpeciesReference(specRef.getId()).setDenominator(specRef.getDenominator());
			}
		} else if (prop.equals(TreeNodeChangeEvent.email)){
			// evtSrc is a Creator
			Creator cr = (Creator) evtSrc;
			libDoc.getModelHistory().getCreator(getCreatorIndex(cr)).setEmail(cr.getEmail());
		} else if (prop.equals(TreeNodeChangeEvent.encoding)){
			ASTNode node = (ASTNode) evtSrc;
			logger.log(Level.DEBUG, String.format("Couldn't change the %s in the libSBML-Document", node.getClass().getSimpleName()));
		} else if (prop.equals(TreeNodeChangeEvent.exponent)){
			ASTNode node = (ASTNode) evtSrc;
			logger.log(Level.DEBUG, String.format("Couldn't change the %s in the libSBML-Document", node.getClass().getSimpleName()));
		} else if (prop.equals(TreeNodeChangeEvent.extentUnits)){
			if (evtSrc instanceof Model){
				libDoc.getModel().setExtentUnits(((Model) evtSrc).getExtentUnits());
			}
		} else if (prop.equals(TreeNodeChangeEvent.familyName)){
			Creator cr = (Creator) evtSrc;
			libDoc.getModelHistory().getCreator(getCreatorIndex(cr)).setFamilyName(cr.getFamilyName());		
		} else if (prop.equals(TreeNodeChangeEvent.fast)){
			if (evtSrc instanceof Reaction){
				Reaction reac = (Reaction) evtSrc;
				libDoc.getModel().getReaction(reac.getId()).setFast(reac.getFast());
			}
		} else if (prop.equals(TreeNodeChangeEvent.formula)){
			if (evtSrc instanceof KineticLaw){
				KineticLaw kinLaw = (KineticLaw) evtSrc;
				Reaction parentKinLaw = kinLaw.getParent();
				libDoc.getModel().getReaction(parentKinLaw.getId()).getKineticLaw().setFormula(kinLaw.getFormula());
			}
		} else if (prop.equals(TreeNodeChangeEvent.givenName)){
			Creator cr = (Creator) evtSrc;
			libDoc.getModelHistory().getCreator(getCreatorIndex(cr)).setGivenName(cr.getGivenName());
		} else if (prop.equals(TreeNodeChangeEvent.hasOnlySubstanceUnits)){
			if (evtSrc instanceof Species){
				Species spec = (Species) evtSrc;
				libDoc.getModel().getSpecies(spec.getId()).setHasOnlySubstanceUnits(spec.getHasOnlySubstanceUnits());
			}
		} else if (prop.equals(TreeNodeChangeEvent.history)){
			// evtSrc is an Annotation-element
		} else if (prop.equals(TreeNodeChangeEvent.id)){
			// search the element in libDoc with the old id and set the new one
			// you don't have to ask here of which instance the evtSrc is 
			if (evt.getOldValue() != null){
				libDoc.getElementBySId((String)evt.getOldValue()).setId((String) evt.getNewValue());
			} else {
				// then the id was not set, but how to find then the element in libDoc?
				// TODO: find the element in libDoc
			}
		} else if (prop.equals(TreeNodeChangeEvent.initialAmount)){
			//evtSrc is a Species
			Species spec = (Species) evtSrc;
			libDoc.getModel().getSpecies(spec.getId()).setInitialAmount(spec.getInitialAmount());
		} else if (prop.equals(TreeNodeChangeEvent.initialValue)){
			if (evtSrc instanceof Trigger){
				Trigger trig = (Trigger) evtSrc;
				Event parentTrig = (Event) trig.getParent();
				libDoc.getModel().getEvent(parentTrig.getId()).getTrigger().setInitialValue(trig.getInitialValue());
			}
		} else if (prop.equals(TreeNodeChangeEvent.isEOF)){
			if (evtSrc instanceof XMLToken){
				XMLToken token = (XMLToken) evtSrc;
				logger.log(Level.DEBUG, String.format("Couldn't change the %s in the libSBML-Document", token.getClass().getSimpleName()));
			}
		} else if (prop.equals(TreeNodeChangeEvent.isExplicitlySetConstant)){
			//TODO
		} else if (prop.equals(TreeNodeChangeEvent.isSetNumberType)){
			//TODO
		} else if (prop.equals(TreeNodeChangeEvent.kind)){
			if (evtSrc instanceof Unit){
				Unit u = (Unit) evtSrc;
				UnitDefinition udef = (UnitDefinition) u.getParent().getParent();
				transferKindProperties(u, libDoc.getModel().getUnitDefinition(udef.getId()).getUnit(getUnitIndex(u, udef)));
			}
		} else if (prop.equals(TreeNodeChangeEvent.lengthUnits)){
			if (evtSrc instanceof Model){
				libDoc.getModel().setLengthUnits(((Model) evtSrc).getLengthUnits());
			}
		} else if (prop.equals(TreeNodeChangeEvent.level)){
			libDoc.setLevelAndVersion(((AbstractSBase) evtSrc).getLevel(), ((AbstractSBase) evtSrc).getVersion());
		} else if (prop.equals(TreeNodeChangeEvent.mantissa)){
			ASTNode node = (ASTNode) evtSrc;
			logger.log(Level.DEBUG, String.format("Couldn't change the %s in the libSBML-Document", node.getClass().getSimpleName()));
		} else if (prop.equals(TreeNodeChangeEvent.math)) {
			MathContainer mathContainer = (MathContainer) evt.getSource();
			if (mathContainer instanceof KineticLaw) {
				Reaction r = ((KineticLaw) mathContainer).getParent();
				org.sbml.libsbml.KineticLaw libKl = libDoc.getModel().getReaction(r.getId()).getKineticLaw();
				libKl.setFormula(mathContainer.getFormula());
			} else if (mathContainer instanceof Constraint) {
				Constraint con = (Constraint) mathContainer;
				int index = getContraintIndex(con);
				libDoc.getModel().getConstraint(index).setMath(convertASTNode(mathContainer.getMath()));
			} else if (mathContainer instanceof Delay){
				Delay delay = (Delay) mathContainer;
				libDoc.getModel().getEvent(delay.getParent().getId()).getDelay().setMath(convertASTNode(mathContainer.getMath()));
			} else if (mathContainer instanceof FunctionDefinition){
				FunctionDefinition funcDef = (FunctionDefinition) mathContainer;
				libDoc.getModel().getFunctionDefinition(funcDef.getId()).setMath(convertASTNode(mathContainer.getMath()));
			} else if (mathContainer instanceof StoichiometryMath){
				StoichiometryMath sto = (StoichiometryMath) mathContainer;
				libDoc.getModel().getSpeciesReference(sto.getParent().getId()).getStoichiometryMath().setMath(convertASTNode(mathContainer.getMath()));
			} else if (mathContainer instanceof InitialAssignment){
				InitialAssignment initAssign = (InitialAssignment) mathContainer;
				libDoc.getModel().getInitialAssignment(initAssign.getVariable()).setMath(convertASTNode(mathContainer.getMath()));
			} else if (mathContainer instanceof Priority){
				Priority prio = (Priority) mathContainer;
				libDoc.getModel().getEvent(prio.getParent().getId()).getPriority().setMath(convertASTNode(mathContainer.getMath()));
			} else if (mathContainer instanceof Trigger){
				Trigger trig = (Trigger) mathContainer;
				libDoc.getModel().getEvent(trig.getParent().getId()).getTrigger().setMath(convertASTNode(mathContainer.getMath()));
			}
		} else if (prop.equals(TreeNodeChangeEvent.message)){
			if (evtSrc instanceof Constraint){
				Constraint con = (Constraint) evtSrc;
				int index = getContraintIndex(con);
				org.sbml.libsbml.XMLNode xml = new XMLNode(con.getMessageString());
				libDoc.getModel().getConstraint(index).setMessage(xml);
			}
		} else if (prop.equals(TreeNodeChangeEvent.metaId)){
			//TODO: same case like the "unsetCVTerms"-case
		} else if (prop.equals(TreeNodeChangeEvent.model)){
			org.sbml.jsbml.SBMLDocument doc = (org.sbml.jsbml.SBMLDocument) evtSrc;
			LibSBMLWriter write = new LibSBMLWriter();
			libDoc.setModel((org.sbml.libsbml.Model) write.writeModel(doc.getModel()));
		} else if (prop.equals(TreeNodeChangeEvent.modified)){
			libDoc.getModelHistory().setModifiedDate(convertDate(((History)evtSrc).getModifiedDate()));
		} else if (prop.equals(TreeNodeChangeEvent.multiplier)){
			if (evtSrc instanceof Unit){
				Unit u = (Unit) evtSrc;
				UnitDefinition udef = (UnitDefinition) u.getParent().getParent();
				libDoc.getModel().getUnitDefinition(udef.getId()).getUnit(getUnitIndex(u, udef)).setMultiplier(u.getMultiplier());
			}
		} else if (prop.equals(TreeNodeChangeEvent.name)){
			// evtSrc must be a type of namedSBase
			NamedSBase nsb = (NamedSBase) evtSrc;
			libDoc.getElementBySId(nsb.getId()).setName(nsb.getName());
		} else if (prop.equals(TreeNodeChangeEvent.namespace)){
			//TODO: same case like the "unsetCVTerms"-case
		} else if (prop.equals(TreeNodeChangeEvent.nonRDFAnnotation)){
			//evtSrc is an Annotation-element
			//TODO
		} else if (prop.equals(TreeNodeChangeEvent.notes)){
			//TODO: same case like the "unsetCVTerms"-case
		} else if (prop.equals(TreeNodeChangeEvent.numerator)){
			ASTNode node = (ASTNode) evtSrc;
			logger.log(Level.DEBUG, String.format("Couldn't change the %s in the libSBML-Document", node.getClass().getSimpleName()));
		} else if (prop.equals(TreeNodeChangeEvent.offset)){
			if (evtSrc instanceof Unit){
				Unit u = (Unit) evtSrc;
				UnitDefinition udef = (UnitDefinition) u.getParent().getParent();
				libDoc.getModel().getUnitDefinition(udef.getId()).getUnit(getUnitIndex(u, udef)).setOffset(u.getOffset());
			}
		} else if (prop.equals(TreeNodeChangeEvent.organisation)){
			Creator cr = (Creator) evtSrc;
			libDoc.getModelHistory().getCreator(getCreatorIndex(cr)).setOrganisation(cr.getOrganisation());
		} else if (prop.equals(TreeNodeChangeEvent.outside)){
			if (evtSrc instanceof Compartment){
				Compartment comp = (Compartment) evtSrc;
				libDoc.getModel().getCompartment(comp.getId()).setOutside(comp.getOutside());
			}
		} else if (prop.equals(TreeNodeChangeEvent.parentSBMLObject)){
			//TODO
			if (evtSrc instanceof ASTNode){
				ASTNode node = (ASTNode) evtSrc;
				logger.log(Level.DEBUG, String.format("Couldn't change the %s in the libSBML-Document", node.getClass().getSimpleName()));
			}
		} else if (prop.equals(TreeNodeChangeEvent.persistent)){
			if (evtSrc instanceof Trigger){
				Trigger trig = (Trigger) evtSrc;
				Event parentTrig = (Event) trig.getParent();
				libDoc.getModel().getEvent(parentTrig.getId()).getTrigger().setPersistent(trig.getPersistent());
			}
		} else if (prop.equals(TreeNodeChangeEvent.priority)){
			if (evtSrc instanceof Event){
				Event event = (Event) evtSrc;
				org.sbml.libsbml.Priority prio = new org.sbml.libsbml.Priority(event.getPriority().getLevel(), event.getPriority().getVersion());
				transferMathContainerProperties(event.getPriority(), prio);
				libDoc.getModel().getEvent(event.getId()).setPriority(prio);
			}
			// what does this case? isn't it already checked with nodeRemoved() or nodeAdded()?
		} else if (prop.equals(TreeNodeChangeEvent.qualifier)){
			//TODO: CVTerm
		} else if (prop.equals(TreeNodeChangeEvent.rdfAnnotationNamespaces)){
			// evtSrc is an Annotation-element
			//TODO
		} else if (prop.equals(TreeNodeChangeEvent.reversible)){
			Reaction reac= (Reaction) evtSrc;
			libDoc.getModel().getReaction(reac.getId()).setReversible(reac.getReversible());
		} else if (prop.equals(TreeNodeChangeEvent.SBMLDocumentAttributes)){
			//TODO: SBMLDocument
		} else if (prop.equals(TreeNodeChangeEvent.sboTerm)){
			//TODO: same case like the "unsetCVTerms"-case			
		} else if (prop.equals(TreeNodeChangeEvent.scale)){
			if (evtSrc instanceof Unit){
				Unit u = (Unit) evtSrc;
				UnitDefinition udef = (UnitDefinition) u.getParent().getParent();
				libDoc.getModel().getUnitDefinition(udef.getId()).getUnit(getUnitIndex(u, udef)).setScale(u.getScale());
			}
		} else if (prop.equals(TreeNodeChangeEvent.setAnnotation)){
			//TODO: every SBase has this case
		} else if (prop.equals(TreeNodeChangeEvent.size)){
			if (evtSrc instanceof Compartment){
				Compartment comp = (Compartment) evtSrc;
				libDoc.getModel().getCompartment(comp.getId()).setSize(comp.getSize());
			}
		} else if (prop.equals(TreeNodeChangeEvent.spatialDimensions)){
			if (evtSrc instanceof Compartment){
				Compartment comp = (Compartment) evtSrc;
				libDoc.getModel().getCompartment(comp.getId()).setSpatialDimensions(comp.getSpatialDimensions());
			}
		} else if (prop.equals(TreeNodeChangeEvent.spatialSizeUnits)){
			if (evtSrc instanceof Species){
				Species spec = (Species) evtSrc;
				libDoc.getModel().getSpecies(spec.getId()).setSpatialSizeUnits(spec.getSpatialSizeUnits());
			}
		} else if (prop.equals(TreeNodeChangeEvent.species)){
			if (evtSrc instanceof SimpleSpeciesReference){
				SimpleSpeciesReference ref = (SimpleSpeciesReference) evtSrc;
				libDoc.getModel().getSpeciesReference(ref.getId()).setSpecies(ref.getSpecies());
			}
		} else if (prop.equals(TreeNodeChangeEvent.speciesType)){
			if (evtSrc instanceof Species){
				Species spec = (Species) evtSrc;
				libDoc.getModel().getSpecies(spec.getId()).setSpeciesType(spec.getSpeciesType());
			}
		} else if (prop.equals(TreeNodeChangeEvent.stoichiometry)){
			SpeciesReference specRef = (SpeciesReference) evtSrc;
			libDoc.getModel().getSpeciesReference(specRef.getId()).setStoichiometry(specRef.getStoichiometry());
		} else if (prop.equals(TreeNodeChangeEvent.style)){
			ASTNode node = (ASTNode) evtSrc;
			logger.log(Level.DEBUG, String.format("Couldn't change the %s in the libSBML-Document", node.getClass().getSimpleName()));
		} else if (prop.equals(TreeNodeChangeEvent.substanceUnits)){
			if (evtSrc instanceof Species){
				Species spec = (Species) evtSrc;
				libDoc.getModel().getSpecies(spec.getId()).setSubstanceUnits(spec.getSubstanceUnits());
			} else if (evtSrc instanceof KineticLaw){
				KineticLaw kinLaw = (KineticLaw) evtSrc;
				Reaction parentKinLaw = (Reaction) kinLaw.getParent();
				libDoc.getModel().getReaction(parentKinLaw.getId()).getKineticLaw().setSubstanceUnits(kinLaw.getSubstanceUnits());
			} else if (evtSrc instanceof Model){
				libDoc.getModel().setSubstanceUnits(((Model) evtSrc).getSubstanceUnits());
			}
		} else if (prop.equals(TreeNodeChangeEvent.symbol)){
			if(evtSrc instanceof InitialAssignment){
				libDoc.getModel().getInitialAssignment(((InitialAssignment) evtSrc).getVariable()).setSymbol(((InitialAssignment) evtSrc).getSymbol());
			}
		} else if (prop.equals(TreeNodeChangeEvent.text)){
			ASTNode node = (ASTNode) evtSrc;
			logger.log(Level.DEBUG, String.format("Couldn't change the %s in the libSBML-Document", node.getClass().getSimpleName()));
		} else if (prop.equals(TreeNodeChangeEvent.timeUnits)){
			if (evtSrc instanceof Event){
				Event event = (Event) evtSrc;
				libDoc.getModel().getEvent(event.getId()).setTimeUnits(event.getTimeUnits());
			} else if (evtSrc instanceof KineticLaw){
				KineticLaw kinLaw = (KineticLaw) evtSrc;
				Reaction parentKinLaw = (Reaction) kinLaw.getParent();
				libDoc.getModel().getReaction(parentKinLaw.getId()).getKineticLaw().setTimeUnits(kinLaw.getTimeUnits());
			} else if (evtSrc instanceof Model){
				libDoc.getModel().setTimeUnits(((Model) evtSrc).getTimeUnits());
			}
		} else if (prop.equals(TreeNodeChangeEvent.type)){
			ASTNode node = (ASTNode) evtSrc;
			logger.log(Level.DEBUG, String.format("Couldn't change the %s in the libSBML-Document", node.getClass().getSimpleName()));
		} else if (prop.equals(TreeNodeChangeEvent.units)){
			if (evtSrc instanceof Species){
				Species spec = (Species) evtSrc;
				libDoc.getModel().getSpecies(spec.getId()).setUnits(spec.getUnits());
			} else if (evtSrc instanceof Compartment){
				Compartment comp = (Compartment) evtSrc;
				libDoc.getModel().getCompartment(comp.getId()).setUnits(comp.getUnits());
			} else if (evtSrc instanceof Parameter){
				Parameter param = (Parameter) evtSrc;
				libDoc.getModel().getParameter(param.getId()).setUnits(param.getUnits());
			} else if (evtSrc instanceof Rule){
				Rule rule = (Rule) evtSrc;
				if (rule instanceof RateRule){
					RateRule rrule = (RateRule) rule;
					libDoc.getModel().getRule(rrule.getVariable()).setUnits(rrule.getUnits());
				} else if (rule instanceof AlgebraicRule){
					AlgebraicRule algRule = (AlgebraicRule) rule;
					//TODO
				} else if (rule instanceof AssignmentRule){
					AssignmentRule assRule = (AssignmentRule) rule;
					libDoc.getModel().getRule(assRule.getVariable()).setUnits(assRule.getUnits());
				}
			}
		} else if (prop.equals(TreeNodeChangeEvent.unsetCVTerms)){
			SBase sb = (SBase) evtSrc;
			//TODO: all SBases can unset the CVTerms...
		} else if (prop.equals(TreeNodeChangeEvent.userObject)){
			ASTNode node = (ASTNode) evtSrc;
			logger.log(Level.DEBUG, String.format("Couldn't change the %s in the libSBML-Document", node.getClass().getSimpleName()));
		} else if (prop.equals(TreeNodeChangeEvent.useValuesFromTriggerTime)){
			Event event = (Event) evtSrc;
			libDoc.getModel().getEvent(event.getId()).setUseValuesFromTriggerTime(event.getUseValuesFromTriggerTime());
		} else if (prop.equals(TreeNodeChangeEvent.value)){
			if (evtSrc instanceof Parameter){
				Parameter param = (Parameter) evtSrc;
				libDoc.getModel().getParameter(param.getId()).setValue(param.getValue());
			}
		} else if (prop.equals(TreeNodeChangeEvent.variable)){
			if (evtSrc instanceof EventAssignment){
				EventAssignment evAssign = (EventAssignment) evtSrc;
				Event parentEvAssign = (Event) evAssign.getParent().getParent();
				libDoc.getModel().getEvent(parentEvAssign.getId()).getEventAssignment((String)evt.getOldValue()).setVariable(evAssign.getVariable());
			} else if (evtSrc instanceof Rule){
				Rule rule = (Rule) evtSrc;
				if (rule instanceof RateRule){
					RateRule rrule = (RateRule) rule;
					libDoc.getModel().getRule((String)evt.getOldValue()).setVariable(rrule.getVariable());
				} else if (rule instanceof AlgebraicRule){
					AlgebraicRule algRule = (AlgebraicRule) rule;
					//TODO
				} else if (rule instanceof AssignmentRule){
					AssignmentRule assRule = (AssignmentRule) rule;
					libDoc.getModel().getRule((String)evt.getOldValue()).setVariable(assRule.getVariable());
				}
			}
		} else if (prop.equals(TreeNodeChangeEvent.version)){
			libDoc.setLevelAndVersion(((AbstractSBase) evtSrc).getLevel(), ((AbstractSBase) evtSrc).getVersion());
		} else if (prop.equals(TreeNodeChangeEvent.volume)){
			if (evtSrc instanceof Compartment){
				Compartment comp = (Compartment) evtSrc;
				libDoc.getModel().getCompartment(comp.getId()).setVolume(comp.getVolume());
			}
		} else if (prop.equals(TreeNodeChangeEvent.volumeUnits)){
			if (evtSrc instanceof Model){
				libDoc.getModel().setVolumeUnits(((Model) evtSrc).getVolumeUnits());
			}
		} else if (prop.equals(TreeNodeChangeEvent.xmlTriple)){
			XMLToken token = (XMLToken) evtSrc;
			logger.log(Level.DEBUG, String.format("Couldn't change the %s in the libSBML-Document", token.getClass().getSimpleName()));
		}
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
	 * @param unit
	 * @param u
	 */
	private int transferKindProperties(Unit unit, org.sbml.libsbml.Unit u) {
		switch (unit.getKind()) {
		case AMPERE:
			u.setKind(libsbmlConstants.UNIT_KIND_AMPERE);
			break;
		case BECQUEREL:
			u.setKind(libsbmlConstants.UNIT_KIND_BECQUEREL);
			break;
		case CANDELA:
			u.setKind(libsbmlConstants.UNIT_KIND_CANDELA);
			break;
		case CELSIUS:
			u.setKind(libsbmlConstants.UNIT_KIND_CELSIUS);
			break;
		case COULOMB:
			u.setKind(libsbmlConstants.UNIT_KIND_COULOMB);
			break;
		case DIMENSIONLESS:
			u.setKind(libsbmlConstants.UNIT_KIND_DIMENSIONLESS);
			break;
		case FARAD:
			u.setKind(libsbmlConstants.UNIT_KIND_FARAD);
			break;
		case GRAM:
			u.setKind(libsbmlConstants.UNIT_KIND_GRAM);
			break;
		case GRAY:
			u.setKind(libsbmlConstants.UNIT_KIND_GRAY);
			break;
		case HENRY:
			u.setKind(libsbmlConstants.UNIT_KIND_HENRY);
			break;
		case HERTZ:
			u.setKind(libsbmlConstants.UNIT_KIND_HERTZ);
			break;
		case INVALID:
			u.setKind(libsbmlConstants.UNIT_KIND_INVALID);
			break;
		case ITEM:
			u.setKind(libsbmlConstants.UNIT_KIND_ITEM);
			break;
		case JOULE:
			u.setKind(libsbmlConstants.UNIT_KIND_JOULE);
			break;
		case KATAL:
			u.setKind(libsbmlConstants.UNIT_KIND_KATAL);
			break;
		case KELVIN:
			u.setKind(libsbmlConstants.UNIT_KIND_KELVIN);
			break;
		case KILOGRAM:
			u.setKind(libsbmlConstants.UNIT_KIND_KILOGRAM);
			break;
		case LITER:
			u.setKind(libsbmlConstants.UNIT_KIND_LITER);
			break;
		case LITRE:
			u.setKind(libsbmlConstants.UNIT_KIND_LITRE);
			break;
		case LUMEN:
			u.setKind(libsbmlConstants.UNIT_KIND_LUMEN);
			break;
		case LUX:
			u.setKind(libsbmlConstants.UNIT_KIND_LUX);
			break;
		case METER:
			u.setKind(libsbmlConstants.UNIT_KIND_METER);
			break;
		case METRE:
			u.setKind(libsbmlConstants.UNIT_KIND_METRE);
			break;
		case MOLE:
			u.setKind(libsbmlConstants.UNIT_KIND_MOLE);
			break;
		case NEWTON:
			u.setKind(libsbmlConstants.UNIT_KIND_NEWTON);
			break;
		case OHM:
			u.setKind(libsbmlConstants.UNIT_KIND_OHM);
			break;
		case PASCAL:
			u.setKind(libsbmlConstants.UNIT_KIND_PASCAL);
			break;
		case RADIAN:
			u.setKind(libsbmlConstants.UNIT_KIND_RADIAN);
			break;
		case SECOND:
			u.setKind(libsbmlConstants.UNIT_KIND_SECOND);
			break;
		case SIEMENS:
			u.setKind(libsbmlConstants.UNIT_KIND_SIEMENS);
			break;
		case SIEVERT:
			u.setKind(libsbmlConstants.UNIT_KIND_SIEVERT);
			break;
		case STERADIAN:
			u.setKind(libsbmlConstants.UNIT_KIND_STERADIAN);
			break;
		case TESLA:
			u.setKind(libsbmlConstants.UNIT_KIND_TESLA);
			break;
		case VOLT:
			u.setKind(libsbmlConstants.UNIT_KIND_VOLT);
			break;
		case WATT:
			u.setKind(libsbmlConstants.UNIT_KIND_WATT);
			break;
		case WEBER:
			u.setKind(libsbmlConstants.UNIT_KIND_WEBER);
			break;
		}
		return u.getKind();
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

	/**
	 * searches the index of the incoming unit in the ListOfUnits of the UnitDefinition
	 * @param unit
	 * @param udef
	 * @return
	 */
	private int getUnitIndex(Unit unit, UnitDefinition udef) {
		int index = 0;
		for (int k=0; k<udef.getListOfUnits().size();k++){
			Unit u = udef.getUnit(k);
			if(u.equals(unit)){
				index = k;
				break;
			}
		}
		return index;
	}
	
	/**
	 * 
	 * @param con
	 * @return
	 */
	private int getContraintIndex(Constraint con) {
		int index = 0;
		for (int k=0; k<doc.getModel().getListOfConstraints().size(); k++){
			Constraint c = doc.getModel().getConstraint(k);
			if(c.equals(con)){
				index = k;
				break;
			}
		}
		return index;
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	private ModelCreator convertToModelCreator(Creator c) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param createdDate
	 * @return
	 */
	private org.sbml.libsbml.Date convertDate(Date createdDate) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param creator
	 * @return
	 */
	private long getCreatorIndex(Creator creator) {
		int index = 0;
		for (int k=0; k<doc.getHistory().getNumCreators(); k++){
			Creator c = doc.getHistory().getCreator(k);
			if(c.equals(creator)){
				index = k;
				break;
			}
		}
		return index;
	}

	/**
	 * 
	 * @param newValue
	 * @return
	 */
	private org.sbml.libsbml.CVTerm convertCVTerm(CVTerm newValue) {
		// TODO Auto-generated method stub
		return null;
	}

}
