package it.anseltechnology.rfid.sdklibrary.plugin;

import org.apache.cordova.CordovaPlugin;

import java.util.Locale;

import javax.security.auth.callback.Callback;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.Observer;

import it.anseltechnology.rfid.sdklibrary.RfidLibraryInterface;
import it.anseltechnology.rfid.sdklibrary.core.enums.ConnectionState;
import it.anseltechnology.rfid.sdklibrary.core.interfaces.callbacks.OnConnectionStateListener;
import it.anseltechnology.rfid.sdklibrary.core.interfaces.callbacks.OnRfidResultListener;

/**
 * This class echoes a string called from JavaScript.
 */
public class RfidSdkLibrary extends CordovaPlugin {

    private static final String INIT = "init";
    private static final String CONNECT = "connect";
    private static final String START_RFID = "start_rfid";
    private static final String STOP_RFID = "stop_rfid";

    CallbackContext myCallbackContext;
    private RfidLibraryInterface rfidInterface;

    private static CallbackContext myRfidCallbackContext = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.myCallbackContext = callbackContext;

        if (action.equals(INIT)) {
            String deviceType = args.getString(0);

            this.initDevice(deviceType);
            
            return true;
        }
        else if (action.equals(CONNECT)) {
            String connectType = args.getString(0);

            this.connectDevice(connectType);

            return true;
        }
        else if (action.equals(START_RFID)) {
            myRfidCallbackContext = callbackContext;

            this.startRfidScan();

            return true;
        }
        else if (action.equals(STOP_RFID)) {
            this.stopRfidScan();

            return true;
        }
        return false;
    }

    private void initDevice(String deviceType) {
        try {
            Context context = this.cordova.getActivity().getApplicationContext();
            this.rfidInterface = new RfidLibraryInterface();

            this.rfidInterface.initDevice(deviceType, context);

            myCallbackContext.success();
        }
        catch (Exception e) {
            e.printStackTrace();
            myCallbackContext.error(e.getMessage());
        }
    }

    private void connectDevice(String connectType) {
        try {            
            this.rfidInterface.setOnConnectionStateListener(new OnConnectionStateListener() {
                public void onConnectionStateChange(ConnectionState state) {
                    if (state == ConnectionState.CONNECTED) {
                        
                        setRfidListener();

                        myCallbackContext.success("Device connected!");
                    }
                }
            });

            this.rfidInterface.connectDevice(connectType);
        }
        catch (Exception e) {
            e.printStackTrace();
            myCallbackContext.error(e.getMessage());
        }
    }

    private void startRfidScan() {

        try {
            this.rfidInterface.startRfidScan(false);

            // Send no result for synchronous callback
            PluginResult pluginresult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginresult.setKeepCallback(true);
            this.myCallbackContext.sendPluginResult(pluginresult);
        }
        catch (Exception e) {
            e.printStackTrace();
            myCallbackContext.error(e.getMessage());
        }
        
    }

    private void stopRfidScan() {
        this.rfidInterface.stopRfidScan();

        myCallbackContext.success();
    }

    private void setRfidListener() {
        this.rfidInterface.setOnRfidResultListener(new OnRfidResultListener() {
            public void onRfidFound(String rfid) {
                myRfidCallbackContext.success("{\"status\":\"OK\", \"body\":\""+rfid+"\"}");
                myRfidCallbackContext = null;
            }

            public void onRfidNotFound() {
                
            }

            public void onRfidTooManyFound(int tagNumber) {
                myRfidCallbackContext.success("{\"status\":\"KO\", \"error\":\"Troppi tag in campo (letti "+ tagNumber +" tag)\"}");
                myRfidCallbackContext = null;
            }

            public void onRfidInventory() {

            }

            public void onRfidReadStart() {
                
            }

            public void onRfidReadStop() {
                
            }
        });
    }
}
