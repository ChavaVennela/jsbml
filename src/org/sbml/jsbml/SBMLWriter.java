/*
 * $Id: SBMLWriter.java 102 2009-12-13 19:52:50Z andreas-draeger $
 * $URL: https://jsbml.svn.sourceforge.net/svnroot/jsbml/trunk/src/org/sbml/jsbml/SBMLWriter.java $
 *
 *
 *==================================================================================
 * Copyright (c) 2009 the copyright is held jointly by the individual
 * authors. See the file AUTHORS for the list of authors.
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

package org.sbml.jsbml;

import java.io.IOException;
import java.util.List;

import org.sbml.jsbml.io.IOProgressListener;

/**
 * @author Andreas Dr&auml;ger <a
 *         href="mailto:andreas.draeger@uni-tuebingen.de">
 *         andreas.draeger@uni-tuebingen.de</a>
 * 
 */
public interface SBMLWriter {

	/**
	 * 
	 * @return
	 */
	public int getNumErrors(Object sbase);

	/**
	 * 
	 * @param sbase
	 * @return
	 */
	public List<SBMLException> getWriteWarnings(Object sbase);

	/**
	 * Deletes those elements that are not referenced or not needed within the
	 * model.
	 * 
	 * @param model
	 * @param orig
	 */
	public void removeUnneccessaryElements(Model model, Object orig);

	/**
	 * Save the changes in the model.
	 * 
	 * @param model
	 * @param object
	 * @throws SBMLException
	 */
	public void saveChanges(Model model, Object object) throws SBMLException;

	/**
	 * Save all changes that belong to one particular reaction in the model.
	 * 
	 * @param reaction
	 * @param model
	 * @throws SBMLException
	 */
	public void saveChanges(Reaction reaction, Object model)
			throws SBMLException;

	/**
	 * 
	 * @param model
	 * @return
	 * @throws SBMLException
	 */
	public Object writeModel(Model model) throws SBMLException;

	/**
	 * 
	 * @param sbmlDocument
	 * @param filename
	 * @return
	 * @throws SBMLException
	 * @throws IOException
	 */
	public boolean writeSBML(Object sbmlDocument, String filename)
			throws SBMLException, IOException;

	/**
	 * 
	 * @param object
	 * @param filename
	 * @param programName
	 * @param versionNumber
	 * @return
	 * @throws SBMLException
	 * @throws IOException
	 */
	public boolean writeSBML(Object object, String filename,
			String programName, String versionNumber) throws SBMLException,
			IOException;

	/**
	 * Allows this class to fire events through this event Listener.
	 * 
	 * @param listener
	 */
	public void addIOProgressListener(IOProgressListener listener);
}
