/*
 *  JFLAP - Formal Languages and Automata Package
 * 
 * 
 *  Susan H. Rodger
 *  Computer Science Department
 *  Duke University
 *  August 27, 2009

 *  Copyright (c) 2002-2009
 *  All rights reserved.

 *  JFLAP is open source software. Please see the LICENSE for terms.
 *
 */





package file.xml;


import java.io.*;
import java.util.Map;

import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.*;


import model.util.JFLAPConstants;

import org.w3c.dom.*;

import file.Codec;
import file.EncodeException;
import file.FileParseException;
import file.xml.*;



import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * This is the codec for reading and writing JFLAP structures as XML documents.
 * 
 * @author Thomas Finley, Henry Qin
 */

public abstract class JFFCodec<T> extends Codec<T> {


	/**
	 * Determines which files this FileFilter will allow. We are only allowing files with extension XML and jff.
	 * 
	 */
	@Override
	public boolean accept(File f){
		if (f.isDirectory()) return true;
		boolean b = false;
		for (String s: new String[]{".xml",".jff",".jdef"})
			b = (b || f.getName().endsWith(s));
		return true;
	} 

	/**
	 * Given a file, this will return a JFLAP structure associated with that
	 * file.
	 * 
	 * @param file
	 *            the file to decode into a structure
	 * @return a JFLAP structure resulting from the interpretation of the file
	 * @throws FileParseException
	 *             if there was a problem reading the file
	 */
	public T decode(File file) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			Transducer<T> transducer = TransducerHelper.getTransducer(doc.getDocumentElement());
			return transducer.fromStructureRoot(doc.getDocumentElement());
		} catch (ParserConfigurationException e) {
			throw new FileParseException("Java could not create the parser!");
		} catch (IOException e) {
			throw new FileParseException("Could not open file to read!");
		} catch (org.xml.sax.SAXException e) {
			throw new FileParseException("Could not parse XML!\n" + e.getMessage());
		} catch (ExceptionInInitializerError e) {
			// Hmm. That shouldn't be.
			System.err.println("STATIC INIT:");
			e.getException().printStackTrace();
			throw new FileParseException("Unexpected Error!");
		}
	}





	/**
	 * Given a structure, this will attempt to write the structure as a
	 * serialized object to a file.
	 * 
	 * @param structure
	 *            the structure to encode
	 * @param file
	 *            the file to save the structure to
	 * @param parameters
	 *            implementors have the option of accepting custom parameters in
	 *            the form of a map
	 * @return the file to which the structure was written
	 * @throws EncodeException
	 *             if there was a problem writing the file
	 */
	@Override
	public File encode(T structure, File file, Map parameters) {
		file = new File(this.proposeFilename(file.getName(), structure));
		Transducer transducer = null;
		try {
			transducer = TransducerHelper.getTransducer(structure);

			/*
			 * If we are saving a pumping lemma, the associated structure would
			 * actually be a pumping lemma chooser. Thus, we have to get the
			 * lemma from the chooser.
			 */
			Document dom = transducer.toDOM(structure);

			//			Document dom = transducer.toDOM(structure);    // original line

			DOMPrettier.makePretty(dom);
			Source s = new DOMSource(dom);
			Result r = new StreamResult(file);
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.transform(s, r);
			return file;
		} catch (IllegalArgumentException e) {
			throw new EncodeException(
					"No XML transducer available for this structure!");
		} catch (TransformerConfigurationException e) {
			throw new EncodeException("Could not open file to write!");
		} catch (TransformerException e) {
			throw new EncodeException("Could not open file to write!");
		}
	}

	/**
	 * Returns if this type of structure can be encoded with this encoder. This
	 * should not perform a detailed check of the structure, since the user will
	 * have no idea why it will not be encoded correctly if the {@link #encode}
	 * method does not throw a {@link FileParseException}.
	 * 
	 * @param structure
	 *            the structure to check
	 * @return if the structure, perhaps with minor changes, could possibly be
	 *         written to a file
	 */
	public boolean canEncode(Serializable structure) {
		return true;
	}

	/**
	 * Returns the description of this codec.
	 * 
	 * @return the description of this codec
	 */
	public String getDescription() {
		return "JFLAP 7 File";
	}

	/**
	 * Given a proposed filename, returns a new suggested filename. JFLAP 4
	 * saved files have the suffix <CODE>.jff</CODE> appended to them.
	 * 
	 * @param filename
	 *            the proposed name
	 * @param structure
	 *            the structure that will be saved
	 * @return the new suggestion for a name
	 */
	public String proposeFilename(String filename, Serializable structure) {
		String suffix = JFLAPConstants.JFF_SUFFIX;
		if (!filename.endsWith(suffix)) filename += suffix;

		return filename;
	}

	public static FileFilter getJFFfileFilter(){
		return new FileFilter() {

			@Override
			public String getDescription() {
				return "A filter for JFLAP "+ JFLAPConstants.VERSION + " files.";
			}

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(JFLAPConstants.JFF_SUFFIX);

			}
		};
	}

}
