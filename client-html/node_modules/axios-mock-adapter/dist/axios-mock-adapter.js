(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory(require("axios"));
	else if(typeof define === 'function' && define.amd)
		define(["axios"], factory);
	else if(typeof exports === 'object')
		exports["AxiosMockAdapter"] = factory(require("axios"));
	else
		root["AxiosMockAdapter"] = factory(root["axios"]);
})(window, function(__WEBPACK_EXTERNAL_MODULE_axios__) {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
/******/ 		}
/******/ 	};
/******/
/******/ 	// define __esModule on exports
/******/ 	__webpack_require__.r = function(exports) {
/******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 		}
/******/ 		Object.defineProperty(exports, '__esModule', { value: true });
/******/ 	};
/******/
/******/ 	// create a fake namespace object
/******/ 	// mode & 1: value is a module id, require it
/******/ 	// mode & 2: merge all properties of value into the ns
/******/ 	// mode & 4: return value when already ns object
/******/ 	// mode & 8|1: behave like require
/******/ 	__webpack_require__.t = function(value, mode) {
/******/ 		if(mode & 1) value = __webpack_require__(value);
/******/ 		if(mode & 8) return value;
/******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
/******/ 		var ns = Object.create(null);
/******/ 		__webpack_require__.r(ns);
/******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
/******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
/******/ 		return ns;
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = "./src/index.js");
/******/ })
/************************************************************************/
/******/ ({

/***/ "./node_modules/deep-equal/index.js":
/*!******************************************!*\
  !*** ./node_modules/deep-equal/index.js ***!
  \******************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

eval("var pSlice = Array.prototype.slice;\nvar objectKeys = __webpack_require__(/*! ./lib/keys.js */ \"./node_modules/deep-equal/lib/keys.js\");\nvar isArguments = __webpack_require__(/*! ./lib/is_arguments.js */ \"./node_modules/deep-equal/lib/is_arguments.js\");\n\nvar deepEqual = module.exports = function (actual, expected, opts) {\n  if (!opts) opts = {};\n  // 7.1. All identical values are equivalent, as determined by ===.\n  if (actual === expected) {\n    return true;\n\n  } else if (actual instanceof Date && expected instanceof Date) {\n    return actual.getTime() === expected.getTime();\n\n  // 7.3. Other pairs that do not both pass typeof value == 'object',\n  // equivalence is determined by ==.\n  } else if (!actual || !expected || typeof actual != 'object' && typeof expected != 'object') {\n    return opts.strict ? actual === expected : actual == expected;\n\n  // 7.4. For all other Object pairs, including Array objects, equivalence is\n  // determined by having the same number of owned properties (as verified\n  // with Object.prototype.hasOwnProperty.call), the same set of keys\n  // (although not necessarily the same order), equivalent values for every\n  // corresponding key, and an identical 'prototype' property. Note: this\n  // accounts for both named and indexed properties on Arrays.\n  } else {\n    return objEquiv(actual, expected, opts);\n  }\n}\n\nfunction isUndefinedOrNull(value) {\n  return value === null || value === undefined;\n}\n\nfunction isBuffer (x) {\n  if (!x || typeof x !== 'object' || typeof x.length !== 'number') return false;\n  if (typeof x.copy !== 'function' || typeof x.slice !== 'function') {\n    return false;\n  }\n  if (x.length > 0 && typeof x[0] !== 'number') return false;\n  return true;\n}\n\nfunction objEquiv(a, b, opts) {\n  var i, key;\n  if (isUndefinedOrNull(a) || isUndefinedOrNull(b))\n    return false;\n  // an identical 'prototype' property.\n  if (a.prototype !== b.prototype) return false;\n  //~~~I've managed to break Object.keys through screwy arguments passing.\n  //   Converting to array solves the problem.\n  if (isArguments(a)) {\n    if (!isArguments(b)) {\n      return false;\n    }\n    a = pSlice.call(a);\n    b = pSlice.call(b);\n    return deepEqual(a, b, opts);\n  }\n  if (isBuffer(a)) {\n    if (!isBuffer(b)) {\n      return false;\n    }\n    if (a.length !== b.length) return false;\n    for (i = 0; i < a.length; i++) {\n      if (a[i] !== b[i]) return false;\n    }\n    return true;\n  }\n  try {\n    var ka = objectKeys(a),\n        kb = objectKeys(b);\n  } catch (e) {//happens when one is a string literal and the other isn't\n    return false;\n  }\n  // having the same number of owned properties (keys incorporates\n  // hasOwnProperty)\n  if (ka.length != kb.length)\n    return false;\n  //the same set of keys (although not necessarily the same order),\n  ka.sort();\n  kb.sort();\n  //~~~cheap key test\n  for (i = ka.length - 1; i >= 0; i--) {\n    if (ka[i] != kb[i])\n      return false;\n  }\n  //equivalent values for every corresponding key, and\n  //~~~possibly expensive deep test\n  for (i = ka.length - 1; i >= 0; i--) {\n    key = ka[i];\n    if (!deepEqual(a[key], b[key], opts)) return false;\n  }\n  return typeof a === typeof b;\n}\n\n\n//# sourceURL=webpack://AxiosMockAdapter/./node_modules/deep-equal/index.js?");

/***/ }),

