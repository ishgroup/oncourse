
var $j = jQuery.noConflict();

(function($) {
  jQuery.event.handle = jQuery.event.dispatch;
})(jQuery);