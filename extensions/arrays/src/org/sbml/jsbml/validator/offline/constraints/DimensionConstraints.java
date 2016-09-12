/*
 * 
 * ----------------------------------------------------------------------------
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML>
 * for the latest version of JSBML and more information about SBML.
 * Copyright (C) 2009-2016 jointly by the following organizations:
 * 1. The University of Tuebingen, Germany
 * 2. EMBL European Bioinformatics Institute (EBML-EBI), Hinxton, UK
 * 3. The California Institute of Technology, Pasadena, CA, USA
 * 4. The University of California, San Diego, La Jolla, CA, USA
 * 5. The Babraham Institute, Cambridge, UK
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation. A copy of the license agreement is provided
 * in the file named "LICENSE.txt" included with this software distribution
 * and also available online as <http://sbml.org/Software/JSBML/License>.
 * ----------------------------------------------------------------------------
 */
package org.sbml.jsbml.validator.offline.constraints;

import java.util.Set;

import org.sbml.jsbml.ext.arrays.Dimension;
import org.sbml.jsbml.validator.SBMLValidator.CHECK_CATEGORY;
import org.sbml.jsbml.validator.offline.ValidationContext;

/**
 * @author rodrigue
 *
 */
public class DimensionConstraints extends AbstractConstraintDeclaration {

  @Override
  public void addErrorCodesForCheck(Set<Integer> set, int level, int version,
      CHECK_CATEGORY category) 
  {
    switch (category) {
    case GENERAL_CONSISTENCY:
      break;
    case IDENTIFIER_CONSISTENCY:
      break;
    case MATHML_CONSISTENCY:
      break;
    case MODELING_PRACTICE:
      break;
    case OVERDETERMINED_MODEL:
      break;
    case SBO_CONSISTENCY:
      break;
    case UNITS_CONSISTENCY:
      break;
    }  
  }

  @Override
  public void addErrorCodesForAttribute(Set<Integer> set, int level,
      int version, String attributeName) {
    // TODO Auto-generated method stub
    
  }

  @Override
  ValidationFunction<?> getValidationFunction(int errorCode) {
    ValidationFunction<Dimension> func = null;

    switch (errorCode) {

    case ARRAYS_20201:

      func = new ValidationFunction<Dimension>() {
        @Override
        public boolean check(ValidationContext ctx, Dimension c) {

          // TODO - can just use the UnknownAttributeValidationFunction<Dimension> method 
          
          return true;
        }
      };
      break;
    }

    return func;
  }  

}
