import jQuery, * as $ from "./jquery-1.11.2";
import "./initialise";

var $j = jQuery.noConflict();

$j(document).ready(function () {
    $j("input[name^='customField']").each(function () {
        if ($j(this).data('default')) {
            var dataType = $j(this).data('type');
            var dataValue = $j(this).data('default');
            var values;
            var creatable;
            var hiddenInput = $j('<input type="hidden"></input>')

            if (dataType === 7) {
               dataValue.forEach(function(x) {
                    if(x.value.includes("*")) {
                        creatable = true;
                    }
                });
                values = dataValue.filter(function(x) {
                    return !x.value.includes("*")
                });
            }

            if (dataType === 8) {
                hiddenInput.attr("data-key", $j(this).attr("data-key"));
                hiddenInput.attr("name", $j(this).attr("name"));
                $j(this).attr("data-key",null)
                $j(this).attr("name",null)
                $j(this).before(hiddenInput);
                values = dataValue.filter(function(x) {
                    return !x.value.includes("*")
                });
                values = dataValue.map(function(x) {
                    return {value: x.value, label: x.label ? x.label + (" (" + x.value + ")" ) : "" }
                });
            }

            if(![7,8].includes(dataType)) {
                values = dataValue.split(';');
                creatable = dataValue.includes("*");
                values = values.filter(function(x){ return x !== "*" });
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
                        if(dataType === 8) {
                            $j(this).val( ui.item.label );
                            hiddenInput.attr( "value", ui.item.value );
                        }
                        return false;
                    },
                    create: function() {
                        $j(this).on("keydown", function(e) {
                            if(!creatable) {
                                e.preventDefault()
                            }
                        })
                    }
                }).focus(function() {
                    $j(this).autocomplete('search', "")
                })
                if(!$j(this).val()) {
                    $j(this).val(values[0].label ? values[0].label : values[0].value)
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

