package com.fluxlus.V2Gdecoder.dataprocess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import com.v2gclarity.risev2g.shared.enumerations.GlobalValues;
// import com.v2gclarity.risev2g.shared.messageHandling.MessageHandler;
// import com.v2gclarity.risev2g.shared.utils.MiscUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.grammars.GrammarFactory;
import com.siemens.ct.exi.core.grammars.Grammars;	
import com.siemens.ct.exi.main.api.sax.EXIResult;
import com.siemens.ct.exi.main.api.sax.EXISource;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;

import com.fluxlus.V2Gdecoder.binascii.BinAscii;

/*
 *  Copyright (C) V2Gdecoder by FlUxIuS (Sebastien Dudek)
 */

public class dataprocess {
	// public MessageHandler messageHandler;
	
	// public MessageHandler getMessageHandler() {
	// 	return messageHandler;
	// }
	
	// public static void initConfig() {
	// 	MiscUtils.loadProperties("./test.properties");
	// }

	public static String Xml2Exi(String inputsc, String xmlstr, decodeMode mode, Grammars grammar) throws IOException, SAXException, EXIException {
		/*
		 * 		Encode XML to EXI
		 * 		In(1): XML string or input file path string
		 * 		In(2): (decodeMode) Input/Output mode
		 * 		Out: encoded result string
		 * */
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		ByteArrayOutputStream bosEXI = null;
		OutputStream osEXI = null;
		String result = null;
		String outfile = null;
		
		exiFactory.setGrammars(grammar);

		EXIResult exiResult = new EXIResult(exiFactory);
		if (mode == decodeMode.FILETOSTR || mode == decodeMode.STRTOSTR)
		{ // stream output
			bosEXI = new ByteArrayOutputStream();
			exiResult.setOutputStream(bosEXI);
		} else { // file output
			if (mode == decodeMode.STRTOFILE) {
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String filename = new String("out."+timestamp.toString()+".exi");
				outfile = filename;
				osEXI = new FileOutputStream(filename);
			}
			else
				osEXI = new FileOutputStream(xmlstr + ".exi");
			exiResult.setOutputStream(osEXI);
		}
		
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler( exiResult.getHandler() );
		
		xmlReader.parse(new InputSource(new StringReader(inputsc))); // parse XML input
		if (mode == decodeMode.FILETOSTR || mode == decodeMode.STRTOSTR)
		{
			result = BinAscii.hexlify(bosEXI.toByteArray());
			bosEXI.close();
		} else {
			osEXI.close();
			result = new String("File written in '" + mode + outfile + "'");
		}
		return result;
	}
	
	public static String Exi2Xml(String existr, decodeMode mode, Grammars grammar) throws IOException, SAXException, EXIException, TransformerException {
		/*
		 * 		Decode EXI data to XML
		 * 		In(1): String to decode
		 * 		In(2): (decodeMode) Input/Output
		 * 		In(3): String of grammar XSD path to use 
		 * 		Out: decoded result string
		 * */
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		String result = null;
		String inputsc = existr;
		Result res = null;
		ByteArrayOutputStream outputStream = null;
		InputSource is = null;
		exiFactory.setGrammars(grammar);
		
		if (mode == decodeMode.FILETOSTR || mode == decodeMode.FILETOFILE)
			is = new InputSource(inputsc);
		else
			is = new InputSource(new ByteArrayInputStream(BinAscii.unhexlify(inputsc)));
		
		SAXSource exiSource = new EXISource(exiFactory);
		exiSource.setInputSource(is);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		
		if (mode == decodeMode.STRTOFILE || mode == decodeMode.FILETOFILE)
		{
			String filename = null;
			if (mode == decodeMode.STRTOFILE)
			{
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				filename = new String("out."+timestamp.toString()+".xml");
			} else
				filename = existr + ".xml";
			
			res = new StreamResult(filename);
			result = new String("File written in '" + filename + "'");
		} else {
			outputStream = new ByteArrayOutputStream();
			res = new StreamResult(outputStream);
		}	
		
		transformer.transform(exiSource, res);	
		
		if (mode == decodeMode.FILETOSTR || mode == decodeMode.STRTOSTR)
			result = new String(outputStream.toByteArray());
		
		return result;
	}

	public static String fuzzyExiEncoder(String xmlstr, decodeMode dmode, Grammars[] grammars) throws IOException, SAXException
	{
		/*
		 * 		Enumerate V2G grammar to decode EXI data
		 * 		In(1): Input string
		 * 		In(2): (decodeMode) Input/Output modes
		 * 		Out: Decoded result string
		 */

		String result = null;
		String inputsc = null;
		Grammars grammar = null;

		if (dmode == decodeMode.FILETOSTR || dmode == decodeMode.FILETOFILE)
		{ // In case the input is a file
			byte[] rbytes = Files.readAllBytes(Paths.get(xmlstr));
			inputsc = new String(rbytes);
		} else {
			inputsc = xmlstr;
		}

		/* Selects grammar intelligenly */
		if (inputsc.contains("supportedAppProtocol"))
		{ // select AppProtocol schema to set V2G grammar
			grammar = grammars[1];
		} else if (inputsc.contains("V2G_Message")) { // select MSG DEF
			grammar = grammars[0];
		} else { // XMLDSIG by default
			grammar = grammars[2];
		}

		try {
			result = Xml2Exi(inputsc, xmlstr, dmode, grammar);
		} catch(EXIException e)
		{
			e.printStackTrace();
		}

		return result;
	}
	
	public static String fuzzyExiDecoded(String strinput, decodeMode dmode, Grammars[] grammars)
	{
		/*
		 * 		Enumerate V2G grammar to decode EXI data
		 * 		In(1): Input string
		 * 		In(2): (decodeMode) Input/Output modes
		 * 		Out: Decoded result string
		 */
	
		String result = null;
		
		try {
			result = Exi2Xml(strinput, dmode, grammars[0]);
		} catch (Exception e1) {
			try {
				result = Exi2Xml(strinput, dmode, grammars[1]);
			} catch (Exception e2) {
				try {
					result = Exi2Xml(strinput, dmode, grammars[2]);
				} catch (EXIException e3) {
					// do nothing
					//e3.printStackTrace();
				} catch (Exception b3) {
					// do nothing
					//b3.printStackTrace();
				}
			}
		}
		
		return result;
	}
}
