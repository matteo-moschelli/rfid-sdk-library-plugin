var exec = require('cordova/exec');

exports.init = function (arg0, success, error) {
    exec(success, error, 'RfidSdkLibrary', 'init', [arg0]);
};

exports.connect = function (arg0, success, error) {
    exec(success, error, 'RfidSdkLibrary', 'connect', [arg0]);
};

exports.disconnect = function (success, error) {
    exec(success, error, 'RfidSdkLibrary', 'disconnect');
}

exports.startRfidScan = function(success, error) {
    exec(success, error, 'RfidSdkLibrary', 'start_rfid');
};

exports.stopRfidScan = function(success, error) {
    exec(success, error, 'RfidSdkLibrary', 'stop_rfid');
};

exports.startBarcodeScan = function(success, error) {
    exec(success, error, 'RfidSdkLibrary', 'start_barcode');
}

exports.stopBarcodeScan = function(success, error) {
    exec(success, error, 'RfidSdkLibrary', 'stop_barcode');
}

exports.getVersion = function(success, error) {
    exec(success, error, 'RfidSdkLibrary', 'version');
}