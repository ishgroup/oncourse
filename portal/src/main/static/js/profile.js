goog.provide('profile');

goog.require('initialise');



var $j = jQuery.noConflict();


function initAutocomplete(){if ($j("#suburb").length) {

        $j("#suburb").autocomplete({source: '/portal/profile:sub',
	    minLength: 3,
            select: function(event, ui) {
                var value = ui.item.value;
                var suburb = suburbFromString(value);
                var postcode = postcodeFromString(value); // otherwise it thinks it's a number
                var state = postcode.length==0 ? '' : stateFromPostcode(postcode);
                $j(this).attr("value",suburb);
                $j("#postcode").attr("value",postcode);
                $j("#state").attr("value",state);

            },
            close: function(event, ui) {
                var value = $j(this).attr("value");
                var suburb = suburbFromString(value);
                $j(this).attr("value",suburb);
            }
        });
       }

}


$j(document).ready(function() {
	initAutocomplete();

    $j('.nav-tabs.header-tabs .toggle-tabs').on('click', function() {
        $j(this).parent().find('li').toggleClass('show');
    });

    $j('.nav-tabs.header-tabs li').on('click', function() {
        $j(this).parent().find('li').removeClass('show');
    });
});
