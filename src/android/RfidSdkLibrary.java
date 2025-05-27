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

import com.google.gson.Gson;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.Observer;

import it.anseltechnology.rfid.sdklibrary.RfidLibraryInterface;
import it.anseltechnology.rfid.sdklibrary.core.enums.ConnectionState;
import it.anseltechnology.rfid.sdklibrary.core.interfaces.callbacks.OnConnectionStateListener;
import it.anseltechnology.rfid.sdklibrary.core.interfaces.callbacks.OnRfidResultListener;
import it.anseltechnology.rfid.sdklibrary.data.RfidInventoryResult;

/**
 * This class echoes a string called from JavaScript.
 */
public class RfidSdkLibrary extends CordovaPlugin {

    private static final String INIT = "init";
    private static final String CONNECT = "connect";
    private static final String DISCONNECT = "disconnect";
    private static final String START_RFID = "start_rfid";
    private static final String STOP_RFID = "stop_rfid";
    private static final String VERSION = "version";

    CallbackContext myCallbackContext;
    private RfidLibraryInterface rfidInterface;

    private static CallbackContext myRfidCallbackContext = null;
    private static CallbackContext myConnectStateContext = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.myCallbackContext = callbackContext;

        if (action.equals(INIT)) {
            String deviceType = args.getString(0);

            this.initDevice(deviceType);
            
            return true;
        }
        else if (action.equals(CONNECT)) {
            myConnectStateContext = callbackContext;

            String connectType = args.getString(0);

            this.connectDevice(connectType);

            return true;
        }
        else if (action.equals(DISCONNECT)) {
            this.disconnectDevice();

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
        else if (action.equals(VERSION)) {
            this.getVersion();

            return true;
        }
        return false;
    }

    private void initDevice(String deviceType) {
        try {
            Context context = this.cordova.getActivity().getApplicationContext();
            this.rfidInterface = new RfidLibraryInterface();

            this.rfidInterface.initDevice(deviceType, context);
            setRfidListener();

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
                    PluginResult res = new PluginResult(PluginResult.Status.OK, "{\"connectState\":\"" + state.toString() + "\"}");
                    res.setKeepCallback(true);
                    myConnectStateContext.sendPluginResult(res);
                }
            });
            
            this.rfidInterface.connectDevice(connectType);

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

    private void disconnectDevice() {
        this.rfidInterface.disconnectDevice();

        myCallbackContext.success();
    }

    private void startRfidScan() {

        try {
            this.rfidInterface.startRfidScan(true); // true is for reading Inventory and not single tag

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

        myRfidCallbackContext = null;

        myCallbackContext.success();
    }

    private void setRfidListener() {
        this.rfidInterface.setOnRfidResultListener(new OnRfidResultListener() {
            public void onRfidFound(String rfid) {
                PluginResult res = new PluginResult(PluginResult.Status.OK, "{\"status\":\"OK\", \"body\":\""+rfid+"\"}");
                res.setKeepCallback(true);
                myRfidCallbackContext.sendPluginResult(res);
            }

            public void onRfidNotFound() {
                
            }

            public void onRfidTooManyFound(int tagNumber) {
                PluginResult res = new PluginResult(PluginResult.Status.OK, "{\"status\":\"KO\", \"error\":\"Troppi tag in campo (letti "+ tagNumber +" tag)\"}");
                res.setKeepCallback(true);
                myRfidCallbackContext.sendPluginResult(res);
            }

            public void onRfidInventory(RfidInventoryResult result) {
                Gson gson = new Gson();

                PluginResult res = new PluginResult(PluginResult.Status.OK, "{\"type\":\"INVENTORY_RESULT\", \"body\":" + gson.toJson(result) + "}");
                res.setKeepCallback(true);
                myRfidCallbackContext.sendPluginResult(res);
            }

            public void onRfidReadStart() {
                PluginResult res = new PluginResult(PluginResult.Status.OK, "{\"type\":\"RFID_STATUS\", \"active\": true}");
                res.setKeepCallback(true);
                myRfidCallbackContext.sendPluginResult(res);
            }

            public void onRfidReadStop() {
                PluginResult res = new PluginResult(PluginResult.Status.OK, "{\"type\":\"RFID_STATUS\", \"active\": false}");
                res.setKeepCallback(true);
                myRfidCallbackContext.sendPluginResult(res);
            }
        });
    }

    void getVersion() {
        String version = rfidInterface.getLibraryVersion();

        PluginResult res = new PluginResult(PluginResult.Status.OK, version);
        //pluginresult.setKeepCallback(true);
        myCallbackContext.sendPluginResult(res);
    }
}
