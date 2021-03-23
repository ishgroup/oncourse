import "./masonry.pkgd";
import jQuery, * as $ from "./jquery-1.11.2";

var $j = jQuery.noConflict();

$j(document).ready(function () {
    if ($j('#dashboard').length) {
        $j('#wrap').addClass('dashboard');
        new Masonry(document.querySelector('div#dashboard'), {
            itemSelector: 'div.dashboard-card',
            columnWidth: 10
        });
    }
});