/***/ "./node_modules/deep-equal/lib/is_arguments.js":
/*!*****************************************************!*\
  !*** ./node_modules/deep-equal/lib/is_arguments.js ***!
  \*****************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

eval("var supportsArgumentsClass = (function(){\n  return Object.prototype.toString.call(arguments)\n})() == '[object Arguments]';\n\nexports = module.exports = supportsArgumentsClass ? supported : unsupported;\n\nexports.supported = supported;\nfunction supported(object) {\n  return Object.prototype.toString.call(object) == '[object Arguments]';\n};\n\nexports.unsupported = unsupported;\nfunction unsupported(object){\n  return object &&\n    typeof object == 'object' &&\n    typeof object.length == 'number' &&\n    Object.prototype.hasOwnProperty.call(object, 'callee') &&\n    !Object.prototype.propertyIsEnumerable.call(object, 'callee') ||\n    false;\n};\n\n\n//# sourceURL=webpack://AxiosMockAdapter/./node_modules/deep-equal/lib/is_arguments.js?");

/***/ }),

/***/ "./node_modules/deep-equal/lib/keys.js":
/*!*********************************************!*\
  !*** ./node_modules/deep-equal/lib/keys.js ***!
  \*********************************************/
/*! no static exports found */
/***/ (function(module, exports) {

eval("exports = module.exports = typeof Object.keys === 'function'\n  ? Object.keys : shim;\n\nexports.shim = shim;\nfunction shim (obj) {\n  var keys = [];\n  for (var key in obj) keys.push(key);\n  return keys;\n}\n\n\n//# sourceURL=webpack://AxiosMockAdapter/./node_modules/deep-equal/lib/keys.js?");

/***/ }),

