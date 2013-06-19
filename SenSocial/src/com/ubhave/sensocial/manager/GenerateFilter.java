package com.ubhave.sensocial.manager;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.Context;
import android.os.Environment;

import com.sensocial.R;

public class GenerateFilter {

	protected static void createXML(Context context,ArrayList<String> activities, String streamConfig, String sensorName, String sensorDataType){
		try
		{
			
			Element rootElement,mainRoot;
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();

			File file = new File(Environment.getExternalStorageDirectory(), "filter.xml"); 
			if (file.exists() && file.length()!=0) 
			{ 
				System.out.println("file found");
				doc = docBuilder.parse(file); 
				mainRoot = doc.getDocumentElement(); 
			} 
			else 
			{ 
				mainRoot = doc.createElement("Filter");
				doc.appendChild(mainRoot); 
			} 

			rootElement = doc.createElement("Configuration");
			rootElement.setAttribute("name", streamConfig);
			rootElement.setAttribute("sense", "false");
			mainRoot.appendChild(rootElement);

			Element condition = doc.createElement("Condition");
			condition.setAttribute("name", "c1");
			rootElement.appendChild(condition);
			int i=2, j=1;
			Element activity;
			for(String s:activities){

				if(!s.equalsIgnoreCase("LogicalOR")){
					activity = doc.createElement("activity"+j++);
					activity.setAttribute("name", s);
					condition.appendChild(activity);
				}
				else{
					condition = doc.createElement("Condition");
					condition.setAttribute("name", "c"+i++);
					rootElement.appendChild(condition);
					j=1;
				}				

			}

			Element reqData = doc.createElement("required_data");
			reqData.setAttribute("sensor", sensorName);
			reqData.setAttribute("location", "client");
			reqData.setAttribute("type", sensorDataType);
			rootElement.appendChild(reqData);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			StreamResult result =  new StreamResult(new File(Environment.getExternalStorageDirectory(), "filter.xml"));
			transformer.transform(source, result);

			System.out.println("GenerateFilter Done");

		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

}