package com.zzl.intelligence;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaActionSound;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.KeyEvent;

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

  private CallbackContext BleButtonCallbackContext = null;

//  private BroadcastReceiver receiver;

  private MediaSessionCompat mMediaSession;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("start")) {
      if (this.BleButtonCallbackContext != null) {
        removeBleButtonListener();
      }
      this.BleButtonCallbackContext = callbackContext;
      Context context = this.cordova.getContext();

//      IntentFilter intentFilter = new IntentFilter();
//      intentFilter.addAction(Intent.ACTION_VOICE_COMMAND);
//      if(this.receiver == null){
//        this.receiver = new BroadcastReceiver() {
//          @Override
//          public void onReceive(Context context, Intent intent) {
//            updateBleButtonInfo(intent);
//          }
//        };
//        webView.getContext().registerReceiver(this.receiver, intentFilter);
//      }

      mMediaSession = new MediaSessionCompat(context,LOG_TAG);
      mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
      mMediaSession.setCallback(new MediaSessionCompat.Callback() {
        @Override
        public boolean onMediaButtonEvent(Intent intent) {
          String action = intent.getAction();
          if (action.equals(Intent.ACTION_MEDIA_BUTTON)) {
            // 获得KeyEvent对象
            KeyEvent key = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (key == null) {
              return false;
            }
            if (key.getAction() != KeyEvent.ACTION_DOWN) {
              int keycode = key.getKeyCode();
              if (keycode == KeyEvent.KEYCODE_MEDIA_NEXT) {
                // 下一首按键
                sendUpdate("PRESS_NEXT", true);
              } else if (keycode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
                // 上一首按键
                sendUpdate("PRESS_PREVIOUS", true);
              } else if (keycode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                // 播放按键
                sendUpdate("PRESS_PLAY", true);
              } else if (keycode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                // 暂停按键
                sendUpdate("PRESS_PAUSE", true);
              } else if (keycode == KeyEvent.KEYCODE_VOLUME_UP) {
                // 暂停按键
                sendUpdate("PRESS_VOLUME_UP", true);
              } else if (keycode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                // 暂停按键
                sendUpdate("PRESS_VOLUME_DOWN", true);
              }
              // 还可以添加更多按键操作，可以参阅 KeyEvent 类
            }
          }
          return true;
        }
      });
      mMediaSession.setActive(true);

      // Don't return any result now, since status results will be sent when events
      // come in from broadcast receiver
      PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
      pluginResult.setKeepCallback(true);
      callbackContext.sendPluginResult(pluginResult);
      return true;
    } else if (action.equals("stop")) {
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

//  private void updateBleButtonInfo(Intent intent) {
//    sendUpdate(Boolean.toString(true), true);
//  }

  private void sendUpdate(String message, boolean keepCallback) {
    if (this.BleButtonCallbackContext != null) {
      PluginResult result = new PluginResult(PluginResult.Status.OK, message);
      result.setKeepCallback(keepCallback);
      this.BleButtonCallbackContext.sendPluginResult(result);
    }
  }

  private void removeBleButtonListener() {
    if(mMediaSession != null) {
      mMediaSession.setCallback(null);
      mMediaSession.setActive(false);
      mMediaSession.release();
    }
//    if (this.receiver != null) {
//      webView.getContext().unregisterReceiver(this.receiver);
//      this.receiver = null;
//    }
  }
}
