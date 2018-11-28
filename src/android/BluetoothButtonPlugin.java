package com.zzl.intelligence;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaActionSound;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class BluetoothButtonPlugin extends CordovaPlugin {

  private static final String LOG_TAG = "BleButtonManager";

  BroadcastReceiver receiver;

  private CallbackContext BleButtonCallbackContext = null;

  private BluetoothAdapter ba;

  public BluetoothButtonPlugin(){
    this.receiver = null;
  }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
      if (action.equals("start")) {
        if(this.BleButtonCallbackContext != null){
          removeBleButtonListener();
        }
        this.BleButtonCallbackContext = callbackContext;
        this.ba = BluetoothAdapter.getDefaultAdapter();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.media.ACTION_SCO_AUDIO_STATE_UPDATED");
        if(this.receiver == null){
          this.receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
              updateBleButtonInfo(intent);
            }
          };
          webView.getContext().registerReceiver(this.receiver, intentFilter);
        }
        // Don't return any result now, since status results will be sent when events come in from broadcast receiver
        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
        return true;
      }
      else if(action.equals("stop")){
        removeBleButtonListener();
        this.sendUpdate("", false); // release status callback in JS side
        this.BleButtonCallbackContext = null;
        callbackContext.success();
        return true;
      }
      return false;
    }

  public void onDestroy() {
    removeBleButtonListener();
  }

  /**
   * Stop battery receiver.
   */
  public void onReset() {
    removeBleButtonListener();
  }

  private void updateBleButtonInfo(Intent intent) {
    int state = intent.getIntExtra("android.media.extra.SCO_AUDIO_STATE", 0);
    if(state == 1){
      sendUpdate(Boolean.toString(true), true);
    }
  }

  private void sendUpdate(String message, boolean keepCallback) {
    if (this.BleButtonCallbackContext != null) {
      PluginResult result = new PluginResult(PluginResult.Status.OK, message);
      result.setKeepCallback(keepCallback);
      this.BleButtonCallbackContext.sendPluginResult(result);
    }
  }

  private void removeBleButtonListener() {
    if (this.receiver != null) {
      try {
        webView.getContext().unregisterReceiver(this.receiver);
        this.receiver = null;
      } catch (Exception e) {
        LOG.e(LOG_TAG, "Error unregistering ble button receiver: " + e.getMessage(), e);
      }
    }
  }
}
