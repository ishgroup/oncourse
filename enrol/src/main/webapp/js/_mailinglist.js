var $j = jQuery.noConflict();

$j(document).ready(function() {
    initHints($j("[id*=mailingListForm]").attr('id'));
});
