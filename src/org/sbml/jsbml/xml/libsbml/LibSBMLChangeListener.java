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

import org.sbml.jsbml.CompartmentType;
import org.sbml.jsbml.Constraint;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.MathContainer;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Species;
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
		if (node instanceof CompartmentType) {
			CompartmentType ct = (CompartmentType) node;
			org.sbml.libsbml.CompartmentType libCt = libDoc.getModel().createCompartmentType();
			if (ct.isSetId()) {
				libCt.setId(ct.getId());
			}
			if (ct.isSetName()) {
				libCt.setName(ct.getName());
			}
			// TODO: setSBOTerm etc.
		}
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sbml.jsbml.util.TreeNodeChangeListener#nodeRemoved(javax.swing.tree.TreeNode)
	 */
	public void nodeRemoved(TreeNode node) {
		org.sbml.libsbml.Model libModel = libDoc.getModel();
		if (node instanceof CompartmentType) {
			CompartmentType ct = (CompartmentType) node;
			org.sbml.libsbml.CompartmentType pt = libModel
					.getCompartmentType(ct.getId());
			libModel.removeCompartmentType(ct.getId());
		}
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@SuppressWarnings("deprecation")
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
