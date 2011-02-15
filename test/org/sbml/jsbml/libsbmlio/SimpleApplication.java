/*
 * $Id: FormulaTest.java 102 2009-12-13 19:52:50Z andreas-draeger $
 * $URL: https://jsbml.svn.sourceforge.net/svnroot/jsbml/trunk/src/org/sbml/jsbml/io/FormulaTest.java $
 * ----------------------------------------------------------------------------
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML>
 * for the latest version of JSBML and more information about SBML.
 *
 * Copyright (C) 2009-2011 jointly by the following organizations:
 * 1. The University of Tuebingen, Germany
 * 2. EMBL European Bioinformatics Institute (EBML-EBI), Hinxton, UK
 * 3. The California Institute of Technology, Pasadena, CA, USA
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation. A copy of the license agreement is provided
 * in the file named "LICENSE.txt" included with this software distribution
 * and also available online as <http://sbml.org/Software/JSBML/License>.
 * ----------------------------------------------------------------------------
 */
package org.sbml.jsbml.libsbmlio;

import org.sbml.gui.JSBMLvisualizer;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.xml.libsbml.LibSBMLReader;

/**
 * @author Andreas Dr&auml;ger
 * @date 2011-02-03
 */
public class SimpleApplication {

	/**
	 * @param args the path to a valid SBML file.
	 */
	public static void main(String[] args) {
		try {
			// Load LibSBML:
			System.loadLibrary("sbmlj");
			// Extra check to be sure we have access to libSBML:
			Class.forName("org.sbml.libsbml.libsbml");

			// Read SBML file using LibSBML and convert it to JSBML:
			LibSBMLReader reader = new LibSBMLReader();
			SBMLDocument doc = reader.convertSBMLDocument(args[0]);
			
			// Run some application:
			new JSBMLvisualizer(doc);
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
