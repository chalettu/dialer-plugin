
package net.ninjaenterprises.dialer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.content.ActivityNotFoundException;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;


/**
 * Sample PhoneGap plugin to call Dialer app
 * @author chale
 *
 */
public class DialerPlugin extends CordovaPlugin{
	public static final String ACTION_DIAL_NUMBER = "dialNumber";
@Override
public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

	try {
	    if (ACTION_DIAL_NUMBER.equals(action)) { 
	             JSONObject arg_object = args.getJSONObject(0);
	             String phonenumber= arg_object.getString("phone_number");
	            // String phonenumber = "19195740046,,123456#,,,,,1"; // , = pauses
	  	       String encodedPhonenumber="";  		
	  		 encodedPhonenumber = URLEncoder.encode(phonenumber, "UTF-8");
	  	
	  		 Intent calIntent= new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + encodedPhonenumber));
	  			this.cordova.getActivity().startActivity(calIntent);
	             
	       callbackContext.success();
	       return true;
	    }
	    callbackContext.error("Invalid action");
	    return false;
	} catch(Exception e) {
	    System.err.println("Exception: " + e.getMessage());
	    callbackContext.error(e.getMessage());
	    return false;
	} 
      	        
	
}
	
    
    
    /*
     * 
     *  try {
		        Intent callIntent = new Intent(Intent.ACTION_CALL);
		       // callIntent.setData(Uri.parse("tel:+19195740046,,123456#,,,,,1"));
		        String phonenumber = "19195740046,,123456#,,,,,1"; // , = pauses
		       String encodedPhonenumber="";
			try {
				encodedPhonenumber = URLEncoder.encode(phonenumber, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + encodedPhonenumber)));
		        
		        
		        
		        startActivity(callIntent);
		    } catch (ActivityNotFoundException e) {
		        Log.e("helloandroid dialing example", "Call failed", e);
		    }
     * 
     * */
} // end class
