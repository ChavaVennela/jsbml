/*
 * $Id: FormulaTest.java 102 2009-12-13 19:52:50Z andreas-draeger $
 * $URL: https://jsbml.svn.sourceforge.net/svnroot/jsbml/trunk/src/org/sbml/jsbml/io/FormulaTest.java $
 *
 * 
 *==================================================================================
 * Copyright (c) 2009 The jsbml team.
 *
 * This file is part of jsbml, the pure java SBML library. Please visit
 * http://sbml.org for more information about SBML, and http://jsbml.sourceforge.net/
 * to get the latest version of jsbml.
 *
 * jsbml is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * jsbml is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jsbml.  If not, see <http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html>.
 *
 *===================================================================================
 *
 */
package org.sbml.jsbml.test;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.xml.libsbml.LibSBMLReader;
import org.sbml.jsbml.xml.stax.SBMLReader;

/**
 * @author Andreas Dr&auml;ger
 * @date 2010-12-01
 */
public class FormulaTest {

	static {
		try {
			System.loadLibrary("sbmlj");
			// Extra check to be sure we have access to libSBML:
			Class.forName("org.sbml.libsbml.libsbml");
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * 
	 * @param args just the path to one SBML file.
	 * @throws XMLStreamException 
	 * @throws IOException 
	 */
	public static void main(String args[]) throws XMLStreamException, IOException {
		org.sbml.libsbml.SBMLReader libReader = new org.sbml.libsbml.SBMLReader(); 
		org.sbml.libsbml.SBMLDocument libDoc = libReader.readSBML(args[0]);
		LibSBMLReader libTranslator = new LibSBMLReader();
		SBMLDocument doc1 = SBMLReader.readSBML(args[0]);
		SBMLDocument doc2 = libTranslator.convertSBMLDocument(libDoc);
		System.out.println(doc1.equals(doc2));
	}
	
}
