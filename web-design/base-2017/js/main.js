
var $j = jQuery.noConflict();

(function($) {
  $.app = {
    getDimention: function(oWidth, oHeight, nWidth) {
      return {
        height: (oHeight / oWidth) * nWidth,
        width: nWidth
      };
    },
    getImageDimension: function(el, onReady) {
      var src = typeof el.attr === 'function' ? el.attr('src') : el.src !== undefined ? el.src : el;

      var image = new Image();
      image.onload = function(){
        if(typeof(onReady) == 'function') {
          var oWidth = image.width, oHeight = image.height;
          //var nWidth = $(window).width(), nHeight = (oHeight / oWidth) * nWidth;
          var nDim = $.app.getDimention(oWidth, oHeight, $(window).width());

          onReady({
            src: src,
            width: image.width,
            height: image.height,
            nWidth: nDim.width,
            nHeight: nDim.height
          });
        }
      };
      image.src = src;
    }
  }
})(jQuery);