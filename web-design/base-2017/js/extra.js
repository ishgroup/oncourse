// (function($) {
// 	$(document).ready(function() {

// 		$('.home-slider > ul').bxSlider({
// 			mode: 'horizontal',
// 			captions: true,
// 			auto: true,
// 			pause: 5000
// 		});

// 		$('.courseTestimonials > ul > li').each(function(indx, item) {
// 			var ratingBlock = $(item).find('.testimonial-rating');
// 			var rating = ratingBlock.attr('rating');
// 			if(rating) {
// 				ratingBlock.find('>span').removeClass('checked');
// 				for(var i=0;i<rating;i++) {
// 					ratingBlock.find('>span:eq('+ i +')').addClass('checked');
// 				}
// 			}
// 		});

// 		$('.courseTestimonials > ul').bxSlider({
// 			mode: 'fade',
// 			captions: true,
// 			auto: true,
// 			pause: 5000
// 		});
// 	})
// })(jQuery);