/***/ "./src/handle_request.js":
/*!*******************************!*\
  !*** ./src/handle_request.js ***!
  \*******************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\n\nvar utils = __webpack_require__(/*! ./utils */ \"./src/utils.js\");\n\nfunction makeResponse(result, config) {\n  return {\n    status: result[0],\n    data: utils.isSimpleObject(result[1]) ? JSON.parse(JSON.stringify(result[1])) : result[1],\n    headers: result[2],\n    config: config\n  };\n}\n\nfunction handleRequest(mockAdapter, resolve, reject, config) {\n  var url = config.url;\n  if (config.baseURL && config.url.substr(0, config.baseURL.length) === config.baseURL) {\n    url = config.url.slice(config.baseURL ? config.baseURL.length : 0);\n  }\n\n  delete config.adapter;\n  mockAdapter.history[config.method].push(config);\n\n  var handler = utils.findHandler(\n    mockAdapter.handlers,\n    config.method,\n    url,\n    config.data,\n    config.params,\n    config.headers,\n    config.baseURL\n  );\n\n  if (handler) {\n    if (handler.length === 7) {\n      utils.purgeIfReplyOnce(mockAdapter, handler);\n    }\n\n    if (handler.length === 2) {\n      // passThrough handler\n      mockAdapter.originalAdapter(config).then(resolve, reject);\n    } else if (typeof handler[3] !== 'function') {\n      utils.settle(\n        resolve,\n        reject,\n        makeResponse(handler.slice(3), config),\n        mockAdapter.delayResponse\n      );\n    } else {\n      var result = handler[3](config);\n      // TODO throw a sane exception when return value is incorrect\n      if (typeof result.then !== 'function') {\n        utils.settle(resolve, reject, makeResponse(result, config), mockAdapter.delayResponse);\n      } else {\n        result.then(\n          function(result) {\n            if (result.config && result.status) {\n              utils.settle(resolve, reject, makeResponse([result.status, result.data, result.headers], result.config), 0);\n            } else {\n              utils.settle(resolve, reject, makeResponse(result, config), mockAdapter.delayResponse);\n            }\n          },\n          function(error) {\n            if (mockAdapter.delayResponse > 0) {\n              setTimeout(function() {\n                reject(error);\n              }, mockAdapter.delayResponse);\n            } else {\n              reject(error);\n            }\n          }\n        );\n      }\n    }\n  } else {\n    // handler not found\n    utils.settle(\n      resolve,\n      reject,\n      {\n        status: 404,\n        config: config\n      },\n      mockAdapter.delayResponse\n    );\n  }\n}\n\nmodule.exports = handleRequest;\n\n\n//# sourceURL=webpack://AxiosMockAdapter/./src/handle_request.js?");

/***/ }),

