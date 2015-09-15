goog.provide('common');

goog.require('jquery.blockUI');

var $j = jQuery.noConflict();

function initContactEditorHandle()
{
    if ($j(".suburb")) {
        $j(".suburb").autocomplete({source: '/ish/internal/autocomplete.sub', minLength: 3,
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

/**
 * Add default values popup for each custom field which displayed on 'edit contact' page.
 * List of values provided through semicolon and located into 'data-default' attribute of input tag.
 * Popup appears when focus hit on field.
 */
function initCustomFieldHandle() {
    $j(".customField").each(function() {
        if ($j(this).data('default')) {
            var values = $j(this).data('default').split(';');
            if (values.length > 0) {
                $j(this).autocomplete({
                    source: values,
                    minLength: 0,
                    messages: {
                        noResults: '',
                        results: function() {}
                    }
                }).focus(function() {
                    $j(this).autocomplete('search', "")
                });
                if(!$j(this).val()) {
                    $j(this).val(values[0])
                }
            }
        }
    })
}

function initCountryAutoCompleteHandle()
{
    $j("[id*=countryOfBirth]").autocomplete({source: '/ish/internal/autocomplete.country', minLength: 2});
	$j("[id*=country]").autocomplete({source: '/ish/internal/autocomplete.country', minLength: 2});
}

function initHints()
{
    /**
     * unbind all functions which were binded to inputs by other scripts
     */
    $j('span.valid input').unbind("focus blur");
    $j('span.validate input').unbind("focus blur");

    $j(".valid").mouseenter(function() {
        $j(this).find('.hint').removeClass('hidden-text');
    });

    $j(".valid").mouseleave(function() {
        $j(this).find('.hint').addClass('hidden-text');
    });

    $j(".validate").mouseenter(function() {
        $j(this).find('.reason').removeClass('hidden-text');
    });

    $j(".validate").mouseleave(function() {
        $j(this).find('.reason').addClass('hidden-text');
    });
}


$j(document).ready(function() {
    //initHints should be going first becouse inside the function we unbind focus and blur events
    initHints();
    initContactEditorHandle();
	initCountryAutoCompleteHandle();
    initCustomFieldHandle();
});
