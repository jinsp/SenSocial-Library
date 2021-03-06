/*******************************************************************************
 *
 * SenSocial Middleware
 *
 * Copyright (c) ${2014}, University of Birmingham
 * Abhinav Mehrotra, axm514@cs.bham.ac.uk
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Birmingham 
 *       nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior
 *       written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE ABOVE COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *******************************************************************************/
//package com.ubhave.sensocial.filters;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//
//public class FilterParser {
//
//	private final String TAG="SNnMB";
//	private SharedPreferences sp;
//	private SharedPreferences.Editor ed;
//	private Context context;
//	private String rd = "", condition_name, filter_name, configuration_name, reqdata_name, activity_name, sensor_name, sensor_config;
//	private Set<String> filter = new HashSet<String>();
//	private Set<String> configuration = new HashSet<String>();
//	private Set<String> condition = new HashSet<String>();
//	private Set<String> sensorList = new HashSet<String>();
//	private Boolean configAllowed;
//	
//	public FilterParser(Context context){
//		this.context=context;
//		this.sp=context.getSharedPreferences("SSDATA",0);
//		this.ed=sp.edit();
//	}
////	
////	public void parseXML(){
////		try {
////			final String dest_file_path = "/mnt/sdcard/dwn1.xml";
////			File fXmlFile = new File(dest_file_path);
////			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
////			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
////			Document doc = dBuilder.parse(fXmlFile);
////			
////			doc.getDocumentElement().normalize();
////			if(doc.getDocumentElement().getNodeName().equals("filter")){
////				filter.clear();
////				sensorList.clear();
////				NodeList nList = doc.getElementsByTagName("configuration");
////				for (int temp=0;temp<nList.getLength();temp++) {
////					Node nNode = nList.item(temp);
////					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
////						Element eElement = (Element) nNode;
////						configuration_name=eElement.getAttribute("name");
////						System.out.println("config name: "+configuration_name);
////						filter.add(configuration_name);
////						configuration.clear();
////						
////						NodeList nList1 = doc.getElementsByTagName("condition");
////						System.out.println(nList1.getLength());
////						for(int i=0;i<nList1.getLength();i++){
////							Node nNode1 = nList1.item(i);
////							if (nNode1.getNodeType() == Node.ELEMENT_NODE) {
////								Element eElement1 = (Element) nNode1;
////								condition_name=eElement1.getAttribute("name");
////								System.out.println("condition name: "+condition_name);
////								configuration.add(condition_name);
////								condition.clear();
////								
////								System.out.println("length: "+eElement1.getChildNodes().getLength());
////								for(int j=0;j<eElement1.getChildNodes().getLength();j++){
////									if(eElement1.getChildNodes().item(j).getNodeType()==Node.ELEMENT_NODE){
////										activity_name=((Element)eElement1.getChildNodes().item(j)).getAttribute("name");
////										System.out.println("Activity: "+activity_name);
////										condition.add(activity_name);
////										if(activity_name.equalsIgnoreCase("all")){
////											sensorList.add(activity_name);
////										}
////										else{
////											sensorList.add(SensorConfiguration.getSensorNameForActivity(activity_name));											
////										}
////									}									
////								}
////								ed.putStringSet(condition_name, condition);
////								ed.commit();
////								
////							}
////						}
////						
////						NodeList nList2 = doc.getElementsByTagName("required_data");
////						System.out.println(nList2.getLength());
////						for(int i=0;i<nList2.getLength();i++){
////							Node nNode2 = nList2.item(temp);
////							if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
////								Element eElement2 = (Element) nNode2;
////								
////								System.out.println("data length: "+eElement2.getChildNodes().getLength());
////								for(int j=0;j<eElement2.getChildNodes().getLength();j++){
////									if(eElement2.getChildNodes().item(j).getNodeType()==Node.ELEMENT_NODE){
////										System.out.println("element: "+((Element)eElement2.getChildNodes().item(j)).getAttribute("name"));
////										rd="RD"+((Element)eElement2.getChildNodes().item(j)).getAttribute("name");
////										
////										
////										if(((Element)((Element) eElement2.getChildNodes().item(j)).getElementsByTagName("server").item(0)).getAttribute("required").equals("true")){
////											if(((Element)((Element) eElement2.getChildNodes().item(j)).getElementsByTagName("server").item(0)).getAttribute("data").equals("raw")){
////												System.out.println("Server: YES, RAW");
////												rd+="SYR";
////											}
////											else{
////												System.out.println("Server: YES, CLASSIFIED");
////												rd+="SYC";
////											}
////										}
////										else{
////											System.out.println("Server: NO");
////											rd+="SNO";
////										}
////											
////										if(((Element)((Element) eElement2.getChildNodes().item(j)).getElementsByTagName("client").item(0)).getAttribute("required").equals("true")){
////											if(((Element)((Element) eElement2.getChildNodes().item(j)).getElementsByTagName("client").item(0)).getAttribute("data").equals("raw")){
////												System.out.println("Client: YES, RAW");
////												rd+="CYR";
////											}
////											else{
////												System.out.println("Client: YES, CLASSIFIED");
////												rd+="CYC";
////											}
////										}
////										else{
////											System.out.println("Client: NO");
////											rd+="CNO";
////										}										
////									}									
////								}
////							}
////						}
////						sensor_name=rd.substring(0, rd.length()-6);
////						sensor_config=rd.substring(rd.length()-6);
////						configAllowed=new PrivacyPolicyDescriptorParser(context).isAllowed(sensor_name,sensor_config);
////						if(configAllowed){
////							configuration.add(rd);							
////							ed.putStringSet(configuration_name, configuration);
////							ed.commit();
////						}
////						else{
////							for(String conditions:configuration){
////								ed.remove(conditions);
////							}	
////							ed.commit();
////							Log.e(TAG, "Error in "+configuration_name+". Required data configuration does not matches Privacy Policy." +
////									"\n All conditions for this configuration are removed.");
////						}
////													
////					}					
////				}
////
////				ed.putStringSet("ConfigurationSet", filter);
////				ed.commit();
////				new SensorConfiguration(context).subscribeSensors(sensorList);
////				fXmlFile.delete();
////				Log.d(TAG, "Sensors subscribed as per the filter and Filter file deleted");
////				ed.putBoolean("SensorsConfigured", true);
////				ed.commit();
////			}
////			else{
////				Log.e(TAG, "No root node found as: <filter> .....s </filter>");
////			}
////		} catch (Exception e) {
////			Log.e(TAG, "Error encountered: "+e.toString());
////		}
////	}
////	
//	
//}
