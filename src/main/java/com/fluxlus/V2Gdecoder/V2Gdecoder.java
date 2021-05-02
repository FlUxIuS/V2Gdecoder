package com.fluxlus.V2Gdecoder;

import com.fluxlus.V2Gdecoder.server.MultiThreadedServer;
import java.io.IOException;
import org.apache.commons.cli.*;
import org.xml.sax.SAXException;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.grammars.Grammars;	
import com.siemens.ct.exi.grammars.GrammarFactory;
import com.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import com.fluxlus.V2Gdecoder.dataprocess.*;

/*
 *  Copyright (C) V2Gdecoder by FlUxIuS (Sebastien Dudek)
 */

public class V2Gdecoder {	
	public static void main(String[] args) throws IOException, SAXException, EXIException {
		Options options = new Options();
		Option sinput = new Option("s", "string", true, "string to decode");
		sinput.setRequired(false);
		options.addOption(sinput);
		Option finput = new Option("f", "file", true, "input file path");
		finput.setRequired(false);
		options.addOption(finput);
		Option output = new Option("o", "output", false, "output file in a dedicated path");
		output.setRequired(false);
		options.addOption(output);
		Option xmlformat = new Option("x", "xml", false, "XML format");
		xmlformat.setRequired(false);
		options.addOption(xmlformat);
		Option exiform = new Option("e", "exi", false, "EXI format");
		exiform.setRequired(false);
		options.addOption(exiform);
		Option webserv = new Option("w", "web", false, "Webserver");
		webserv.setRequired(false);
		options.addOption(webserv);
		
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;
		
		try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("V2GEXI Helper", options);
            System.exit(1);
        }

		String inputFilePath = cmd.getOptionValue("file");
		if (!cmd.hasOption("file"))
		{
			inputFilePath = cmd.getOptionValue("string");
		}
		//String outputFilePath = cmd.getOptionValue("output"); /* TODO: custom output file */
		decodeMode dmode = decodeMode.STRTOSTR;
		String result = null;

		/* Initialize grammars */
		Grammars[] grammars = {null, null, null};

		/* BOTTLENECK: slow operation */
		try {
			grammars[0] = GrammarFactory.newInstance().createGrammars("." + GlobalValues.SCHEMA_PATH_MSG_DEF.toString());
		} catch (EXIException e) {
			e.printStackTrace();			
		}
		try {
			grammars[1] = GrammarFactory.newInstance().createGrammars("." + GlobalValues.SCHEMA_PATH_APP_PROTOCOL.toString());
		} catch (EXIException e) {
			e.printStackTrace();			
		}
		try {
			grammars[2] = GrammarFactory.newInstance().createGrammars("." + GlobalValues.SCHEMA_PATH_XMLDSIG.toString());
		} catch (EXIException e) {
			e.printStackTrace();			
		}

        if (cmd.hasOption("xml"))
        { // We wan to encode a XML input
        	if (cmd.hasOption("file"))
    		{
    			dmode = decodeMode.FILETOSTR;
    			if (cmd.hasOption("output"))
    				dmode = decodeMode.FILETOFILE;
    		} else {
    			dmode = decodeMode.STRTOSTR;
    			if (cmd.hasOption("output"))
    				dmode = decodeMode.STRTOSTR;
    		}
        	result = dataprocess.fuzzyExiEncoder(inputFilePath, dmode, grammars);
        	if (!cmd.hasOption("output"))
        		System.out.println(result);
        } else if (cmd.hasOption("exi")) { // We wan to decode an EXI input
        	if (cmd.hasOption("file"))
    		{ // if a file path is provided
    			dmode = decodeMode.FILETOSTR;
    			if (cmd.hasOption("output"))
    				dmode = decodeMode.FILETOFILE;
    		} else { // if input is provided in stdin
    			dmode = decodeMode.STRTOSTR;
    			if (cmd.hasOption("output"))
    				dmode = decodeMode.STRTOFILE;
    		}
        	result = dataprocess.fuzzyExiDecoded(inputFilePath, dmode, grammars);
        	if (!cmd.hasOption("output"))
        	{ // output in stdout
        		System.out.println(result);
        	}
        } else if (cmd.hasOption("web")) { // run a encoder/decoder service on port TCP 9000
            MultiThreadedServer server = new MultiThreadedServer(9000, grammars);
            new Thread(server).start();
        }
	}
}
