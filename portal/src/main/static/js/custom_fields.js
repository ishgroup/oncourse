goog.provide('custom_fields');

goog.require('initialise');

var $j = jQuery.noConflict();

$j(document).ready(function () {
    $j("input[name^='customField']").each(function () {
        if ($j(this).data('default')) {

            var dataType = $j(this).data('type');
            var dataValue = $j(this).data('default');
            var values;

            if([7,8].includes(dataType)) {
                values = dataValue.map(function(x) {
                    return x.value.includes("*")
                      ? {value: "Other", label: "Other"}
                      : {value: x.value, label: dataType === 8 ? x.label + (" (" + x.value + ")" ) : "" }
                });


            } else {
                values = dataValue.split(';');
                values = values.map(function(x){ return "*" == x ? "Other" : x });
            }

            if (values.length > 0) {
                $j(this).autocomplete({
                    source: values,
                    minLength: 0,
                    focus: function( event, ui ) {
                        $j(this).val( ui.item.label );
                        return false;
                    },
                    select: function( event, ui ) {
                        $j(this).val( ui.item.label );
                        $j(this).attr( "submit-value", ui.item.value );
                        return false;
                    }
                }).focus(function() {
                    $j(this).autocomplete('search', "")
                })
                if(!$j(this).val()) {
                    $j(this).val(values[0].label ? values[0].label : values[0])
                } else if (dataType === 8) {
                    var savedValue = $j(this).val();
                    var updated = values.find(function (v) {
                        return v.value === savedValue;
                    })

                    if(updated) {
                        $j(this).attr( "submit-value", updated.value );
                        $j(this).val(updated.label);
                    }
                }
            }
        }
    });
});

