# BluetoothButtonPlugin
Bluetooth Button Plugin

# 作用
识别蓝牙耳机的按键操作（播放、暂停、下一首和上一首），升降音量按钮不需要捕获，系统会直接处理。语音键未能捕获。

# 用法示例
```
    cordova.plugins.BluetoothButtonPlugin.start(msg => {
      alert('语音按键点击：' + msg);
      console.log(msg);
      // if (msg) {
      //   cordova.plugins.BaiduAsrPlugin.begin(1);
      // }
    });
```
