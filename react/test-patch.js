let jsdom = require('jsdom'),
    XMLHttpRequest = require('xmlhttprequest').XMLHttpRequest,
    expect = require('chai').expect,
    $ = require('jquery');

global.expect = expect;

global.document = jsdom.jsdom();
global.window = document.defaultView;
global.navigator = window.navigator;

global.$ = $(window);
global.$.support.cors = true;
global.$.ajaxSettings.xhr = function() {
    return new XMLHttpRequest();
};