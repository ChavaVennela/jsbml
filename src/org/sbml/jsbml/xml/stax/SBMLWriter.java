/*
 * $Id$
 * $URL$
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

package org.sbml.jsbml.xml.stax;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.dom.DOMConverter;
import org.codehaus.staxmate.out.SMNamespace;
import org.codehaus.staxmate.out.SMOutputContext;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;
import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.Constraint;
import org.sbml.jsbml.Creator;
import org.sbml.jsbml.History;
import org.sbml.jsbml.JSBML;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.ListOf.Type;
import org.sbml.jsbml.MathContainer;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBase;
import org.sbml.jsbml.UnitDefinition;
import org.sbml.jsbml.resources.Resource;
import org.sbml.jsbml.util.JAXPFacade;
import org.sbml.jsbml.util.StringTools;
import org.sbml.jsbml.util.compilers.MathMLXMLStreamCompiler;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.ctc.wstx.stax.WstxOutputFactory;

/**
 * A SBMLWriter provides the methods to write a SBML file.
 * 
 * @author marine
 * @author rodrigue
 * @author Andreas Dr&auml;ger
 * 
 */
public class SBMLWriter {

	/**
	 * contains the WritingParser instances of this class.
	 */
	private static HashMap<String, WritingParser> instantiatedSBMLParsers = new HashMap<String, WritingParser>();

	/**
	 * contains all the relationships namespace URI <=> ReadingParser class.
	 */
	private static HashMap<String, Class<? extends WritingParser>> packageParsers = new HashMap<String, Class<? extends WritingParser>>();

