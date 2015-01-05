goog.provide('supportcss');

/*!jQuery support.cssProperty
// Name:    jquery.support.cssproperty.js
// Version: Unversioned
// URL:     https://gist.github.com/jackfuchs/556448
// License: Licensed under MIT http://www.opensource.org/licenses/mit-license.php
-------------------------------------------------------------------------------------------*/
/**
 * jQuery.support.cssProperty
 * To verify that a CSS property is supported (or any of its browser-specific implementations)
 *
 * @param string p - css property name
 * [@param] bool rp - optional, if set to true, the css property name will be returned, instead of a boolean support indicator
 *
 * @Author: Axel Jack Fuchs (Cologne, Germany)
 * @Date: 08-29-2010 18:43
 *
 * Example: $.support.cssProperty('boxShadow');
 * Returns: true
 *
 * Example: $.support.cssProperty('boxShadow', true);
 * Returns: 'MozBoxShadow' (On Firefox4 beta4)
 * Returns: 'WebkitBoxShadow' (On Safari 5)
 */
jQuery.support.cssProperty = (function() {
    function cssProperty(p, rp) {
        var b = document.body || document.documentElement,
                s = b.style;

        // No css support detected
        if (typeof s == 'undefined') {
            return false;
        }

        // Tests for standard prop
        if (typeof s[p] == 'string') {
            return rp ? p : true;
        }

        // Tests for vendor specific prop
        v = ['Moz', 'Webkit', 'Khtml', 'O', 'Ms'],
                p = p.charAt(0).toUpperCase() + p.substr(1);
        for (var i = 0; i < v.length; i++) {
            if (typeof s[v[i] + p] == 'string') {
                return rp ? (v[i] + p) : true;
            }
        }
    }

    return cssProperty;
})();