var dialNumber =function(phone_number, successCallback, errorCallback) {

  cordova.exec(
      successCallback, // success callback function
      errorCallback, // error callback function
      'DialerPlugin', // mapped to our native Java class called "Calendar"
      'dialNumber', // with this action name
      [{                  // and this array of custom arguments to create our entry
        "phone_number": phone_number
      }]
    );
  }
        module.exports = dialNumber;