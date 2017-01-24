/*
  jQuery utils - 0.8.0  [modified by Thomas to remove the broken body.youTubeLinksToEmbed()]

  http://code.google.com/p/jquery-utils/

  (c) Maxime Haineault <haineault@gmail.com>
  http://haineault.com

  MIT License (http://www.opensource.org/licenses/mit-license.php

*/

(function($) {

     $.extend($.expr[':'], {
        // case insensitive version of :contains
        icontains: function(a,i,m){return (a.textContent||a.innerText||$j(a).text()||"").toLowerCase().indexOf(m[3].toLowerCase())>=0;}
    });

    $.iterators = {
        getText:  function() { return $(this).text(); },
        parseInt: function(v){ return parseInt(v, 10); }
    };

	$.extend({

        // Taken from ui.core.js.
        // Why are you keeping this gem for yourself guys ? :|
        keyCode: {
            BACKSPACE: 8, CAPS_LOCK: 20, COMMA: 188, CONTROL: 17, DELETE: 46, DOWN: 40,
            END: 35, ENTER: 13, ESCAPE: 27, HOME: 36, INSERT:  45, LEFT: 37,
            NUMPAD_ADD: 107, NUMPAD_DECIMAL: 110, NUMPAD_DIVIDE: 111, NUMPAD_ENTER: 108,
            NUMPAD_MULTIPLY: 106, NUMPAD_SUBTRACT: 109, PAGE_DOWN: 34, PAGE_UP: 33,
            PERIOD: 190, RIGHT: 39, SHIFT: 16, SPACE: 32, TAB: 9, UP: 38
        },

        keyIs: function(k, e) {
            return parseInt($.keyCode[k.toUpperCase()], 10) == parseInt((typeof(e) == 'number' )? e: e.keyCode, 10);
        },

        // Redirect to a specified url
        redirect: function(url) {
            window.location.href = url;
            return url;
        },

        // Stop event shorthand
        stop: function(e, preventDefault, stopPropagation) {
            if (preventDefault)  { e.preventDefault(); }
            if (stopPropagation) { e.stopPropagation(); }
            return preventDefault && false || true;
        },

        // Returns the basename of a path
        basename: function(path) {
            var t = path.split('/');
            return t[t.length] === '' && s || t.slice(0, t.length).join('/');
        },

        // Returns the filename of a path
        filename: function(path) {
            return path.split('/').pop();
        },

        // Returns a formated file size
        filesizeformat: function(bytes, suffixes){
            var b = parseInt(bytes, 10);
            var s = suffixes || ['byte', 'bytes', 'KB', 'MB', 'GB'];
            if (isNaN(b) || b === 0) { return '0 ' + s[0]; }
            if (b == 1)              { return '1 ' + s[0]; }
            if (b < 1024)            { return  b.toFixed(2) + ' ' + s[1]; }
            if (b < 1048576)         { return (b / 1024).toFixed(2) + ' ' + s[2]; }
            if (b < 1073741824)      { return (b / 1048576).toFixed(2) + ' '+ s[3]; }
            else                     { return (b / 1073741824).toFixed(2) + ' '+ s[4]; }
        },

        fileExtension: function(s) {
            var tokens = s.split('.');
            return tokens[tokens.length-1] || false;
        },

        // Returns true if an object is a String
        isString: function(o) {
            return typeof(o) == 'string' && true || false;
        },

        // Returns true if an object is a RegExp
		isRegExp: function(o) {
			return o && o.constructor.toString().indexOf('RegExp()') != -1 || false;
		},

        // Returns true if an object is an array
        // Mark Miller - http://blog.360.yahoo.com/blog-TBPekxc1dLNy5DOloPfzVvFIVOWMB0li?p=916
		isArray: function(o) {
            if (!o) { return false; }
            return Object.prototype.toString.apply(o.constructor.prototype) === '[object Array]';
		},

        isObject: function(o) {
            return (typeof(o) == 'object');
        },

        // Convert input to currency (two decimal fixed number)
		toCurrency: function(i) {
			i = parseFloat(i, 10).toFixed(2);
			return (i=='NaN') ? '0.00' : i;
		},

        /*--------------------------------------------------------------------
         * javascript method: "pxToEm"
         * by:
           Scott Jehl (scott@filamentgroup.com)
           Maggie Wachs (maggie@filamentgroup.com)
           http://www.filamentgroup.com
         *
         * Copyright (c) 2008 Filament Group
         * Dual licensed under the MIT (filamentgroup.com/examples/mit-license.txt) and GPL (filamentgroup.com/examples/gpl-license.txt) licenses.
         *
         * Description: pxToEm converts a pixel value to ems depending on inherited font size.
         * Article: http://www.filamentgroup.com/lab/retaining_scalable_interfaces_with_pixel_to_em_conversion/
         * Demo: http://www.filamentgroup.com/examples/pxToEm/
         *
         * Options:
                scope: string or jQuery selector for font-size scoping
                reverse: Boolean, true reverses the conversion to em-px
         * Dependencies: jQuery library
         * Usage Example: myPixelValue.pxToEm(); or myPixelValue.pxToEm({'scope':'#navigation', reverse: true});
         *
         * Version: 2.1, 18.12.2008
         * Changelog:
         *		08.02.2007 initial Version 1.0
         *		08.01.2008 - fixed font-size calculation for IE
         *		18.12.2008 - removed native object prototyping to stay in jQuery's spirit, jsLinted (Maxime Haineault <haineault@gmail.com>)
        --------------------------------------------------------------------*/

        pxToEm: function(i, settings){
            //set defaults
            settings = jQuery.extend({
                scope: 'body',
                reverse: false
            }, settings);

            var pxVal = (i === '') ? 0 : parseFloat(i);
            var scopeVal;
            var getWindowWidth = function(){
                var de = document.documentElement;
                return self.innerWidth || (de && de.clientWidth) || document.body.clientWidth;
            };

            /* When a percentage-based font-size is set on the body, IE returns that percent of the window width as the font-size.
                For example, if the body font-size is 62.5% and the window width is 1000px, IE will return 625px as the font-size.
                When this happens, we calculate the correct body font-size (%) and multiply it by 16 (the standard browser font size)
                to get an accurate em value. */

            if (settings.scope == 'body' && $.browser.msie && (parseFloat($('body').css('font-size')) / getWindowWidth()).toFixed(1) > 0.0) {
                var calcFontSize = function(){
                    return (parseFloat($('body').css('font-size'))/getWindowWidth()).toFixed(3) * 16;
                };
                scopeVal = calcFontSize();
            }
            else { scopeVal = parseFloat($(settings.scope).css("font-size")); }

            var result = (settings.reverse === true) ? (pxVal * scopeVal).toFixed(2) + 'px' : (pxVal / scopeVal).toFixed(2) + 'em';
            return result;
        }
	});

	$.extend($.fn, {
        // Select a text range in a textarea
        selectRange: function(start, end){
            // use only the first one since only one input can be focused
            if ($(this).get(0).createTextRange) {
                var range = $(this).get(0).createTextRange();
                range.collapse(true);
                range.moveEnd('character',   end);
                range.moveStart('character', start);
                range.select();
            }
            else if ($(this).get(0).setSelectionRange) {
                $(this).bind('focus', function(e){
                    e.preventDefault();
                }).get(0).setSelectionRange(start, end);
            }
            return $(this);
        },

        /*--------------------------------------------------------------------
         * JQuery Plugin: "EqualHeights"
         * by:	Scott Jehl, Todd Parker, Maggie Costello Wachs (http://www.filamentgroup.com)
         *
         * Copyright (c) 2008 Filament Group
         * Licensed under GPL (http://www.opensource.org/licenses/gpl-license.php)
         *
         * Description: Compares the heights or widths of the top-level children of a provided element
                and sets their min-height to the tallest height (or width to widest width). Sets in em units
                by default if pxToEm() method is available.
         * Dependencies: jQuery library, pxToEm method	(article:
                http://www.filamentgroup.com/lab/retaining_scalable_interfaces_with_pixel_to_em_conversion/)
         * Usage Example: $(element).equalHeights();
                Optional: to set min-height in px, pass a true argument: $(element).equalHeights(true);
         * Version: 2.1, 18.12.2008
         *
         * Note: Changed pxToEm call to call $.pxToEm instead, jsLinted (Maxime Haineault <haineault@gmail.com>)
        --------------------------------------------------------------------*/

        equalHeights: function(px){
            $(this).each(function(){
                var currentTallest = 0;
                $(this).children().each(function(i){
                    if ($(this).height() > currentTallest) { currentTallest = $(this).height(); }
                });
                if (!px || !$.pxToEm) { currentTallest = $.pxToEm(currentTallest); } //use ems unless px is specified
                // for ie6, set height since min-height isn't supported
                if ($.browser.msie && $.browser.version == 6.0) { $(this).children().css({'height': currentTallest}); }
                $(this).children().css({'min-height': currentTallest});
            });
            return this;
        },

        // Copyright (c) 2009 James Padolsey
        // http://james.padolsey.com/javascript/jquery-delay-plugin/
        delay: function(time, callback){
            jQuery.fx.step.delay = function(){};
            return this.animate({delay:1}, time, callback);
        }
	});
})(jQuery);
/*
  jQuery strings - 0.2
  http://code.google.com/p/jquery-utils/

  (c) Maxime Haineault <haineault@gmail.com>
  http://haineault.com

  MIT License (http://www.opensource.org/licenses/mit-license.php)

  Implementation of Python3K advanced string formatting
  http://www.python.org/dev/peps/pep-3101/

  Documentation: http://code.google.com/p/jquery-utils/wiki/StringFormat

*/
(function($){
    var strings = {
        strConversion: {
            // tries to translate any objects type into string gracefully
            __repr: function(i){
                switch(this.__getType(i)) {
                    case 'array':case 'date':case 'number':
                        return i.toString();
                    case 'object':
                        var o = [];
                        for (x=0; x<i.length; i++) { o.push(i+': '+ this.__repr(i[x])); }
                        return o.join(', ');
                    case 'string':
                        return i;
                    default:
                        return i;
                }
            },
            // like typeof but less vague
            __getType: function(i) {
                if (!i || !i.constructor) { return typeof(i); }
                var match = i.constructor.toString().match(/Array|Number|String|Object|Date/);
                return match && match[0].toLowerCase() || typeof(i);
            },
            //+ Jonas Raoni Soares Silva

            __pad: function(str, l, s, t){
                var p = s || ' ';
                var o = str;
                if (l - str.length > 0) {
                    o = new Array(Math.ceil(l / p.length)).join(p).substr(0, t = !t ? l : t == 1 ? 0 : Math.ceil(l / 2)) + str + p.substr(0, l - t);
                }
                return o;
            },
            __getInput: function(arg, args) {
                 var key = arg.getKey();
                switch(this.__getType(args)){
                    case 'object': // Thanks to Jonathan Works for the patch
                        var keys = key.split('.');
                        var obj = args;
                        for(var subkey = 0; subkey < keys.length; subkey++){
                            obj = obj[keys[subkey]];
                        }
                        if (typeof(obj) != 'undefined') {
                            if (strings.strConversion.__getType(obj) == 'array') {
                                return arg.getFormat().match(/\.\*/) && obj[1] || obj;
                            }
                            return obj;
                        }
                        else {
                            // TODO: try by numerical index
                        }
                    break;
                    case 'array':
                        key = parseInt(key, 10);
                        if (arg.getFormat().match(/\.\*/) && typeof args[key+1] != 'undefined') { return args[key+1]; }
                        else if (typeof args[key] != 'undefined') { return args[key]; }
                        else { return key; }
                    break;
                }
                return '{'+key+'}';
            },
            __formatToken: function(token, args) {
                var arg   = new Argument(token, args);
                return strings.strConversion[arg.getFormat().slice(-1)](this.__getInput(arg, args), arg);
            },

            // Signed integer decimal.
            d: function(input, arg){
                var o = parseInt(input, 10); // enforce base 10
                var p = arg.getPaddingLength();
                if (p) { return this.__pad(o.toString(), p, arg.getPaddingString(), 0); }
                else   { return o; }
            },
            // Signed integer decimal.
            i: function(input, args){
                return this.d(input, args);
            },
            // Unsigned octal
            o: function(input, arg){
                var o = input.toString(8);
                if (arg.isAlternate()) { o = this.__pad(o, o.length+1, '0', 0); }
                return this.__pad(o, arg.getPaddingLength(), arg.getPaddingString(), 0);
            },
            // Unsigned decimal
            u: function(input, args) {
                return Math.abs(this.d(input, args));
            },
            // Unsigned hexadecimal (lowercase)
            x: function(input, arg){
                var o = parseInt(input, 10).toString(16);
                o = this.__pad(o, arg.getPaddingLength(), arg.getPaddingString(),0);
                return arg.isAlternate() ? '0x'+o : o;
            },
            // Unsigned hexadecimal (uppercase)
            X: function(input, arg){
                return this.x(input, arg).toUpperCase();
            },
            // Floating point exponential format (lowercase)
            e: function(input, arg){
                return parseFloat(input, 10).toExponential(arg.getPrecision());
            },
            // Floating point exponential format (uppercase)
            E: function(input, arg){
                return this.e(input, arg).toUpperCase();
            },
            // Floating point decimal format
            f: function(input, arg){
                return this.__pad(parseFloat(input, 10).toFixed(arg.getPrecision()), arg.getPaddingLength(), arg.getPaddingString(),0);
            },
            // Floating point decimal format (alias)
            F: function(input, args){
                return this.f(input, args);
            },
            // Floating point format. Uses exponential format if exponent is greater than -4 or less than precision, decimal format otherwise
            g: function(input, arg){
                var o = parseFloat(input, 10);
                return (o.toString().length > 6) ? Math.round(o.toExponential(arg.getPrecision())): o;
            },
            // Floating point format. Uses exponential format if exponent is greater than -4 or less than precision, decimal format otherwise
            G: function(input, args){
                return this.g(input, args);
            },
            // Single character (accepts integer or single character string).
            c: function(input, args) {
                var match = input.match(/\w|\d/);
                return match && match[0] || '';
            },
            // String (converts any JavaScript object to anotated format)
            r: function(input, args) {
                return this.__repr(input);
            },
            // String (converts any JavaScript object using object.toString())
            s: function(input, args) {
                return input.toString && input.toString() || ''+input;
            }
        },

        format: function(str, args) {
            var end    = 0;
            var start  = 0;
            var match  = false;
            var buffer = [];
            var token  = '';
            var tmp    = (str||'').split('');
            for(start=0; start < tmp.length; start++) {
                if (tmp[start] == '{' && tmp[start+1] !='{') {
                    end   = str.indexOf('}', start);
                    token = tmp.slice(start+1, end).join('');
                    buffer.push(strings.strConversion.__formatToken(token, (typeof arguments[1] != 'object')? arguments2Array(arguments, 2): args || []));
                }
                else if (start > end || buffer.length < 1) { buffer.push(tmp[start]); }
            }
            return (buffer.length > 1)? buffer.join(''): buffer[0];
        },

        calc: function(str, args) {
            return eval(format(str, args));
        },

        repeat: function(s, n) {
            return new Array(n+1).join(s);
        },

        UTF8encode: function(s) {
            return unescape(encodeURIComponent(s));
        },

        UTF8decode: function(s) {
            return decodeURIComponent(escape(s));
        },

        tpl: function() {
            var out = '', render = true;
            // Set
            // $.tpl('ui.test', ['<span>', helloWorld ,'</span>']);
            if (arguments.length == 2 && $.isArray(arguments[1])) {
                this[arguments[0]] = arguments[1].join('');
                return jQuery;
            }
            // $.tpl('ui.test', '<span>hello world</span>');
            if (arguments.length == 2 && $.isString(arguments[1])) {
                this[arguments[0]] = arguments[1];
                return jQuery;
            }
            // Call
            // $.tpl('ui.test');
            if (arguments.length == 1) {
                return $(this[arguments[0]]);
            }
            // $.tpl('ui.test', false);
            if (arguments.length == 2 && arguments[1] == false) {
                return this[arguments[0]];
            }
            // $.tpl('ui.test', {value:blah});
            if (arguments.length == 2 && $.isObject(arguments[1])) {
                return $($.format(this[arguments[0]], arguments[1]));
            }
            // $.tpl('ui.test', {value:blah}, false);
            if (arguments.length == 3 && $.isObject(arguments[1])) {
                return (arguments[2] == true)
                    ? $.format(this[arguments[0]], arguments[1])
                    : $($.format(this[arguments[0]], arguments[1]));
            }
        }
};

    var Argument = function(arg, args) {
        this.__arg  = arg;
        this.__args = args;
        this.__max_precision = parseFloat('1.'+ (new Array(32)).join('1'), 10).toString().length-3;
        this.__def_precision = 6;
        this.getString = function(){
            return this.__arg;
        };
        this.getKey = function(){
            return this.__arg.split(':')[0];
        };
        this.getFormat = function(){
            var match = this.getString().split(':');
            return (match && match[1])? match[1]: 's';
        };
        this.getPrecision = function(){
            var match = this.getFormat().match(/\.(\d+|\*)/g);
            if (!match) { return this.__def_precision; }
            else {
                match = match[0].slice(1);
                if (match != '*') { return parseInt(match, 10); }
                else if(strings.strConversion.__getType(this.__args) == 'array') {
                    return this.__args[1] && this.__args[0] || this.__def_precision;
                }
                else if(strings.strConversion.__getType(this.__args) == 'object') {
                    return this.__args[this.getKey()] && this.__args[this.getKey()][0] || this.__def_precision;
                }
                else { return this.__def_precision; }
            }
        };
        this.getPaddingLength = function(){
            var match = false;
            if (this.isAlternate()) {
                match = this.getString().match(/0?#0?(\d+)/);
                if (match && match[1]) { return parseInt(match[1], 10); }
            }
            match = this.getString().match(/(0|\.)(\d+|\*)/g);
            return match && parseInt(match[0].slice(1), 10) || 0;
        };
        this.getPaddingString = function(){
            var o = '';
            if (this.isAlternate()) { o = ' '; }
            // 0 take precedence on alternate format
            if (this.getFormat().match(/#0|0#|^0|\.\d+/)) { o = '0'; }
            return o;
        };
        this.getFlags = function(){
            var match = this.getString().matc(/^(0|\#|\-|\+|\s)+/);
            return match && match[0].split('') || [];
        };
        this.isAlternate = function() {
            return !!this.getFormat().match(/^0?#/);
        };
    };

    var arguments2Array = function(args, shift) {
        var o = [];
        for (l=args.length, x=(shift || 0)-1; x<l;x++) { o.push(args[x]); }
        return o;
    };

    $.extend(strings);
})(jQuery);

/*
  jQuery array utils - 0.1
  http://code.google.com/p/jquery-utils/

  (c) Maxime Haineault <haineault@gmail.com>
  http://haineault.com

  MIT License (http://www.opensource.org/licenses/mit-license.php

*/

(function($){

    var dummy      = function(i) { return i; };
    var truthiness = function(i) { return !!i; };

    $.extend({

        all: function(object, iterator){
            var output   = true;
            var iterator = iterator || truthiness;
            $.each(object, function(idx, i){
                if (!iterator(i)) { output = false; }
            });
            return output;
        },

        any: function(object, iterator){
            var output   = false;
            var iterator = iterator || truthiness;
            $.each(object, function(idx, i){
                return iterator(i) && !(output = true) || true;
            });
            return output;
        },

        // equivalent of array.find
        detect: function(object, iterator){
            var output   = false;
            var iterator = iterator || truthiness;
            $.each(object, function(idx, i){
                //return iterator(i) && !(output = i) || true;
                if (iterator(i)) {
                    output = i;
                    return false;
                }
            });
            return output;
        },

        eachSlice: function(object, size, iterator){
            var index = - size, slices = [];
            while ((index += size) < object.length){
                slices.push($.map(object.slice(index, index + size), iterator || dummy));
            }
            return slices;
        },

        inject: function(object, acc, iterator){
            $.each(object, function (idx, val) {
                acc = iterator(acc, val, idx);
            });
            return acc;
        },

        invoke: function(object, method, args){
            $.each(object, function(){
                if ($.isFunction(method)){
                    method.apply(object, args);
                }
                else if ($.isFunction(window[method])){
                    window[method].apply(object, args);
                }
            });
            return object;
        },

        max: function(object, iterator) {
            var output = false;
            $.each(object, function (idx, val) {
                var rs = (iterator || dummy)(val, idx);
                if (!output || rs > output) { output = rs; }
            });
            return output;
        },

        min: function(object, iterator) {
            var output = false;
            $.each(object, function (idx, val) {
                var rs = (iterator || dummy)(val, idx);
                if (!output || rs < output) { output = rs; }
            });
            return output;
        },

        partition: function(object, iterator) {
            var trues = [], falses = [];
            $.each(object, function (idx, val) {
                ((iterator || truthiness)(val, idx) ? trues : falses).push(val);
            });
            return [trues, falses];
        },

        pluck: function(object, property, iterator) {
            var output = [];
            var iterator = iterator || dummy;
            $.each(object, function(){
                // "||this" is required to make IE behave like FF on simple arrays ..
                output.push(iterator(this[property]||this));
            });
            return output;
        },
/*
        reject: function(object, iterator){
            return $.select(object, (iterator || function(i){ return !i; }));
        },

        // findAll equivalent
        select: function(object, iterator){
            var output   = [];
            var iterator = iterator || truthiness;
            $.each(object, function(idx, i){
                if (iterator(i)) {
                    output.push(i);
                }
            });
            return output;
        },
*/

        sum: function(object, iterator){
            var iterator = iterator || function(i) { return parseInt(i, 10) };
            var t = 0;
            $.each(object, function(){
                var v = iterator(this);
                if (!isNaN(v)) { t = t + v; }
            });
            return t;
        },

        zip: function(object, object2, iterator) {
            var output = [];
            var iterator = iterator || dummy;
            $.each(object, function(idx, i){
                if (object2[idx]) { output.push([i, object2[idx]]); }
            });
            return output;
        },

        // Randomize an array object with the Fisher-Yates algorythm
        // Author: Ashley Pond V. (http://sedition.com/perl/javascript-fy.html)
        randomize: function(object) {
            var i = object.length;
            if (i == 0) return false;
            while (--i) {
               var j = Math.floor( Math.random() * ( i + 1 ) );
               var tempi = object[i];
               var tempj = object[j];
               object[i] = tempj;
               object[j] = tempi;
             }
            return object;
        },

        // Returns a range object
        // Author: Matthias Miller
        // Site:   http://blog.outofhanwell.com/2006/03/29/javascript-range-function/
        range:  function() {
            if (!arguments.length) { return []; }
            var min, max, step;
            if (arguments.length == 1) {
                min  = 0;
                max  = arguments[0]-1;
                step = 1;
            }
            else {
                // default step to 1 if it's zero or undefined
                min  = arguments[0];
                max  = arguments[1]-1;
                step = arguments[2] || 1;
            }
            // convert negative steps to positive and reverse min/max
            if (step < 0 && min >= max) {
                step *= -1;
                var tmp = min;
                min = max;
                max = tmp;
                min += ((max-min) % step);
            }
            var a = [];
            for (var i = min; i <= max; i += step) { a.push(i); }
            return a;
        }
    });

    $.extend($.fn, {
        all:       function(iterator) { return $.all(this, iterator); },
        any:       function(iterator) { return $.any(this, iterator); },
        pluck:     function(property, iterator) { return $.pluck(this, property, iterator); },
        detect:    function(iterator) { return $($.detect(this, iterator)); },
        eachSlice: function(size, iterator) { return $.eachSlice(this, size, iterator); },
        //select:    function(iterator) { return $.findAll(this, iterator); },
        sum: function(iterator) {
            var iterator = iterator || function(i) {
                return parseFloat($(i).val() || $(i).text(), 10);
            };
            return $.sum(this, iterator);
        }
    });
})(jQuery);

/*
 jQuery delayed observer - 0.5
 http://code.google.com/p/jquery-utils/

 (c) Maxime Haineault <haineault@gmail.com>
 http://haineault.com

 MIT License (http://www.opensource.org/licenses/mit-license.php)

 Changelog
 =========
 0.2 using closure, special thanks to Stephen Goguen & Tane Piper.
 0.3 now allow object chaining, added license
 0.4 code cleanup, added support for other events than keyup, fixed variable scope
 0.5 changed filename, included in jquery-utils
 0.6 complete rewrite, same structure but more compact,
     now using jquery's "data" method instead of a stack to store data
     it's now possible to change the condition, by default it's "if new this.val == this.oldval"
     now using this.each to support multiple observed elements
*/

(function($){
    $.extend($.fn, {
        delayedObserver: function(callback, delay, options){
            this.each(function(){
                var $obj    = $(this);
                var options = options || {};
                $obj.data('oldval',    $obj.val())
                    .data('delay',     delay || 0.5)
                    .data('condition', options.condition || function() {
                        return ($(this).data('oldval') == $(this).val());
                    })
                    .data('callback',  callback)
                    [(options.event||'keyup')](function(){
                        if ($obj.data('condition').apply($obj)) return;
                        else {
                            if ($obj.data('timer')) clearTimeout($obj.data('timer'));

                            $obj.data('timer', setTimeout(function(){
                                $obj.data('callback').apply($obj);
                            }, $obj.data('delay') * 1000));

                            $obj.data('oldval', $obj.val());
                        }
                    });
                });
        }
    });
})(jQuery);