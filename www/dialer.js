var exec = require("cordova/exec");


function DialerPlugin() {}

// **Initialize speech kit**
//
// `credentialClassName`  The class name to be loaded to retrieve the app id and key  
// `serverName`  The hostname of the server to connect to  
// `port`  The port number for connection  
// `sslEnabled`  True if SSL is enabled for the connection  
// `successCallback`  The callback function for success  
// `failureCallback`  The callback function for error  
DialerPlugin.prototype.dialNumber =function(phone_number, successCallback, errorCallback) {

  exec(
      successCallback, // success callback function
      errorCallback, // error callback function
      'DialerPlugin', // mapped to our native Java class called "Calendar"
      'dialNumber', // with this action name
      [{                  // and this array of custom arguments to create our entry
        "phone_number": phone_number
      }]
    );
  }

});

 var DialerPlugin = new DialerPlugin();
        module.exports = DialerPlugin;