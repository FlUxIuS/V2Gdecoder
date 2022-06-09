package com.fluxlus.V2Gdecoder.server;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.SAXException;

import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.grammars.Grammars;	

import com.fluxlus.V2Gdecoder.dataprocess.dataprocess;
import com.fluxlus.V2Gdecoder.dataprocess.decodeMode;

public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;
    protected Grammars[] grammars   = null;

    public WorkerRunnable(Socket clientSocket, Grammars[] grammars, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
        this.grammars     = grammars;
    } 
    
    public static Map<String, String> parseHTTPHeaders(InputStream inputStream)
            throws IOException {
        int charRead;
        StringBuffer sb = new StringBuffer();
        while (true) {
            sb.append((char) (charRead = inputStream.read()));
            if ((char) charRead == '\r') {          
                sb.append((char) inputStream.read()); 
                charRead = inputStream.read();       
                if (charRead == '\r') {
                    sb.append((char) inputStream.read());
                    break;
                } else {
                    sb.append((char) charRead);
                }
            }
        }

        String[] headersArray = sb.toString().split("\r\n");
        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < headersArray.length - 1; i++) {
            headers.put(headersArray[i].split(": ")[0],
                    headersArray[i].split(": ")[1]);
        }

        return headers;
    }
    
    public static String parseHTTPBody(InputStream inputStream)
            throws IOException {
    	StringBuilder stringBuilder = new StringBuilder();
    	int length = inputStream.available();
    	InputStreamReader test = new InputStreamReader(inputStream);
    	
    	BufferedReader bufferedReader = new BufferedReader(test);
        char[] charBuffer = new char[4096];
        int bytesRead;
        
        while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
        	stringBuilder.append(charBuffer, 0, bytesRead);
            if (stringBuilder.length() <= length) {
            	break;
            }
        }

       return stringBuilder.toString();
    }
    
    public void run() {
        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            //long time = System.currentTimeMillis();
            Map<String, String> headers = parseHTTPHeaders(input);
            System.out.println(headers);
            String body = parseHTTPBody(input);
            String result = null;
            System.out.println(headers.get("Format").toString());
            
            if (headers.get("Format").contains("EXI"))
            {
            	result = dataprocess.fuzzyExiDecoded(body, decodeMode.STRTOSTR, this.grammars);
            } else {
            	try {
					result = dataprocess.fuzzyExiEncoder(body, decodeMode.STRTOSTR, this.grammars);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }  
            //System.out.println(result);
            output.write(("HTTP/1.1 200 OK\n\n" + result +
            		"").getBytes());
            output.close();
            input.close();
            
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}
