import jQuery, * as $ from "./jquery-1.11.2";
import "./jquery.slimscroll.min";
import "./initialise";

var $j = jQuery.noConflict();
var target;

$j(document).ready(function () {


    if (window.location.href.indexOf("/applications") > -1 && window.location.hash != "") {

        target = window.location.hash;
        window.location.hash = "";
    }

    $j("a[href^='#reject']").click(function()
    {

        var id = $j(this).closest('span').attr('data');
        var blockId = '#application-' + id;
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

        var id = $j(this).closest('span').attr('data');
        var blockId = '#application-' + id;
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



    $j("a[href^='/portal/history/applications#application-']").click(function(e) {
        if (!$j("a[href^='#applications-history']").closest('li').hasClass('active')) {
            location.reload();
        } else {
            e.preventDefault();
            scrollToTarget(this.hash);
        }
    });

    $j('.header-top .nav-links.logged .change-user ul.dropdown-menu').slimscroll({
        height: 250
    });

    $j(document).on('click', '.link-user-switch', function(e) {
        e.preventDefault();
        $j('.logged .change-user').slideToggle();
        $j('.logged .change-user').toggleClass('user-open');
    });

    if($j('.btn-group.change-user').length > 0) {
        $j('.view-all-users').removeClass('hide');
        $j('.user-action-links').addClass('has-more-users');
    }
});

$j(window).load(function() {
    scrollToTarget(target)
});

function scrollToTarget(hashTag) {
    if (hashTag) {
        $j('html, body').animate({
            scrollTop: $j(hashTag).offset().top - $j('#header-holder').height()
        }, 700, 'swing', function () {});
    }
}
