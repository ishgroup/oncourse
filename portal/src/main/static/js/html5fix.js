//goog.provide('html5fix');

//goog.require('jquery');


var $j = jQuery.noConflict();


/**
 * The method check that the browser supports placeholders for input tags (html5).
 * If not (IE 9 and below) then restore placeholders.
 *
 */
function placeholderFix() {

    jQuery.support.placeholder = false;
    webkit_type = document.createElement('input');
    if ('placeholder' in webkit_type) jQuery.support.placeholder = true;

    
    // part of the code is moved to an anonymous function - to properly refrexhing of placeholders
    // this code is borrowed from NIDA's web site
    $j(function () {

        if (!jQuery.support.placeholder) {

            var active = document.activeElement;

            $j(':text, textarea, :password').focus(function () {

                if (($j(this).attr('placeholder')) && ($j(this).attr('placeholder').length > 0) && ($j(this).attr('placeholder') != '') && $j(this).val() == $j(this).attr('placeholder')) {
                    $j(this).val('').removeClass('hasPlaceholder');
                }
            }).blur(function () {
                if (($j(this).attr('placeholder')) && ($j(this).attr('placeholder').length > 0) && ($j(this).attr('placeholder') != '') && ($j(this).val() == '' || $j(this).val() == $j(this).attr('placeholder'))) {
                    $j(this).val($j(this).attr('placeholder')).addClass('hasPlaceholder');
                }
            });

            $j(':text, textarea, :password').blur();
            $j(active).focus();
            $j('form').submit(function () {
                $j(this).find('.hasPlaceholder').each(function () {
                    $j(this).val('');
                });
            });
        }
    });
}

$j(document).ready(function() {
    placeholderFix();
});