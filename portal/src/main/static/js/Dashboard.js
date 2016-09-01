goog.provide('Dashboard');

goog.require('jquery');
goog.require('masonry.pkgd');

var $j = jQuery.noConflict();

$j(document).ready(function () {

    if ($j('#dashboard').length) {
        $j('.main-container').removeClass('col-lg-9 col-sm-8');
        $j('.main-container').addClass('col-lg-12 col-sm-12');
        $j('#wrap').css('background-color', '#F5F0DF')
        $j('div#dashboard').masonry({
            itemSelector: 'div.dashboard-card',
            columnWidth: 10
        });
    }
});
