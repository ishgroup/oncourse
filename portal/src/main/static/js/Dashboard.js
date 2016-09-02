goog.provide('Dashboard');

goog.require('jquery');
goog.require('masonry.pkgd');

var $j = jQuery.noConflict();

$j(document).ready(function () {

    if ($j('#dashboard').length) {
        $j('#wrap').addClass('dashboard')
        $j('div#dashboard').masonry({
            itemSelector: 'div.dashboard-card',
            columnWidth: 10
        });
    }
});