	/**
	 * This method creates the necessary number of white spaces at the beginning
	 * of an entry in the SBML file.
	 * 
	 * @param indent
	 * @return
	 */
	private static String createIndent(int indent) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indent; i++) {
			sb.append(' ');
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param object
	 *            : object to write
	 * @param namespace
	 * @return the list of the WritingParser instances necessary to write this
	 *         object.
	 */
	private static ArrayList<WritingParser> getInitializedParsers(
			Object object, String namespace) {
		Set<String> packageNamespaces = null;

		// System.out.println("SBMLWriter : getInitializedParsers : namespace, object = "
		// + namespace + ", " + object);

		if (object instanceof SBase) {
			SBase sbase = (SBase) object;
			packageNamespaces = sbase.getNamespaces();
		} else if (object instanceof Annotation) {
			Annotation annotation = (Annotation) object;
			packageNamespaces = annotation.getNamespaces();
		}
		ArrayList<WritingParser> sbmlParsers = new ArrayList<WritingParser>();

		if (packageNamespaces != null) {

			// System.out
			// .println("SBMLWriter : getInitializedParsers : namespaces = "
			// + packageNamespaces);
			if (!packageNamespaces.contains(namespace)) {
				try {

					if (SBMLWriter.instantiatedSBMLParsers
							.containsKey(namespace)) {
						WritingParser sbmlParser = SBMLWriter.instantiatedSBMLParsers
								.get(namespace);
						sbmlParsers.add(sbmlParser);
					} else {

						ReadingParser sbmlParser = SBMLReader
								.getPackageParsers(namespace).newInstance();

						if (sbmlParser instanceof WritingParser) {
							SBMLWriter.instantiatedSBMLParsers.put(namespace,
									(WritingParser) sbmlParser);
							sbmlParsers.add((WritingParser) sbmlParser);
						}
					}
				} catch (InstantiationException e) {
					throw new IllegalArgumentException(
							"An error occur while creating a parser : "
									+ e.getMessage());
				} catch (IllegalAccessException e) {
					throw new IllegalArgumentException(
							"An error occur while creating a parser : "
									+ e.getMessage());
				}
			}

			Iterator<String> iterator = packageNamespaces.iterator();
			while (iterator.hasNext()) {
				String packageNamespace = iterator.next();
				try {
					if (SBMLWriter.instantiatedSBMLParsers
							.containsKey(packageNamespace)) {
						WritingParser parser = SBMLWriter.instantiatedSBMLParsers
								.get(packageNamespace);
						sbmlParsers.add(parser);
					} else if (!SBMLWriter.instantiatedSBMLParsers
							.containsKey(packageNamespace)) {

						// This check allows to write e.g. CellDesigner
						// Namespaces
						// manually to an XML file, without implement the whole
						// parser.
						// (e.g. http://www.sbml.org/2001/ns/celldesigner)
						if (SBMLReader.getPackageParsers(packageNamespace) == null) {
							System.out
									.println("Warning: Skipping detailed parsing of Namespace '"
											+ packageNamespace
											+ "'. No parser available.");
							continue;
						}

						ReadingParser sbmlParser = SBMLReader
								.getPackageParsers(packageNamespace)
								.newInstance();

						if (sbmlParser instanceof WritingParser) {

							SBMLWriter.instantiatedSBMLParsers.put(
									packageNamespace,
									(WritingParser) sbmlParser);
							sbmlParsers.add((WritingParser) sbmlParser);
						}
					}
				} catch (InstantiationException e) {
					throw new IllegalArgumentException(
							"An error occur while creating a parser : "
									+ e.getMessage());
				} catch (IllegalAccessException e) {
					throw new IllegalArgumentException(
							"An error occur while creating a parser : "
									+ e.getMessage());
				}
			}
		}

		// System.out.println("SBMLWriter : getInitializedParsers : SBMLparsers = "
		// + SBMLParsers);

		return sbmlParsers;
	}

	/**
	 * 
	 * @param level
	 * @param version
	 * @return the name space matching the level and version.
	 */
	private static String getNamespaceFrom(int level, int version) {
		if (level == 3) {
			if (version == 1) {
				return "http://www.sbml.org/sbml/level3/version1/core";
			}
		} else if (level == 2) {
			if (version == 4) {
				return "http://www.sbml.org/sbml/level2/version4";
			} else if (version == 3) {
				return "http://www.sbml.org/sbml/level2/version3";
			} else if (version == 2) {
				return "http://www.sbml.org/sbml/level2/version2";
			} else if (version == 1) {
				return "http://www.sbml.org/sbml/level2";
			}
		} else if (level == 1) {
			if ((version == 1) || (version == 2)) {
				return "http://www.sbml.org/sbml/level1";
			}
		}
		return null;
	}

	/**
	 * 
	 * @param namespace
	 * @return the WritingParser class associated with 'namespace'. Null if
	 *         there is not matching WritingParser class.
	 */
	public static Class<? extends WritingParser> getWritingPackageParsers(
			String namespace) {
		return SBMLWriter.packageParsers.get(namespace);
	}

	/**
	 * Initializes the packageParser {@link HasMap} of this class.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static void initializePackageParserNamespaces() {
		Properties p = new Properties();
		try {
			p
					.loadFromXML(Resource
							.getInstance()
							.getStreamFromResourceLocation(
									"org/sbml/jsbml/resources/cfg/PackageParserNamespaces.xml"));
			for (Object k : p.keySet()) {
				String key = k.toString();
				packageParsers.put(key, (Class<? extends WritingParser>) Class
						.forName(p.getProperty(key)));
			}
		} catch (InvalidPropertiesFormatException e) {
			throw new IllegalArgumentException(
					"The format of the PackageParserNamespaces.xml file is incorrect.");
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"There was a problem opening the file PackageParserNamespaces.xml.");
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
			throw new IllegalArgumentException(
					"There was a problem loading the file PackageParserNamespaces.xml : "
							+ e.getMessage());

		}
	}

	/**
	 * 
	 * @param args
	 * @throws SBMLException
	 */
	public static void main(String[] args) throws SBMLException {

		if (args.length < 1) {
			System.out
					.println("Usage : java org.sbml.jsbml.xml.stax.SBMLWriter sbmlFileName");
			System.exit(0);
		}

		String fileName = args[0];
		String jsbmlWriteFileName = fileName.replaceFirst(".xml", "-jsbml.xml");

		System.out.println("Reading " + fileName + "and writing"
				+ jsbmlWriteFileName);

		SBMLDocument testDocument;
		try {
			testDocument = SBMLReader.readSBMLFile(fileName);
			write(testDocument, jsbmlWriteFileName);
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the SBMLDocument in a SBML file.
	 * 
	 * @param sbmlDocument
	 *            : the SBMLDocument to write
	 * @param stream
	 *            : a stream where to write the content of the model to.
	 * @throws XMLStreamException
	 * @throws SBMLException
	 * @throws IOException
	 * 
	 */
	public static void write(SBMLDocument sbmlDocument, OutputStream stream)
			throws XMLStreamException, SBMLException {
		write(sbmlDocument, stream, null, null);
	}

	/**
	 * 
	 * @param sbmlDocument
	 * @param stream
	 * @param programName
	 *            can be null
	 * @param programVersion
	 *            can be null
	 * @throws XMLStreamException
	 * @throws SBMLException
	 * 
	 */
	public static void write(SBMLDocument sbmlDocument, OutputStream stream,
			String programName, String programVersion)
			throws XMLStreamException, SBMLException {
		if (!sbmlDocument.isSetLevel() || !sbmlDocument.isSetVersion()) {
			throw new IllegalArgumentException(
					"Unable to write SBML output for documents with undefined SBML Level and Version flag.");
		}
		SBMLReader.initializePackageParserNamespaces();
		SBMLWriter.initializePackageParserNamespaces();

		SMOutputFactory smFactory = new SMOutputFactory(WstxOutputFactory
				.newInstance());

		XMLStreamWriter2 streamWriter = smFactory.createStax2Writer(stream);

		SMOutputDocument outputDocument = SMOutputFactory.createOutputDocument(
				streamWriter, "1.0", "UTF-8", false);
		// outputDocument.setIndentation(newLine + " ", 1, 2);

		String SBMLNamespace = getNamespaceFrom(sbmlDocument.getLevel(),
				sbmlDocument.getVersion());
		SMOutputContext context = outputDocument.getContext();
		SMNamespace namespace = context.getNamespace(SBMLNamespace);
		namespace.setPreferredPrefix("");
		outputDocument.addCharacters("\n");

		/*
		 * Write a comment to track which program created this SBML file and
		 * which version of JSBML was used for this purpose.
		 */
		if ((programName != null) && (programName.length() > 0)) {
			String date = String.format("%1$tY-%1$tm-%1$td %1$tR", Calendar
					.getInstance().getTime());
			String msg = " Created by %s version %s on %s with jsbml version %s. ";
			outputDocument.addComment(String
					.format(msg, programName, (programVersion != null)
							&& (programVersion.length() > 0) ? programVersion
							: "?", date, JSBML.getJSBMLDottedVersion()));
			outputDocument.addCharacters("\n");
		}

		SMOutputElement smOutputElement = outputDocument.addElement(namespace,
				"sbml");

		SBMLObjectForXML xmlObject = new SBMLObjectForXML();
		xmlObject.setName("sbml");
		xmlObject.setNamespace(SBMLNamespace);
		xmlObject.addAttributes(sbmlDocument.writeXMLAttributes());

		Iterator<Entry<String, String>> it = xmlObject.getAttributes()
				.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			smOutputElement.addAttribute(entry.getKey(), entry.getValue());
		}
		ReadingParser notesParser = null;
		ReadingParser mathMLParser = null;
		try {
			notesParser = SBMLReader.getPackageParsers(
					"http://www.w3.org/1999/xhtml").newInstance();
			mathMLParser = SBMLReader.getPackageParsers(
					"http://www.w3.org/1998/Math/MathML").newInstance();
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(
					"An error occur while creating a parser : "
							+ e.getMessage());
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(
					"An error occur while creating a parser : "
							+ e.getMessage());
		}

		int indent = 2;
		if (sbmlDocument.isSetNotes()) {
			writeNotes(sbmlDocument, smOutputElement, streamWriter,
					SBMLNamespace, indent);
		}
		if (sbmlDocument.isSetAnnotation()) {
			writeAnnotation(sbmlDocument, smOutputElement, streamWriter,
					SBMLNamespace, indent);
		}
		smOutputElement.addCharacters("\n");

		writeSBMLElements(xmlObject, smOutputElement, streamWriter,
				sbmlDocument, notesParser, mathMLParser, indent);

		outputDocument.closeRoot();
	}

	/**
	 * Writes the SBMLDocument in a SBML file.
	 * 
	 * @param sbmlDocument
	 *            : the SBMLDocument to write
	 * @param fileName
	 *            : the name of the file where to write the SBMLDocument.
	 * 
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 * @throws SBMLException
	 * 
	 */
	public static void write(SBMLDocument sbmlDocument, String fileName)
			throws XMLStreamException, FileNotFoundException, SBMLException {
		write(sbmlDocument, fileName, null, null);
	}

	/**
	 * 
	 * @param sbmlDocument
	 * @param fileName
	 * @param programName
	 * @param programVersion
	 * 
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 * @throws SBMLException
	 * 
	 */
	public static void write(SBMLDocument sbmlDocument, String fileName,
			String programName, String programVersion)
			throws XMLStreamException, FileNotFoundException, SBMLException {
		write(sbmlDocument, new BufferedOutputStream(new FileOutputStream(
				fileName)), programName, programVersion);
	}

	/**
	 * Writes the annotation of this sbase component.
	 * 
	 * @param sbase
	 *            : the sbase component
	 * @param element
	 *            : the matching SMOutputElement
	 * @param writer
	 *            : the XMLStreamWriter2
	 * @param sbmlNamespace
	 *            : the SBML namespace.
	 * @param indent
	 *            the number of indent white spaces of this annotation.
	 * @throws XMLStreamException
	 * @throws SBMLException
	 * @throws SAXException
	 */
	private static void writeAnnotation(SBase sbase, SMOutputElement element,
			XMLStreamWriter writer, String sbmlNamespace, int indent)
			throws XMLStreamException, SBMLException {
		SMNamespace namespace = element.getNamespace(sbmlNamespace);
		namespace.setPreferredPrefix("");
		Annotation annotation = sbase.getAnnotation();
		SMOutputElement annotationElement;
		String whiteSpaces = createIndent(indent);
		element.addCharacters("\n");
		element.setIndentation(whiteSpaces, indent, 2);
		annotationElement = element.addElement(namespace, "annotation");
		annotationElement.setIndentation(whiteSpaces, indent, 2);

		if (annotation.getNoRDFAnnotation() != null) {
			StringBuffer annotationBeginning = StringTools.concat(whiteSpaces,
					"<annotation");

			HashMap<String, String> otherNamespaces = annotation
					.getAnnotationNamespaces();
			Iterator<Entry<String, String>> it = otherNamespaces.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				StringTools.append(annotationBeginning, " ", entry.getKey(),
						"=\"", entry.getValue(), "\"");
				if (entry.getKey().contains(":")) {
					String[] key = entry.getKey().split(":");
					annotationElement.getNamespace(key[1], entry.getValue());
				} else {
					annotationElement.getNamespace("", entry.getValue());
				}
			}
			StringTools.append(annotationBeginning, Character.valueOf('>'),
					Character.valueOf('\n'), annotation.getNoRDFAnnotation(),
					whiteSpaces, "</annotation>", Character.valueOf('\n'));

			DOMConverter converter = new DOMConverter();
			String annotationString = annotationBeginning.toString()
					.replaceAll("&", "&amp;");
			// here indent gets lost.
			Document domDocument = null;
			try {
				domDocument = JAXPFacade.getInstance().create(
						new BufferedReader(new StringReader(annotationString)),
						true);
				converter.writeFragment(domDocument.getFirstChild()
						.getChildNodes(), writer);
			} catch (SAXException e) {
				e.printStackTrace();
				// TODO : log error or send SBMLException
			}
		} else {
			writer.writeCharacters("\n");
		}

		// if the given SBase is not a model and the level is smaller than 3,
		// no history can be written.
		if ((annotation.isSetHistory() && ((sbase.getLevel() > 3) || (sbase instanceof Model)))
				|| annotation.getListOfCVTerms().size() > 0) {
			writeRDFAnnotation(annotation, annotationElement, writer,
					indent + 2);
		}
		SBMLObjectForXML xmlObject = new SBMLObjectForXML();
		writeSBMLElements(xmlObject, annotationElement, writer, annotation,
				null, null, indent + 2);
	}

	/**
	 * Writes the listOfCVTerms.
	 * 
	 * @param listOfCVTerms
	 *            : the list of CVTerms to write
	 * @param rdfNamespaces
	 *            : the RDF namespaces and prefixes
	 * @param writer
	 *            : the XMLStreamWriter2
	 * @param indent
	 * @throws XMLStreamException
	 */
	private static void writeCVTerms(List<CVTerm> listOfCVTerms,
			HashMap<String, String> rdfNamespaces, XMLStreamWriter writer,
			int indent) throws XMLStreamException {
		String rdfPrefix = rdfNamespaces
				.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		String whiteSpace = createIndent(indent);
		if (listOfCVTerms.size() > 0) {

			for (int i = 0; i < listOfCVTerms.size(); i++) {
				CVTerm cvTerm = listOfCVTerms.get(i);
				String namespaceURI = null;
				String prefix = null;
				String elementName = null;
				if (cvTerm.getQualifierType().equals(
						CVTerm.Type.BIOLOGICAL_QUALIFIER)) {
					namespaceURI = "http://biomodels.net/biology-qualifiers/";
					prefix = rdfNamespaces
							.get("http://biomodels.net/biology-qualifiers/");
					elementName = cvTerm.getBiologicalQualifierType()
							.getElementNameEquivalent();
				} else if (cvTerm.getQualifierType().equals(
						CVTerm.Type.MODEL_QUALIFIER)) {
					namespaceURI = "http://biomodels.net/model-qualifiers/";
					prefix = rdfNamespaces
							.get("http://biomodels.net/model-qualifiers/");
					elementName = Annotation
							.getElementNameEquivalentToQualifier(cvTerm
									.getModelQualifierType());
				}

				if ((namespaceURI != null) && (elementName != null)
						&& (prefix != null)) {
					writer.writeCharacters(whiteSpace);
					writer.writeStartElement(prefix, elementName, namespaceURI);
					writer.writeCharacters("\n");
					if (cvTerm.getNumResources() > 0) {
						writer.writeCharacters(whiteSpace + "  ");
						writer.writeStartElement(rdfPrefix, "Bag",
								"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
						writer.writeCharacters("\n");
						for (int j = 0; j < cvTerm.getNumResources(); j++) {
							writer.writeCharacters(whiteSpace + "    ");
							writer
									.writeStartElement(rdfPrefix, "li",
											"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
							writer
									.writeAttribute(
											rdfPrefix,
											"http://www.w3.org/1999/02/22-rdf-syntax-ns#",
											"resource", cvTerm
													.getResourceURI(j));
							writer.writeEndElement();
							writer.writeCharacters("\n");
						}
						writer.writeCharacters(whiteSpace + "  ");
						writer.writeEndElement();
						writer.writeCharacters("\n");
						writer.writeCharacters(whiteSpace);
						writer.writeEndElement();
						writer.writeCharacters("\n");
					}
				}
			}
		}
	}

	/**
	 * Writes the history represented by this History instance.
	 * 
	 * @param history
	 *            : the model history to write
	 * @param rdfNamespaces
	 *            : contains the RDF namespaces and their prefixes.
	 * @param writer
	 *            : the XMLStreamWriter2
	 * @param indent
	 * @throws XMLStreamException
	 */
	private static void writeHistory(History history,
			HashMap<String, String> rdfNamespaces, XMLStreamWriter writer,
			int indent) throws XMLStreamException {
		String whiteSpace = createIndent(indent);
		String rdfPrefix = rdfNamespaces
				.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		if (history.getNumCreators() > 0) {
			String creatorPrefix = rdfNamespaces
					.get("http://purl.org/dc/elements/1.1/");
			writer.writeCharacters(whiteSpace);
			writer.writeStartElement(creatorPrefix, "creator",
					"http://purl.org/dc/elements/1.1/");
			writer.writeCharacters("\n");
			writer.writeCharacters(whiteSpace + "  ");
			writer.writeStartElement(rdfPrefix, "Bag",
					"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
			writer.writeCharacters("\n");

			for (int i = 0; i < history.getNumCreators(); i++) {
				Creator modelCreator = history.getCreator(i);
				writer.writeCharacters(whiteSpace + "    ");
				writer.writeStartElement(rdfPrefix, "li",
						"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
				writer.writeAttribute(rdfPrefix,
						"http://www.w3.org/1999/02/22-rdf-syntax-ns#",
						"parseType", "Resource");
				String vCardPrefix = rdfNamespaces
						.get("http://www.w3.org/2001/vcard-rdf/3.0#");

				if (modelCreator.isSetFamilyName()
						|| modelCreator.isSetGivenName()) {
					writer.writeCharacters("\n");
					writer.writeCharacters(whiteSpace + "      ");
					writer.writeStartElement(vCardPrefix, "N",
							"http://www.w3.org/2001/vcard-rdf/3.0#");
					writer.writeAttribute(
							"http://www.w3.org/1999/02/22-rdf-syntax-ns#",
							"parseType", "Resource");
					writer.writeCharacters("\n");

					if (modelCreator.isSetFamilyName()) {
						writer.writeCharacters(whiteSpace + "        ");
						writer.writeStartElement(vCardPrefix, "Family",
								"http://www.w3.org/2001/vcard-rdf/3.0#");
						writer.writeCharacters(modelCreator.getFamilyName());
						writer.writeEndElement();
						writer.writeCharacters("\n");
					}
					if (modelCreator.isSetGivenName()) {
						writer.writeCharacters(whiteSpace + "        ");
						writer.writeStartElement(vCardPrefix, "Given",
								"http://www.w3.org/2001/vcard-rdf/3.0#");
						writer.writeCharacters(modelCreator.getGivenName());
						writer.writeEndElement();
						writer.writeCharacters("\n");
					}
					writer.writeCharacters(whiteSpace + "      ");
					writer.writeEndElement();
					writer.writeCharacters("\n");
				}

				if (modelCreator.isSetEmail()) {
					writer.writeCharacters(whiteSpace + "      ");
					writer.writeStartElement(vCardPrefix, "EMAIL",
							"http://www.w3.org/2001/vcard-rdf/3.0#");
					writer.writeCharacters(modelCreator.getEmail());
					writer.writeEndElement();
					writer.writeCharacters("\n");
				}
				if (modelCreator.isSetOrganisation()) {
					writer.writeCharacters(whiteSpace + "      ");
					writer.writeStartElement(vCardPrefix, "ORG",
							"http://www.w3.org/2001/vcard-rdf/3.0#");
					writer.writeAttribute(rdfPrefix,
							"http://www.w3.org/1999/02/22-rdf-syntax-ns#",
							"parseType", "Resource");
					writer.writeCharacters("\n");
					writer.writeCharacters(whiteSpace + "        ");
					writer.writeStartElement(vCardPrefix, "Orgname",
							"http://www.w3.org/2001/vcard-rdf/3.0#");
					writer.writeCharacters(modelCreator.getOrganisation());
					writer.writeEndElement();
					writer.writeCharacters("\n");
					writer.writeCharacters(whiteSpace + "      ");
					writer.writeEndElement();
					writer.writeCharacters("\n");
				}
				writer.writeCharacters(whiteSpace + "    ");
				writer.writeEndElement();
				writer.writeCharacters("\n");
			}
			writer.writeCharacters(whiteSpace + "  ");
			writer.writeEndElement();
			writer.writeCharacters("\n");
			writer.writeCharacters(whiteSpace);
			writer.writeEndElement();
			writer.writeCharacters("\n");
		}
		String datePrefix = rdfNamespaces.get("http://purl.org/dc/terms/");

		// System.out.println("isSetCreatedDate = " +
		// modelHistory.isSetCreatedDate());
		// System.out.println("isSetModifiedDate = " +
		// modelHistory.isSetModifiedDate());

		if (history.isSetCreatedDate()) {
			writer.writeCharacters(whiteSpace);
			writer.writeStartElement(datePrefix, "created",
					"http://purl.org/dc/terms/");
			writer.writeAttribute(rdfPrefix,
					"http://www.w3.org/1999/02/22-rdf-syntax-ns#", "parseType",
					"Resource");
			writer.writeCharacters("\n");
			writer.writeCharacters(whiteSpace + "  ");
			writer.writeStartElement(datePrefix, "W3CDTF",
					"http://purl.org/dc/terms/");
			writer.writeCharacters(history.getCreatedDate().toString());
			writer.writeEndElement();
			writer.writeCharacters("\n");
			writer.writeCharacters(whiteSpace);
			writer.writeEndElement();
			writer.writeCharacters("\n");
		}
		if (history.isSetModifiedDate()) {
			for (int i = 0; i < history.getNumModifiedDates(); i++) {
				writer.writeCharacters(whiteSpace);
				writer.writeStartElement(datePrefix, "modified",
						"http://purl.org/dc/terms/");
				writer.writeAttribute(rdfPrefix,
						"http://www.w3.org/1999/02/22-rdf-syntax-ns#",
						"parseType", "Resource");
				writer.writeCharacters("\n");
				writer.writeCharacters(whiteSpace + "  ");
				writer.writeStartElement(datePrefix, "W3CDTF",
						"http://purl.org/dc/terms/");
				writer.writeCharacters(history.getModifiedDate(i).toString());
				writer.writeEndElement();
				writer.writeCharacters("\n");
				writer.writeCharacters(whiteSpace);
				writer.writeEndElement();
				writer.writeCharacters("\n");
			}
		}
	}

	/**
	 * Writes the MathML expression for the math element of this sbase
	 * component.
	 * 
	 * @param m
	 *            : the sbase component
	 * @param element
	 *            : the matching SMOutputElement
	 * @param writer
	 *            : the XMLStreamWriter
	 * @param indent
	 * @throws XMLStreamException
	 * 
	 */
	private static void writeMathML(MathContainer m, SMOutputElement element,
			XMLStreamWriter writer, int indent) throws XMLStreamException {
		if (m.isSetMath()) {

			writer.writeCharacters("\n");
			writer.writeCharacters(createIndent(indent + 2));
			// writer.setPrefix("math", "http://www.w3.org/1998/Math/MathML");
			// writer.writeStartElement("http://www.w3.org/1998/Math/MathML",
			// "math");
			writer.writeStartElement("math");
			writer.writeAttribute("xmlns:math",
					"http://www.w3.org/1998/Math/MathML");
			writer.writeCharacters("\n");

			MathMLXMLStreamCompiler compiler = new MathMLXMLStreamCompiler(
					writer, createIndent(indent + 4));
			compiler.compile(m.getMath());

			writer.writeCharacters(createIndent(indent + 2));
			writer.writeEndElement();
			writer.writeCharacters("\n");
		}
	}

	/**
	 * Writes the message of this Constraint.
	 * 
	 * @param sbase
	 *            : the Constraint component
	 * @param element
	 *            : the matching SMOUtputElement
	 * @param writer
	 *            : the XMLStreamWriter2
	 * @param sbmlNamespace
	 *            : the SBML namespace
	 */
	private static void writeMessage(Constraint sbase, SMOutputElement element,
			XMLStreamWriter writer, String sbmlNamespace) {

		SMNamespace namespace = element.getNamespace(sbmlNamespace);
		namespace.setPreferredPrefix("");
		try {
			element.addCharacters("\n");

			DOMConverter converter = new DOMConverter();
			SMOutputElement note = element.addElementWithCharacters(namespace,
					"message", "\n");
			String messageString = sbase.getMessage().replaceAll("&", "&amp;");
			Document domDocument = JAXPFacade.getInstance().create(
					new BufferedReader(new StringReader(messageString)), true);
			converter.writeFragment(domDocument.getChildNodes(), writer);
			note.addCharacters("\n");
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the notes of this sbase component.
	 * 
	 * @param sbase
	 *            : the SBase component
	 * @param element
	 *            : the matching SMOUtputElement
	 * @param writer
	 *            : the XMLStreamWriter2
	 * @param sbmlNamespace
	 *            : the SBML namespace
	 * @param indent
	 * @throws XMLStreamException
	 */
	private static void writeNotes(SBase sbase, SMOutputElement element,
			XMLStreamWriter writer, String sbmlNamespace, int indent)
			throws XMLStreamException {

		SMNamespace namespace = element.getNamespace(sbmlNamespace);
		namespace.setPreferredPrefix("");
		element.addCharacters("\n");
		String whiteSpaces = createIndent(indent);
		writer.writeCharacters(whiteSpaces);

		DOMConverter converter = new DOMConverter();
		SMOutputElement note = element.addElementWithCharacters(namespace,
				"notes", "\n");
		note.setIndentation(whiteSpaces, indent, 2);
		String notes = sbase.getNotesString();
		/*
		 * This can lead to problems if utf8 characters are encoded in the notes
		 * using the designated HTML codes, i.e., &#8820; for left double
		 * quotes.
		 */
		// .replaceAll("&", "&amp;");
		try {
			Document domDocument = JAXPFacade.getInstance().create(
					new BufferedReader(new StringReader(notes)), true);
			converter.writeFragment(domDocument.getChildNodes(), writer);
			note.addCharacters("\n");
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the RDF annotation contained in 'annotation'.
	 * 
	 * @param annotation
	 *            : the annotation to write
	 * @param annotationElement
	 *            : the matching SMOutputElement
	 * @param writer
	 *            : the XMLStreamWriter.
	 * @param indent
	 * @throws XMLStreamException
	 */
	private static void writeRDFAnnotation(Annotation annotation,
			SMOutputElement annotationElement, XMLStreamWriter writer,
			int indent) throws XMLStreamException {
		String whiteSpace = createIndent(indent);
		SMNamespace namespace = annotationElement.getNamespace(
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
		annotationElement.setIndentation(whiteSpace, indent, 2);
		SMOutputElement rdfElement = annotationElement.addElement(namespace,
				"RDF");

		HashMap<String, String> rdfNamespaces = annotation
				.getRDFAnnotationNamespaces();
		Iterator<Entry<String, String>> it = rdfNamespaces.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			if (!entry.getKey().equals(namespace.getURI())) {
				writer.writeNamespace(entry.getValue(), entry.getKey());
			}
		}
		rdfElement.addCharacters("\n");
		rdfElement.setIndentation(whiteSpace + "  ", indent + 2, 2);
		SMOutputElement descriptionElement = rdfElement.addElement(namespace,
				"Description");
		descriptionElement.addAttribute(namespace, "about", annotation
				.getAbout());
		descriptionElement.addCharacters("\n");
		if (annotation.isSetHistory()) {
			writeHistory(annotation.getHistory(), rdfNamespaces, writer,
					indent + 4);
		}
		if (annotation.getListOfCVTerms().size() > 0) {
			writeCVTerms(annotation.getListOfCVTerms(), rdfNamespaces, writer,
					indent + 2);
		}
		descriptionElement.setIndentation(whiteSpace + "  ", indent + 2, 2);
		descriptionElement.addCharacters(whiteSpace + "  ");
		annotationElement.setIndentation(whiteSpace, indent, 2);
		rdfElement.addCharacters("\n");
		rdfElement.addCharacters(whiteSpace);
		annotationElement.addCharacters("\n");
	}

	/**
	 * Writes the SBML elements.
	 * 
	 * @param parentXmlObject
	 *            : contains the XML information of the parentElement.
	 * @param smOutputParentElement
	 *            : SMOutputElement of the parentElement.
	 * @param streamWriter
	 * @param objectToWrite
	 *            : the Object to write.
	 * @param notesParser
	 *            : the WritingParser to parse the notes.
	 * @param MathMLparser
	 *            : the WritingParser to parse the MathML expressions.
	 * @param indent
	 *            The number of white spaces to indent this element.
	 * @throws XMLStreamException
	 * @throws SBMLException
	 * @throws SAXException
	 */
	private static void writeSBMLElements(SBMLObjectForXML parentXmlObject,
			SMOutputElement smOutputParentElement,
			XMLStreamWriter streamWriter, Object objectToWrite,
			ReadingParser notesParser, ReadingParser MathMLparser, int indent)
			throws XMLStreamException, SBMLException {

		String whiteSpaces = createIndent(indent);

		// Get the list of parsers to use.
		// TODO : check this, it should probably be a loop, one element could be
		// written by several parser
		ArrayList<WritingParser> listOfPackages = getInitializedParsers(
				objectToWrite, smOutputParentElement.getNamespace().getURI());

		// System.out.println("SBMLWriter : writeSBMLElements : xmlObject = " +
		// xmlObject);
		// System.out.println("SBMLWriter : writeSBMLElements : parentElement = "
		// + parentElement.getLocalName() + ", "
		// + parentElement.getNamespace().getURI());
		// System.out.println("SBMLWriter : writeSBMLElements : objectToWrite = "
		// + objectToWrite + '\n');
		// System.out.println("SBMLWriter : writeSBMLElements : listOfPackages = "
		// + listOfPackages + '\n');

		Iterator<WritingParser> iterator = listOfPackages.iterator();
		while (iterator.hasNext()) {
			WritingParser parser = iterator.next();
			List<Object> sbmlElementsToWrite = parser
					.getListOfSBMLElementsToWrite(objectToWrite);

			// System.out.println("SBMLWriter : writeSBMLElements : parser = "
			// + parser);
			// System.out
			// .println("SBMLWriter : writeSBMLElements : elementsToWrite = "
			// + sbmlElementsToWrite);

			if (sbmlElementsToWrite == null) {
				// TODO test if there are some characters to write.
				streamWriter.writeCharacters(whiteSpaces.substring(0,
						indent - 2));
			} else {
				for (int i = 0; i < sbmlElementsToWrite.size(); i++) {
					Object nextObjectToWrite = sbmlElementsToWrite.get(i);

					/*
					 * Skip predefined UnitDefinitions (check depending on Level
					 * and Version).
					 */
					if (nextObjectToWrite instanceof ListOf<?>) {
						ListOf<?> list = (ListOf<?>) nextObjectToWrite;
						if (list.size() > 0) {
							SBase sb = list.getFirst();
							if ((sb instanceof UnitDefinition)
									&& (parser
											.getListOfSBMLElementsToWrite(nextObjectToWrite) == null)) {
								streamWriter.writeCharacters(whiteSpaces
										.substring(0, indent - 2));
								continue;
							}
						} else {
							streamWriter.writeCharacters(whiteSpaces.substring(
									0, indent - 2));
							continue;
						}
					}

					parentXmlObject.clear();

					/*
					 * The following containers are all optional in a
					 * <reaction>, but if any is present, it must not be empty:
					 * <listOfReactants>, <listOfProducts>, <listOfModifiers>,
					 * <kineticLaw>. (References: L2V2 Section 4.13; L2V3
					 * Section 4.13; L2V4 Section 4.13)
					 */
					if (nextObjectToWrite instanceof ListOf<?>) {
						ListOf<?> toTest = (ListOf<?>) nextObjectToWrite;
						Type listType = toTest.getSBaseListType();
						if (listType == Type.none) {
							// Prevent writing invalid SBML if list types are
							// not set appropriately.
							throw new SBMLException(String.format(
									"Unknown ListOf type \"%s\".", toTest
											.getElementName()));
						}
						if (listType.equals(ListOf.Type.listOfReactants)
								|| listType.equals(ListOf.Type.listOfProducts)
								|| listType.equals(ListOf.Type.listOfModifiers)) {
							if (toTest.size() < 1) {
								continue; // Skip these, see reference in
								// comment above.
							}
						}
					} else if (nextObjectToWrite instanceof KineticLaw) {
						// TODO: Is there any chance, that an KineticLaw get's
						// an empty XML entity?
					}
					streamWriter.writeCharacters(whiteSpaces);
					parser.writeElement(parentXmlObject, nextObjectToWrite);
					parser.writeNamespaces(parentXmlObject, nextObjectToWrite);
					parser.writeAttributes(parentXmlObject, nextObjectToWrite);
					SMOutputElement newOutPutElement = null;
					if (parentXmlObject.isSetName()) {
						if (parentXmlObject.isSetNamespace()) {
							SMNamespace namespaceContext = smOutputParentElement
									.getNamespace(parentXmlObject
											.getNamespace(), parentXmlObject
											.getPrefix());
							newOutPutElement = smOutputParentElement
									.addElement(namespaceContext,
											parentXmlObject.getName());
						} else {

							newOutPutElement = smOutputParentElement
									.addElement(smOutputParentElement
											.getNamespace(), parentXmlObject
											.getName());
						}

						Iterator<Entry<String, String>> it = parentXmlObject
								.getAttributes().entrySet().iterator();
						while (it.hasNext()) {
							Entry<String, String> entry = it.next();
							newOutPutElement.addAttribute(entry.getKey(), entry
									.getValue());
						}
						if (nextObjectToWrite instanceof SBase) {
							SBase s = (SBase) nextObjectToWrite;
							if (s.isSetNotes() && notesParser != null) {
								writeNotes(s, newOutPutElement, streamWriter,
										newOutPutElement.getNamespace()
												.getURI(), indent + 2);
							}
							if (s.isSetAnnotation()) {
								writeAnnotation(s, newOutPutElement,
										streamWriter, newOutPutElement
												.getNamespace().getURI(),
										indent + 2);
							}
						}
						if (nextObjectToWrite instanceof Constraint
								&& notesParser != null) {
							Constraint constraint = (Constraint) nextObjectToWrite;
							if (!constraint.isSetMessage()) {
								writeMathML(constraint, newOutPutElement,
										streamWriter, indent + 2);
							}
						}
						if ((nextObjectToWrite instanceof MathContainer)
								&& (MathMLparser != null)) {
							MathContainer mathContainer = (MathContainer) nextObjectToWrite;
							writeMathML(mathContainer, newOutPutElement,
									streamWriter, indent + 2);
						}
						newOutPutElement.addCharacters("\n");

						writeSBMLElements(parentXmlObject, newOutPutElement,
								streamWriter, nextObjectToWrite, notesParser,
								MathMLparser, indent + 2);
						smOutputParentElement.addCharacters(StringTools
								.newLine());
					}
					streamWriter.writeCharacters(whiteSpaces.substring(0,
							indent - 2));
				}
			}
		}
	}

	/**
	 * 
	 * @param d
	 * @return
	 * @throws XMLStreamException
	 * @throws SBMLException
	 * 
	 */
	public static String writeSBMLToString(SBMLDocument d)
			throws XMLStreamException, SBMLException {
		return writeSBMLToString(d, null, null);
	}

	/***
	 * 
	 * @param d
	 * @param programName
	 * @param programVersion
	 * @return
	 * @throws XMLStreamException
	 * @throws SBMLException
	 * 
	 */
	public static String writeSBMLToString(SBMLDocument d, String programName,
			String programVersion) throws XMLStreamException, SBMLException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		write(d, stream, programName, programVersion);
		return stream.toString();
	}

	/**
	 * 
	 * @param document
	 * @param file
	 * @param programName
	 * @param programVersion
	 * @return
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 * @throws SBMLException
	 */
	public static String write(SBMLDocument document, File file,
			String programName, String programVersion)
			throws FileNotFoundException, XMLStreamException, SBMLException {
		FileOutputStream stream = new FileOutputStream(file);
		write(document, stream, programName, programVersion);
		return stream.toString();
	}

	/**
	 * 
	 * @param document
	 * @param file
	 * @throws SBMLException 
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public static void write(SBMLDocument document, File file) throws FileNotFoundException, XMLStreamException, SBMLException {
		write(document, file, null, null);
	}

	// ToCHECK : writing of X should not include unset fields.

	// TODO : dcterms:created, dcterms:modified are not saved !

	// TODO : notes where an & is present are not written out (so almost all the
	// notes from the models in Biomodels DB)
	// When java read some notes with &amp;, it convert it to simply & in UTF-8
	// and when trying to write out the notes,
	// there is then an exception as the character is not allowed.
	// => Maybe the new encodeForHTML() function comes in handy
	// here?
	// TODO: I don't think it is a good style, replacing all & by &amp; Maybe
	// the user has taken care of this himself
	// (as he should in my opinion) and then all "&amp;" replace to "&amp;amp;"!
	// A better way would be, replacing all
	// those "replaceAll("&", "&amp;");" by a check, which checks if there are
	// "&"'s in the string and then if they
	// already have the style &amp; and only if not => encode characters by
	// their HTML encoding.

	// TODO : when there are some custom annotations that do not declare their
	// namespace in the annotation but only on
	// the sbml element, the whole annotation failed to be written out.

	// TODO : put all of that as tracker item on sourceforge as it will probably
	// take some time to be resolved.
	// TODO : put some logging system in place

	// TODO : write a script to automatically test an SBML file, checking with
	// libsbml that all the values are the same in the original file
	// and in the newly created one.

	// TODO : test a bit more Xstream with stax and using Qname to see how it
	// can deal with math or rdf bloc

}
