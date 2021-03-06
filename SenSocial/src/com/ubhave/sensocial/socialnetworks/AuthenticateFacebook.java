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
package com.ubhave.sensocial.socialnetworks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.ubhave.sensocial.tcp.ClientServerCommunicator;

/**
 * Provides the methods to authenticate user with their Facebook account.
 */
@SuppressWarnings({ "deprecation", "unused" })
public class AuthenticateFacebook {

	private Context context;
	final private String TAG = "SNnMB";
	private Facebook fb;	
	private SharedPreferences sp;
	private AsyncFacebookRunner asyncRunner;
	String appId;
	String clientSecretId;
	
	public AuthenticateFacebook(Context context, String clientId, String clientSecretId) {
		this.context=context;
		sp=context.getSharedPreferences("SSDATA",0);
		this.appId=clientId;
		this.clientSecretId=clientSecretId;
	}

	/**
	 * This method should be called inside the Facebook-Login button's onClickListner. <br>
	 * It authenticates the user via Facebook.
	 * @param currentActivity Activity in which it is called.
	 * @return GraphUser object. This contains profile data of authenticated user.
	 */
	public void tryLogin(final Activity activity) {
		fb = new Facebook(appId);
    	asyncRunner = new AsyncFacebookRunner(fb);
		fb.authorize(activity,new String[] {"read_stream"}, new DialogListener() {				
			public void onFacebookError(FacebookError e) {
				Log.e(TAG, e.toString());
				Toast.makeText(activity.getApplicationContext(), "Facebook Error. PLease try again!", Toast.LENGTH_SHORT).show();
			}				
			public void onError(DialogError e) {
				Log.e(TAG, e.toString());
				Toast.makeText(activity.getApplicationContext(), "Error. PLease try again!", Toast.LENGTH_SHORT).show();
			}				
			public void onComplete(Bundle values) {
				Editor editor =sp.edit();
				editor.putString("access_token", fb.getAccessToken());
				editor.putLong("access_expires", fb.getAccessExpires());
				editor.commit();				     
				new FacebookExtendedToken().execute();
			}				
			public void onCancel() {
				Toast.makeText(activity.getApplicationContext(), "Page Canceled. Please Try Again.", Toast.LENGTH_SHORT).show();
			}
		});	
	}
	
	/**
	 * This method should be inside onActivityResult. <br/>
	 * Remember to create onActivityResult method in the same activity where tryLogin for Facebook was called. <br/>
	 * Pass all the argument which are received by onActivityResult.
	 * @param currentActivity
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void insideOnActivityResult(Activity currentActivity, int requestCode, int resultCode, Intent data) {
		fb.authorizeCallback(requestCode, resultCode, data);
	}
	
	/**
	 * Method to exchange the temporary token with permanent(extended) token from Facebook 	 *
	 */
	private class FacebookExtendedToken extends AsyncTask<Void,Void,Void>{

		protected Void doInBackground(Void... params) {
			try {
				JSONObject obj =null;
				String jsonUser =fb.request("me");	
				Log.d(TAG, "FB json: "+jsonUser);
				obj=Util.parseJson(jsonUser);	    				
				String userId = obj.optString("id");
				String userName =obj.optString("name");   				
				Editor editor =sp.edit();
				editor.putString("fbusername", userId);
				editor.putBoolean("fblogin", true);	
				editor.putString("name", userName);
				editor.commit();
				Log.d(TAG, "FB name: "+sp.getString("name", null));
				Log.d(TAG, "AT old: "+sp.getString("access_token", null));
				URL url=new URL("https://graph.facebook.com/oauth/access_token?grant_type=fb_exchange_token&client_id="+appId+
						"&client_secret="+clientSecretId+"&fb_exchange_token="+sp.getString("access_token", null));
				InputStream Istream=url.openConnection().getInputStream();
				BufferedReader r = new BufferedReader(new InputStreamReader(Istream));
				StringBuilder total = new StringBuilder();
				String line;
				while ((line = r.readLine()) != null) {
				    total.append(line);
				}
				String new_access_token= total.toString();
				editor.putString("fbtoken", new_access_token);
				editor.commit();
				Log.d(TAG, "AT new: "+sp.getString("fbtoken", null));

				if(sp.getBoolean("useridbyfacebook", false)==false){
					ClientServerCommunicator.registerFacebook(context, sp.getString("name", "null"), 
							sp.getString("userid", "null"),	sp.getString("fbusername", "null"),  sp.getString("fbtoken", "null"));					
				}
				else if(sp.getString("userid", "null").equals("null")){
					String user_id=  generateUserId(sp.getString("fbusername", "null"));
					Editor ed=sp.edit();
					ed.putString("userid", user_id);
					ed.commit();
					ClientServerCommunicator.registerUser(context,user_id, sp.getString("deviceid", "null"), sp.getString("bluetoothmac", "null"));
					ClientServerCommunicator.registerFacebook(context, sp.getString("name", "null"), 
							user_id, sp.getString("fbusername", "null"),  sp.getString("fbtoken", "null"));					
				}
			} catch (NullPointerException e) {
		    	Log.e(TAG, "NullPointerException Error:"+e.toString());
            }catch (FacebookError e) {
		    	Log.e(TAG, "FacebookError Error:"+e.toString());
			} catch (JSONException e) {
		    	Log.e(TAG, "JSONException Error:"+e.toString());
			} catch (MalformedURLException e) {
		    	Log.e(TAG, "MalformedURLException Error:"+e.toString());
			} catch (IOException e) {
		    	Log.e(TAG, "IOException Error:"+e.toString());
			}
			return null; 	

		}
		
		private String generateUserId(String id){
			id="ssuid"+ id.trim();
			return id;
		}
		
	}

		

}
