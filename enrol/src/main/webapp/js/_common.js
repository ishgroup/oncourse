var $j = jQuery.noConflict();

function initContactEditorHandle()
{
    $j('.dateOfBirth').datepicker({
        changeMonth: true,
        changeYear: true,
        maxDate: '0'
    });


    if ($j(".suburb")) {
        $j(".suburb").autocomplete({source: '/ui/internal/autocomplete.sub', minLength: 3,
            select: function(event, ui) {
                var value = ui.item.value;
                var suburb = suburbFromString(value);
                var postcode = postcodeFromString(value); // otherwise it thinks it's a number
                var state = postcode.length==0 ? '' : stateFromPostcode(postcode);
                $j(this).attr("value",suburb);
                $j(".postcode").attr("value",postcode);
                $j(".state").attr("value",state);

            },
            close: function(event, ui) {
                var value = $j(this).attr("value");
                var suburb = suburbFromString(value);
                $j(this).attr("value",suburb);
            }
        });
    }
}

function initAvetmissEditorHandle()
{
    $j("[id*=countryOfBirth]").autocomplete({source: '/ui/internal/autocomplete.country', minLength: 2});
}

$j(document).ready(function() {
    initContactEditorHandle();
    initAvetmissEditorHandle();
});
