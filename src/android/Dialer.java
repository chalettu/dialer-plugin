
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
    
	/**
	 * Action to initialize speech kit
	 */
	public static final String ACTION_INIT = "initSpeechKit";
	/**
	 * Action to start recognition
	 */
	public static final String ACTION_START_RECO = "startRecognition";
	/**
	 * Action to stop recognition
	 */
	public static final String ACTION_STOP_RECO = "stopRecognition";
	/**
	 * Action to get recognition results
	 */
	public static final String ACTION_GET_RECO_RESULT = "getRecoResult";
	/**
	 * Action to clean up speech kit after initialization
	 */
	public static final String ACTION_CLEANUP = "cleanup";
	/**
	 * Action to start TTS playback
	 */
	public static final String ACTION_PLAY_TTS = "startTTS";
	/**
	 * Action to stop TTS playback
	 */
	public static final String ACTION_STOP_TTS = "stopTTS";
	/**
	 * Action to setup a callback id to get the next event
	 */
	public static final String ACTION_QUERY_NEXT_EVENT = "queryNextEvent";
	
	/**
	 * Return code - success
	 */
	public static final int RC_SUCCESS = 0;
	/**
	 * Return code - failure
	 */
	public static final int RC_FAILURE = -1;
	/**
	 * Return code - speech kit not initialized
	 */
	public static final int RC_NOT_INITIALIZED = -2;
	/**
	 * Return code - speech recognition not started
	 */
	public static final int RC_RECO_NOT_STARTED = -3;
	/**
	 * Return code - no recognition result is available
	 */
	public static final int RC_RECO_NO_RESULT_AVAIL = -4;
	/**
	 * Return code - TTS playback was not started
	 */
	public static final int RC_TTS_NOT_STARTED = -5;
	/**
	 * Return code - recognition failure
	 */
	public static final int RC_RECO_FAILURE = -6;
	/**
	 * Return code TTS text is invalid
	 */
	public static final int RC_TTS_TEXT_INVALID = -7;
	/**
	 * Return code - TTS parameters are invalid
	 */
	public static final int RC_TTS_PARAMS_INVALID = -8;
	
	/**
	 * Call back event - Initialization complete
	 */
	public static final String EVENT_INIT_COMPLETE = "InitComplete";
	/**
	 * Call back event - clean up complete
	 */
	public static final String EVENT_CLEANUP_COMPLETE = "CleanupComplete";
	/**
	 * Call back event - Recognition started
	 */
	public static final String EVENT_RECO_STARTED = "RecoStarted";
	/**
	 * Call back event - Recognition compelte
	 */
	public static final String EVENT_RECO_COMPLETE = "RecoComplete";
	/**
	 * Call back event - Recognition stopped
	 */
	public static final String EVENT_RECO_STOPPED = "RecoStopped";
	/**
	 * Call back event - Processing speech recognition result
	 */
	public static final String EVENT_RECO_PROCESSING = "RecoProcessing";
	/**
	 * Call back event - Recognition error
	 */
	public static final String EVENT_RECO_ERROR = "RecoError";
	/**
	 * Call back event - Volume update while recording speech
	 */
	public static final String EVENT_RECO_VOLUME_UPDATE = "RecoVolumeUpdate";
	/**
	 * Call back event - TTS playback started
	 */
	public static final String EVENT_TTS_STARTED = "TTSStarted";
	/**
	 * Call back event - TTS playing
	 */
	public static final String EVENT_TTS_PLAYING = "TTSPlaying";
	/**
	 * Call back event - TTS playback stopped
	 */
	public static final String EVENT_TTS_STOPPED = "TTSStopped";
	/**
	 * Call back event - TTS playback complete
	 */
	public static final String EVENT_TTS_COMPLETE = "TTSComplete";
    
    
	// variables to support recognition
	/**
	 * Speech kit reference
	 */
	private SpeechKit speechKit = null;
	/**
	 * Recognition listener
	 */
    private Recognizer.Listener recoListener;
	/**
	 * Recognizer reference
	 */
    private Recognizer currentRecognizer = null;
	/**
	 * Handler reference
	 */
    private Handler handler = null;
	/**
	 * Reference to last result
	 */
    private Recognition.Result [] lastResult = null;
	/**
	 * State variable to track if recording is active
	 */
    private boolean recording = false;
    
    /**
     * ID provided to invoke callback function.
     */
    private CallbackContext recognitionCallbackContext = null;
    
    // variables to support TTS
	/**
	 * Vocalizer reference for text to speech
	 */
    private Vocalizer vocalizerInstance = null;
	/**
	 * Context object for text to speech tracking
	 */
    private Object _lastTtsContext = null;
    /**
     * ID provided to invoke callback function.
     */
    private CallbackContext ttsCallbackContext = null;
    
    public CallbackContext callbackContext;
    /**
     * Method to initiate calls from PhoneGap/javascript API
     *
     * @param action
     * The action method
     *
     * @param data
     * Incoming parameters
     *
     * @param callbackId
     * The call back id
     */
	public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        
		Log.d("NuancePlugin", "NuancePlugin.execute: Entered method. Action = ["+action+"] Call Back Context = ["+callbackContext+"]");
		
		PluginResult result = null;
        
		try{
			
			if (ACTION_INIT.equals(action)) { // INITALIZE
				// initialize sppech kit
				result = initSpeechKit(data, callbackContext);
			}
			else if (ACTION_CLEANUP.equals(action)) {  // CLEANUP
				result = cleanupSpeechKit(data, callbackContext);
			}
			else if (ACTION_START_RECO.equals(action)) {  // START RECOGNITION
				result = startRecognition(data, callbackContext);
			}
			else if (ACTION_STOP_RECO.equals(action)) {  // STOP RECOGNITION
				result = stopRecognition(data, callbackContext);
			}
			else if (ACTION_GET_RECO_RESULT.equals(action)) {  // GET THE LAST RESULT
				Log.d("NuancePlugin", "NuancePlugin.execute: Call to get results.");
				result = getRecoResult(data, callbackContext);
			}
			else if (ACTION_PLAY_TTS.equals(action)) {  // START TTS PLAYBACK
				Log.d("NuancePlugin", "NuancePlugin.execute: Call to start TTS.");
				result = startTTS(data, callbackContext);
			}
			else if (ACTION_STOP_TTS.equals(action)) {  // START TTS PLAYBACK
				Log.d("NuancePlugin", "NuancePlugin.execute: Call to stop TTS.");
				result = stopTTS(data, callbackContext);
			}
			else if (ACTION_QUERY_NEXT_EVENT.equals(action)) {  // add callback for next event
                
				Log.d("NuancePlugin", "NuancePlugin.execute: Call to query next event.");
				JSONObject returnObject = new JSONObject();
				
				ttsCallbackContext = callbackContext;
                
				setReturnCode(returnObject, RC_SUCCESS, "Query Success");
				result = new PluginResult(PluginResult.Status.OK, returnObject);
			}
			else {
				result = new PluginResult(PluginResult.Status.INVALID_ACTION);
				Log.e("NuancePlugin", "NuancePlugin.execute: Invalid action ["+action+"] passed");
			}
            
		}
		catch (JSONException jsonEx) {
			Log.e("NuancePlugin", "NuancePlugin.execute: ["+action+"] Got JSON Exception "+ jsonEx.getMessage(), jsonEx);
			result = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
		}
		catch (Exception e){
			Log.e("NuancePlugin", "NuancePlugin.execute: ["+action+"] Got Exception "+ e.getMessage(), e);
			result = new PluginResult(PluginResult.Status.ERROR);
		}
        
		callbackContext.sendPluginResult(result);
		Log.d("NuancePlugin", "NuancePlugin.execute: Leaving method.");
		return true;
	}
	
		/**
	 * Starts recognition.
	 *
	 * @param data
	 * @param callbackId
	 * @return
	 * @throws JSONException
	 */
	private PluginResult dialNumber(JSONArray data, CallbackContext callbackContext) throws JSONException{
		
		Log.d("NuancePlugin", "NuancePlugin.startRecognition: Entered method.");
		PluginResult result = null;
		String recognitionType = data.getString(0);

		/*
		JSONObject returnObject = new JSONObject();
		if (recoListener != null){
			Log.d("NuancePlugin", "NuancePlugin.execute: LISTENER IS NOT NULL");
		}
		if (speechKit != null){
            
			// get the recognition type
			String recognitionType = data.getString(0);
			Log.d("NuancePlugin", "NuancePlugin.execute: startReco: Reco Type = ["+recognitionType+"]");
			String recognizerType = Recognizer.RecognizerType.Dictation;
			if ("websearch".equalsIgnoreCase(recognitionType)){
				recognizerType = Recognizer.RecognizerType.Search;
			}
			// get the language
			String language = data.getString(1);
			Log.d("NuancePlugin", "NuancePlugin.execute: startReco: Language = ["+language+"]");
			
			recognitionCallbackContext = callbackContext;
			lastResult = null;
			handler = new Handler();
			recoListener = createListener();
            
			// create and start the recognizer reference
			currentRecognizer = speechKit.createRecognizer(recognizerType, Recognizer.EndOfSpeechDetection.Long, language, recoListener, handler);
			currentRecognizer.start();
            
            
			Log.d("NuancePlugin", "NuancePlugin.execute: Recognition started.");
			setReturnCode(returnObject, RC_SUCCESS, "Reco Start Success");
			returnObject.put("event", EVENT_RECO_STARTED);
			
		}
		else{
			Log.e("NuancePlugin", "NuancePlugin.execute: Speech kit was null, initialize not called.");
			setReturnCode(returnObject, RC_NOT_INITIALIZED, "Reco Start Failure: Speech Kit not initialized.");
		}
        */
         try {
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
		        
		     
		  //      startActivity(callIntent);
		    } catch (ActivityNotFoundException e) {
		        Log.e("helloandroid dialing example", "Call failed", e);
		    }

		result = new PluginResult(PluginResult.Status.OK, returnObject);
		result.setKeepCallback(true);
		Log.d("NuancePlugin", "NuancePlugin.startRecognition: Leaving method.");
		return result;
		
	} // end startRecogition
	
	
	
	
    

	    
    }
    
    
    
} // end class
