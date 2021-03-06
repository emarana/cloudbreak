'use strict';

exports.checkClientVersion = function(args, res, next) {
  /**
   * checks the client version
   * 
   *
   * version String 
   * returns VersionCheckResult
   **/
  var examples = {};
  examples['application/json'] = {
  "versionCheckOk" : true,
  "message" : "aeiou"
};
  if (Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  } else {
    res.end();
  }
}

exports.createRDSDatabaseUtil = function(args, res, next) {
  /**
   * create a database for the service in the RDS if the connection could be created
   * 
   *
   * body RDSBuildRequest  (optional)
   * target List  (optional)
   * returns RdsBuildResult
   **/
  var examples = {};
  examples['application/json'] = {
  "results" : {
    "key" : "aeiou"
  }
};
  if (Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  } else {
    res.end();
  }
}

exports.getStackMatrixUtil = function(args, res, next) {
  /**
   * returns default ambari details for distinct HDP and HDF
   * 
   *
   * returns StackMatrix
   **/
  var examples = {};
  examples['application/json'] = {
  "hdp" : {
    "key" : {
      "minAmbari" : "aeiou",
      "ambari" : {
        "repo" : {
          "key" : {
            "baseUrl" : "aeiou",
            "version" : "aeiou",
            "gpgKeyUrl" : "aeiou"
          }
        },
        "version" : "aeiou"
      },
      "repo" : {
        "stack" : {
          "key" : "aeiou"
        },
        "util" : {
          "key" : "aeiou"
        }
      },
      "version" : "aeiou"
    }
  },
  "hdf" : {
    "key" : ""
  }
};
  if (Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  } else {
    res.end();
  }
}

exports.testAmbariDatabaseUtil = function(args, res, next) {
  /**
   * tests a database connection parameters
   * 
   *
   * body AmbariDatabaseDetails  (optional)
   * returns AmbariDatabaseTestResult
   **/
  var examples = {};
  examples['application/json'] = {
  "error" : "aeiou"
};
  if (Object.keys(examples).length > 0) {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(examples[Object.keys(examples)[0]] || {}, null, 2));
  } else {
    res.end();
  }
}

