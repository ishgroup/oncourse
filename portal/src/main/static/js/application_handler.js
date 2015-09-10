goog.provide('application_handler');

var $j = jQuery.noConflict();


$j(document).ready(function () {

    $j("a[href^='#reject']").click(function()
    {

        var id = $j(this).closest('span').attr('id');
        var blockId = '#' + id;
        var actionLink = "/portal/history.applications:reject/" + id;
        $j.ajax({
            type: "GET",
            url: actionLink,
            async: false,
            cache: false,
            success: function(data) {
                if (data.content)
                {
                    $j(blockId).html(data.content);
                }
            }
        });
    });

    $j("a[href^='#enrolnow']").click(function()
    {

        var id = $j(this).closest('span').attr('id');
        var blockId = '#' + id;
        var actionLink = "/portal/history.applications:enrolnow/" + id;
        $j.ajax({
            type: "GET",
            url: actionLink,
            async: false,
            cache: false,
            success: function(data) {
                if (data.redirectUrl) {
                    window.open(data.redirectUrl, '_blank')
                } else {
                    $j(blockId).html(data.content);
                }
            }
        });
    });
});