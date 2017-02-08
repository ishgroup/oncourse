/**
*  shortlist.js
*
*/

(function($j) {
	$j(document).ready(function() {
		$j(document).on('click', '.course_modules > span', function() {
		  $j(this).toggleClass('active');
		  $j(this).next('ul').slideToggle(500);
	  });
	});
})(jQuery);