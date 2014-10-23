goog.provide('html5fix');

goog.require('jquery');


var $j = jQuery.noConflict();

$j(document).ready(function() {
    jQuery(function() {
        jQuery.support.placeholder = false;
        webkit_type = document.createElement('input');
        if('placeholder' in webkit_type) jQuery.support.placeholder = true;
    });
    
    $j(function() {
    
        if(!jQuery.support.placeholder) {
    
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
                $j(this).find('.hasPlaceholder').each(function() { $j(this).val(''); });
            });
        }
    });
});