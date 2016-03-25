var $j = jQuery.noConflict();

function initMasks() {
    $j("#cardnumber").inputmask("9999 9999 9999 9999");
}

$j(document).ready(function() {
    initMasks();
});
