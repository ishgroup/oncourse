$j(document).ready(function() {
  jQuery('#input-price-for-course').val(jQuery('span#price-for-course').text());
  var paymentDate = new Date(jQuery('#date-of-course').text());
  if ( paymentDate < Date.now() ){
    jQuery('#amount-due').css('color','red').append(' was overdue');
  };
  if ( jQuery('.balanse').first().text() > '$0,01' ){
    jQuery('#show-if-balance').css('display', 'block');
  };
});