/***/ "./src/index.js":
/*!**********************!*\
  !*** ./src/index.js ***!
  \**********************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\n\nvar deepEqual = __webpack_require__(/*! deep-equal */ \"./node_modules/deep-equal/index.js\");\n\nvar handleRequest = __webpack_require__(/*! ./handle_request */ \"./src/handle_request.js\");\n\nvar VERBS = ['get', 'post', 'head', 'delete', 'patch', 'put', 'options', 'list'];\n\nfunction adapter() {\n  return function(config) {\n    var mockAdapter = this;\n    // axios >= 0.13.0 only passes the config and expects a promise to be\n    // returned. axios < 0.13.0 passes (config, resolve, reject).\n    if (arguments.length === 3) {\n      handleRequest(mockAdapter, arguments[0], arguments[1], arguments[2]);\n    } else {\n      return new Promise(function(resolve, reject) {\n        handleRequest(mockAdapter, resolve, reject, config);\n      });\n    }\n  }.bind(this);\n}\n\nfunction getVerbObject() {\n  return VERBS.reduce(function(accumulator, verb) {\n    accumulator[verb] = [];\n    return accumulator;\n  }, {});\n}\n\nfunction reset() {\n  resetHandlers.call(this);\n  resetHistory.call(this);\n}\n\nfunction resetHandlers() {\n  this.handlers = getVerbObject();\n}\n\nfunction resetHistory() {\n  this.history = getVerbObject();\n}\n\nfunction MockAdapter(axiosInstance, options) {\n  reset.call(this);\n\n  if (axiosInstance) {\n    this.axiosInstance = axiosInstance;\n    this.originalAdapter = axiosInstance.defaults.adapter;\n    this.delayResponse = options && options.delayResponse > 0 ? options.delayResponse : null;\n    axiosInstance.defaults.adapter = this.adapter.call(this);\n  }\n}\n\nMockAdapter.prototype.adapter = adapter;\n\nMockAdapter.prototype.restore = function restore() {\n  if (this.axiosInstance) {\n    this.axiosInstance.defaults.adapter = this.originalAdapter;\n  }\n};\n\nMockAdapter.prototype.reset = reset;\nMockAdapter.prototype.resetHandlers = resetHandlers;\nMockAdapter.prototype.resetHistory = resetHistory;\n\nVERBS.concat('any').forEach(function(method) {\n  var methodName = 'on' + method.charAt(0).toUpperCase() + method.slice(1);\n  MockAdapter.prototype[methodName] = function(matcher, body, requestHeaders) {\n    var _this = this;\n    var matcher = matcher === undefined ? /.*/ : matcher;\n\n    function reply(code, response, headers) {\n      var handler = [matcher, body, requestHeaders, code, response, headers];\n      addHandler(method, _this.handlers, handler);\n      return _this;\n    }\n\n    function replyOnce(code, response, headers) {\n      var handler = [matcher, body, requestHeaders, code, response, headers, true];\n      addHandler(method, _this.handlers, handler);\n      return _this;\n    }\n\n    return {\n      reply: reply,\n\n      replyOnce: replyOnce,\n\n      passThrough: function passThrough() {\n        var handler = [matcher, body];\n        addHandler(method, _this.handlers, handler);\n        return _this;\n      },\n\n      networkError: function() {\n        reply(function(config) {\n          var error = new Error('Network Error');\n          error.config = config;\n          return Promise.reject(error);\n        });\n      },\n\n      networkErrorOnce: function() {\n        replyOnce(function(config) {\n          var error = new Error('Network Error');\n          error.config = config;\n          return Promise.reject(error);\n        });\n      },\n\n      timeout: function() {\n        reply(function(config) {\n          var error = new Error('timeout of ' + config.timeout + 'ms exceeded');\n          error.config = config;\n          error.code = 'ECONNABORTED';\n          return Promise.reject(error);\n        });\n      },\n\n      timeoutOnce: function() {\n        replyOnce(function(config) {\n          var error = new Error('timeout of ' + config.timeout + 'ms exceeded');\n          error.config = config;\n          error.code = 'ECONNABORTED';\n          return Promise.reject(error);\n        });\n      }\n    };\n  };\n});\n\nfunction findInHandlers(method, handlers, handler) {\n  var index = -1;\n  for (var i = 0; i < handlers[method].length; i += 1) {\n    var item = handlers[method][i];\n    var isReplyOnce = item.length === 7;\n    var comparePaths = item[0] instanceof RegExp && handler[0] instanceof RegExp\n      ? String(item[0]) === String(handler[0])\n      : item[0] === handler[0];\n    var isSame = (\n      comparePaths &&\n      deepEqual(item[1], handler[1], { strict: true }) &&\n      deepEqual(item[2], handler[2], { strict: true })\n    );\n    if (isSame && !isReplyOnce) {\n      index =  i;\n    }\n  }\n  return index;\n}\n\nfunction addHandler(method, handlers, handler) {\n  if (method === 'any') {\n    VERBS.forEach(function(verb) {\n      handlers[verb].push(handler);\n    });\n  } else {\n    var indexOfExistingHandler = findInHandlers(method, handlers, handler);\n    if (indexOfExistingHandler > -1 && handler.length < 7) {\n      handlers[method].splice(indexOfExistingHandler, 1, handler);\n    } else {\n      handlers[method].push(handler);\n    }\n  }\n}\n\nmodule.exports = MockAdapter;\nmodule.exports.default = MockAdapter;\n\n\n//# sourceURL=webpack://AxiosMockAdapter/./src/index.js?");

/***/ }),

