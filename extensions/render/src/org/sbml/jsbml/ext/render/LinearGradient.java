/*
 * $Id$
 * $URL$
 *
 * ---------------------------------------------------------------------------- 
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML> 
 * for the latest version of JSBML and more information about SBML. 
 * 
 * Copyright (C) 2009-2013 jointly by the following organizations: 
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
package org.sbml.jsbml.ext.render;

import java.text.MessageFormat;
import java.util.Map;

import org.sbml.jsbml.PropertyUndefinedError;
import org.sbml.jsbml.SBase;


/**
 * @author Eugen Netz
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Jan Rudolph
 * @version $Rev$
 * @since 1.0
 * @date 08.05.2012
 */
public class LinearGradient extends GradientBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = 825789189989621388L;
	private Double x1, y1, z1, x2, y2, z2;
	
	/**
	 * Creates an LinearGradient instance 
	 */
	public LinearGradient() {
		super();
		initDefaults();
	}

	/**
	 * Clone constructor
	 */
	public LinearGradient(LinearGradient obj) {
		super(obj);
		x1 = obj.x1;
		x2 = obj.x2;
		y1 = obj.y1;
		y2 = obj.y2;
		z1 = obj.z1;
		z2 = obj.z2;
	}

	/* (non-Javadoc)
	 * @see org.sbml.jsbml.ext.render.GradientBase#clone()
	 */
	@Override
	public LinearGradient clone() {
		return new LinearGradient(this);
	}

	/* (non-Javadoc)
	 * @see org.sbml.jsbml.ext.render.GradientBase#initDefaults()
	 */
	@Override
	public void initDefaults() {
		addNamespace(RenderConstants.namespaceURI);
		x1 = 0d;
		y1 = 0d;
		z1 = 0d;
		x2 = 1d;
		y2 = 1d;
		z2 = 1d;
	}

	/* (non-Javadoc)
	 * @see org.sbml.jsbml.ext.render.GradientBase#getAllowsChildren()
	 */
	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sbml.jsbml.ext.render.GradientBase#getChildCount()
	 */
	@Override
	public int getChildCount() {
		int count = 0;
		return count;
	}

	/* (non-Javadoc)
	 * @see org.sbml.jsbml.ext.render.GradientBase#getChildAt(int)
	 */
	@Override
	public SBase getChildAt(int childIndex) {
		if (childIndex < 0) {
			throw new IndexOutOfBoundsException(childIndex + " < 0");
		}
		int pos = 0;
		throw new IndexOutOfBoundsException(MessageFormat.format(
				"Index {0,number,integer} >= {1,number,integer}", childIndex,
				+((int) Math.min(pos, 0))));
	}
	
	/**
	 * @return the value of x1
	 */
	public double getX1() {
		if (isSetX1()) {
			return x1;
		}
		// This is necessary if we cannot return null here.
		throw new PropertyUndefinedError(RenderConstants.x1, this);
	}

	/**
	 * @return whether x1 is set 
	 */
	public boolean isSetX1() {
		return this.x1 != null;
	}

	/**
	 * Set the value of x1
	 */
	public void setX1(Double x1) {
		Double oldX1 = this.x1;
		this.x1 = x1;
		firePropertyChange(RenderConstants.x1, oldX1, this.x1);
	}

	/**
	 * Unsets the variable x1 
	 * @return {@code true}, if x1 was set before, 
	 *         otherwise {@code false}
	 */
	public boolean unsetX1() {
		if (isSetX1()) {
			Double oldX1 = this.x1;
			this.x1 = null;
			firePropertyChange(RenderConstants.x1, oldX1, this.x1);
			return true;
		}
		return false;
	}

	/**
	 * @return the value of x2
	 */
	public double getX2() {
		if (isSetX2()) {
			return x2;
		}
		// This is necessary if we cannot return null here.
		throw new PropertyUndefinedError(RenderConstants.x2, this);
	}

	/**
	 * @return whether x2 is set 
	 */
	public boolean isSetX2() {
		return this.x2 != null;
	}

	/**
	 * Set the value of x2
	 */
	public void setX2(Double x2) {
		Double oldX2 = this.x2;
		this.x2 = x2;
		firePropertyChange(RenderConstants.x2, oldX2, this.x2);
	}

	/**
	 * Unsets the variable x2 
	 * @return {@code true}, if x2 was set before, 
	 *         otherwise {@code false}
	 */
	public boolean unsetX2() {
		if (isSetX2()) {
			Double oldX2 = this.x2;
			this.x2 = null;
			firePropertyChange(RenderConstants.x2, oldX2, this.x2);
			return true;
		}
		return false;
	}

	/**
	 * @return the value of y1
	 */
	public double getY1() {
		if (isSetY1()) {
			return y1;
		}
		// This is necessary if we cannot return null here.
		throw new PropertyUndefinedError(RenderConstants.y1, this);
	}

	/**
	 * @return whether y1 is set 
	 */
	public boolean isSetY1() {
		return this.y1 != null;
	}

	/**
	 * Set the value of y1
	 */
	public void setY1(Double y1) {
		Double oldY1 = this.y1;
		this.y1 = y1;
		firePropertyChange(RenderConstants.y1, oldY1, this.y1);
	}

	/**
	 * Unsets the variable y1 
	 * @return {@code true}, if y1 was set before, 
	 *         otherwise {@code false}
	 */
	public boolean unsetY1() {
		if (isSetY1()) {
			Double oldY1 = this.y1;
			this.y1 = null;
			firePropertyChange(RenderConstants.y1, oldY1, this.y1);
			return true;
		}
		return false;
	}
	
	/**
	 * @return the value of y2
	 */
	public double getY2() {
		if (isSetY2()) {
			return y2;
		}
		// This is necessary if we cannot return null here.
		throw new PropertyUndefinedError(RenderConstants.y2, this);
	}

	/**
	 * @return whether y2 is set 
	 */
	public boolean isSetY2() {
		return this.y2 != null;
	}

	/**
	 * Set the value of y2
	 */
	public void setY2(Double y2) {
		Double oldY2 = this.y2;
		this.y2 = y2;
		firePropertyChange(RenderConstants.y2, oldY2, this.y2);
	}

	/**
	 * Unsets the variable y2 
	 * @return {@code true}, if y2 was set before, 
	 *         otherwise {@code false}
	 */
	public boolean unsetY2() {
		if (isSetY2()) {
			Double oldY2 = this.y2;
			this.y2 = null;
			firePropertyChange(RenderConstants.y2, oldY2, this.y2);
			return true;
		}
		return false;
	}

	/**
	 * @return the value of z1
	 */
	public double getZ1() {
		if (isSetZ1()) {
			return z1;
		}
		// This is necessary if we cannot return null here.
		throw new PropertyUndefinedError(RenderConstants.z1, this);
	}

	/**
	 * @return whether z1 is set 
	 */
	public boolean isSetZ1() {
		return this.z1 != null;
	}

	/**
	 * Set the value of z1
	 */
	public void setZ1(Double z1) {
		Double oldZ1 = this.z1;
		this.z1 = z1;
		firePropertyChange(RenderConstants.z1, oldZ1, this.z1);
	}

	/**
	 * Unsets the variable z1 
	 * @return {@code true}, if z1 was set before, 
	 *         otherwise {@code false}
	 */
	public boolean unsetZ1() {
		if (isSetZ1()) {
			Double oldZ1 = this.z1;
			this.z1 = null;
			firePropertyChange(RenderConstants.z1, oldZ1, this.z1);
			return true;
		}
		return false;
	}

	/**
	 * @return the value of z2
	 */
	public double getZ2() {
		if (isSetZ2()) {
			return z2;
		}
		// This is necessary if we cannot return null here.
		throw new PropertyUndefinedError(RenderConstants.z2, this);
	}

	/**
	 * @return whether z2 is set 
	 */
	public boolean isSetZ2() {
		return this.z2 != null;
	}

	/**
	 * Set the value of z2
	 */
	public void setZ2(Double z2) {
		Double oldZ2 = this.z2;
		this.z2 = z2;
		firePropertyChange(RenderConstants.z2, oldZ2, this.z2);
	}

	/**
	 * Unsets the variable z2 
	 * @return {@code true}, if z2 was set before, 
	 *         otherwise {@code false}
	 */
	public boolean unsetZ2() {
		if (isSetZ2()) {
			Double oldZ2 = this.z2;
			this.z2 = null;
			firePropertyChange(RenderConstants.z2, oldZ2, this.z2);
			return true;
		}
		return false;
	}

	
  /* (non-Javadoc)
   * @see org.sbml.jsbml.ext.render.GradientBase#writeXMLAttributes()
   */
	@Override
  public Map<String, String> writeXMLAttributes() {
    Map<String, String> attributes = super.writeXMLAttributes();
    if (isSetX1()) {
      attributes.remove(RenderConstants.x1);
      attributes.put(RenderConstants.shortLabel + ":" + RenderConstants.x1,
        XMLTools.positioningToString(getX1(), false));
    }
    if (isSetX2()) {
      attributes.remove(RenderConstants.x2);
      attributes.put(RenderConstants.shortLabel + ":" + RenderConstants.x2,
        XMLTools.positioningToString(getX1(), false));
    }
    if (isSetY1()) {
      attributes.remove(RenderConstants.y1);
      attributes.put(RenderConstants.shortLabel + ":" + RenderConstants.y1,
        XMLTools.positioningToString(getX1(), false));
    }
    if (isSetY2()) {
      attributes.remove(RenderConstants.y2);
      attributes.put(RenderConstants.shortLabel + ":" + RenderConstants.y2,
        XMLTools.positioningToString(getX1(), false));
    }
    if (isSetZ1()) {
      attributes.remove(RenderConstants.z1);
      attributes.put(RenderConstants.shortLabel + ":" + RenderConstants.z1,
        XMLTools.positioningToString(getX1(), false));
    }
    if (isSetZ2()) {
      attributes.remove(RenderConstants.z2);
      attributes.put(RenderConstants.shortLabel + ":" + RenderConstants.z2,
        XMLTools.positioningToString(getX1(), false));
    }
    return attributes;
  }


  /* (non-Javadoc)
   * @see org.sbml.jsbml.ext.render.GradientBase#readAttribute(java.lang.String, java.lang.String, java.lang.String)
   */
	@Override
  public boolean readAttribute(String attributeName, String prefix, String value) {
    boolean isAttributeRead = super.readAttribute(attributeName, prefix, value);
    if (!isAttributeRead) {
      isAttributeRead = true;
      if (attributeName.equals(RenderConstants.x1)) {
        setX1(XMLTools.parsePosition(value));
      }
      else if (attributeName.equals(RenderConstants.x2)) {
        setX2(XMLTools.parsePosition(value));
      }
      else if (attributeName.equals(RenderConstants.y1)) {
        setY1(XMLTools.parsePosition(value));
      }
      else if (attributeName.equals(RenderConstants.y2)) {
        setY2(XMLTools.parsePosition(value));
      }
      else if (attributeName.equals(RenderConstants.z1)) {
        setZ1(XMLTools.parsePosition(value));
      }
      else if (attributeName.equals(RenderConstants.z2)) {
        setZ2(XMLTools.parsePosition(value));
      }
      else {
        isAttributeRead = false;
      }
    }
    return isAttributeRead;
  }
}