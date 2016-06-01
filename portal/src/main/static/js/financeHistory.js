/**
 * Javascript for history/finance component
 */

goog.provide('financeHistory');

goog.require('jquery');

var $j = jQuery.noConflict();

function dateEffects() {
    $j('#input-price-for-course').val($j('span#price-for-course').text());
    var paymentDate = new Date($j('#date-of-course').text());
    if (paymentDate < Date.now()) {
        $j('#amount-due').addClass('warning-msg').append(' was overdue');
    }
    if ($j('.balanse').first().text() > '$0,01') {
        $j('#show-if-balance').show();
    }
}

$j(document).ready(function () {
    dateEffects();
});