/***/ "./src/utils.js":
/*!**********************!*\
  !*** ./src/utils.js ***!
  \**********************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\n\nvar axios = __webpack_require__(/*! axios */ \"axios\");\nvar deepEqual = __webpack_require__(/*! deep-equal */ \"./node_modules/deep-equal/index.js\");\n\nfunction isEqual(a, b) {\n  return deepEqual(a, b, { strict: true });\n}\n\n// < 0.13.0 will not have default headers set on a custom instance\nvar rejectWithError = !!axios.create().defaults.headers;\n\nfunction find(array, predicate) {\n  var length = array.length;\n  for (var i = 0; i < length; i++) {\n    var value = array[i];\n    if (predicate(value)) return value;\n  }\n}\n\nfunction combineUrls(baseURL, url) {\n  if (baseURL) {\n    return baseURL.replace(/\\/+$/, '') + '/' + url.replace(/^\\/+/, '');\n  }\n\n  return url;\n}\n\nfunction findHandler(handlers, method, url, body, parameters, headers, baseURL) {\n  return find(handlers[method.toLowerCase()], function(handler) {\n    if (typeof handler[0] === 'string') {\n      return (isUrlMatching(url, handler[0]) || isUrlMatching(combineUrls(baseURL, url), handler[0])) && isBodyOrParametersMatching(method, body, parameters, handler[1])  && isRequestHeadersMatching(headers, handler[2]);\n    } else if (handler[0] instanceof RegExp) {\n      return (handler[0].test(url) || handler[0].test(combineUrls(baseURL, url))) && isBodyOrParametersMatching(method, body, parameters, handler[1]) && isRequestHeadersMatching(headers, handler[2]);\n    }\n  });\n}\n\nfunction isUrlMatching(url, required) {\n  var noSlashUrl = url[0] === '/' ? url.substr(1) : url;\n  var noSlashRequired = required[0] === '/' ? required.substr(1) : required;\n  return (noSlashUrl === noSlashRequired);\n}\n\nfunction isRequestHeadersMatching(requestHeaders, required) {\n  if (required === undefined) return true;\n  return isEqual(requestHeaders, required);\n}\n\nfunction isBodyOrParametersMatching(method, body, parameters, required) {\n  var allowedParamsMethods = ['delete', 'get', 'head', 'options'];\n  if (allowedParamsMethods.indexOf(method.toLowerCase()) >= 0 ) {\n    var params = required ? required.params : undefined;\n    return isParametersMatching(parameters, params);\n  } else {\n    return isBodyMatching(body, required);\n  }\n}\n\nfunction isParametersMatching(parameters, required) {\n  if (required === undefined) return true;\n\n  return isEqual(parameters, required);\n}\n\nfunction isBodyMatching(body, requiredBody) {\n  if (requiredBody === undefined) {\n    return true;\n  }\n  var parsedBody;\n  try {\n    parsedBody = JSON.parse(body);\n  } catch (e) { }\n  return parsedBody ? isEqual(parsedBody, requiredBody) : isEqual(body, requiredBody);\n}\n\nfunction purgeIfReplyOnce(mock, handler) {\n  Object.keys(mock.handlers).forEach(function(key) {\n    var index = mock.handlers[key].indexOf(handler);\n    if (index > -1) {\n      mock.handlers[key].splice(index, 1);\n    }\n  });\n}\n\nfunction settle(resolve, reject, response, delay) {\n  if (delay > 0) {\n    setTimeout(function() {\n      settle(resolve, reject, response);\n    }, delay);\n    return;\n  }\n\n  if (response.config && response.config.validateStatus) {\n    response.config.validateStatus(response.status)\n      ? resolve(response)\n      : reject(createErrorResponse(\n        'Request failed with status code ' + response.status,\n        response.config,\n        response\n      ));\n    return;\n  }\n\n  // Support for axios < 0.11\n  if (response.status >= 200 && response.status < 300) {\n    resolve(response);\n  } else {\n    reject(response);\n  }\n}\n\nfunction createErrorResponse(message, config, response) {\n  // Support for axios < 0.13.0\n  if (!rejectWithError) return response;\n\n  var error = new Error(message);\n  error.config = config;\n  error.response = response;\n  return error;\n}\n\nfunction isSimpleObject(value) {\n  return value !== null && value !== undefined && value.toString() === '[object Object]';\n}\n\nmodule.exports = {\n  find: find,\n  findHandler: findHandler,\n  isSimpleObject: isSimpleObject,\n  purgeIfReplyOnce: purgeIfReplyOnce,\n  settle: settle\n};\n\n\n//# sourceURL=webpack://AxiosMockAdapter/./src/utils.js?");

/***/ }),

/***/ "axios":
/*!************************!*\
  !*** external "axios" ***!
  \************************/
/*! no static exports found */
/***/ (function(module, exports) {

eval("module.exports = __WEBPACK_EXTERNAL_MODULE_axios__;\n\n//# sourceURL=webpack://AxiosMockAdapter/external_%22axios%22?");

/***/ })

/******/ });
});