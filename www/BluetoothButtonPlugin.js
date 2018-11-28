var exec = require('cordova/exec');

exports.start = function(success, fail) {
  return cordova.exec(success, fail, 'BluetoothButtonPlugin', 'start', [{}]);
};
exports.stop = function(success, fail) {
  return cordova.exec(success, fail, 'BluetoothButtonPlugin', 'stop', [{}]);
};