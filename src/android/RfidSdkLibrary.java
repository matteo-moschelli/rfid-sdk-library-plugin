package it.anseltechnology.rfid.sdklibrary.plugin;

import org.apache.cordova.CordovaPlugin;

import java.util.Observer;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import it.anseltechnology.rfid.sdklibrary.RfidLibraryInterface;
import it.anseltechnology.rfid.sdklibrary.core.enums.ConnectionState;

/**
 * This class echoes a string called from JavaScript.
 */
public class RfidSdkLibrary extends CordovaPlugin {

    private static final String INIT = "init";
    private static final String CONNECT = "connect";

    CallbackContext myCallbackContext;
    private RfidLibraryInterface rfidInterface;

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
            this.rfidInterface.connectDevice(connectType);

            Observer<ConnectionState> mConnectionStateObserver = state -> {
                if (state == ConnectionState.CONNECTED) {
                    myCallbackContext.success("Dispositivo connesso");
                }
            };            
        }
        catch (Exception e) {
            e.printStackTrace();
            myCallbackContext.error(e.getMessage());
        }
    }
}
