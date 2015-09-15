goog.provide('custom_fields');

goog.require('initialise');

var $j = jQuery.noConflict();


$j(document).ready(function () {

    $j("input[id^='customField']").each(function () {
        if ($j(this).data('default')) {
            var values = $j(this).data('default').split(';');
            if (values.length > 0) {
                $j(this).autocomplete({
                    source: values,
                    minLength: 0
                }).focus(function() {
                    $j(this).autocomplete('search', "")
                });
                if(!$j(this).val()) {
                    $j(this).val(values[0])
                }
            }
        }
    });
});