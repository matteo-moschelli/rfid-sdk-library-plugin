var exec = require('cordova/exec');

exports.init = function (arg0, success, error) {
    exec(success, error, 'RfidSdkLibrary', 'init', [arg0]);
};

exports.connect = function (arg0, success, error) {
    exec(success, error, 'RfidSdkLibrary', 'connect', [arg0]);
};