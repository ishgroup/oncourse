(function($) {
  $(document).ready(function() {
    $('#cert-verification-modal').on('shown.bs.modal', function () {

    });
    $(document).on('touchend click', '.cert-units > .row > .col-sm-2', function() {
        $(this).parent().toggleClass('units-active');
        $(this).find('.cert-icon').toggleClass('icon-arrow-top');
    });
  });
})(jQuery);