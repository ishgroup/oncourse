/**
*  shortlist.js
*
*  Events and AJAX responses used to create/add/delete to the shortlist.
*
*/

function slideShowShortlist() {
	if($('.shortListOrder').size() > 0) {
		$('.shortListOrder').slideUp();

		$(document).on('click', '.shortlistActionShow', function() {
			$('.shortListOrder').fadeIn('fast');
			$(this).hide();
			$('.shortlistActionHide').show().addClass('active');
		});

		$(document).on('click', '.shortlistActionHide', function() {
			$('.shortListOrder').fadeOut('fast');
			$(this).removeClass('active').hide();
			$('.shortlistActionShow').show();
		});
	}

}

function refreshShortList() {
	/**
	* Refreshes the shortList component and if the courseClassToRefresh!=null,
	* refreshes the shortlistControl for this class.
	*/
	$.ajax({
		type: 'GET',
		url:  '/refreshShortList',
		success: function(msg) {
			$('#shortlist').replaceWith($.trim(msg));
			slideShowShortlist();
		}
	});
}

(function($) {
	$(document).ready(function() {
		slideShowShortlist();

		$(document).on('click', '.closebutton', function() {
			$(this).parents('.dialogContainer').fadeOut('fast');
		});

		// Add items to the shortlist return a response to the user
		$(document).on('click touchend', '.enrolAction:not(.disabled)', function() {
			//grab the classid of the enrol button that's just been clicked.
			var listid = $(this).parents('.classItem').data('classid');
			var buttonPos = $('.classItem[data-classid=' + listid + ']').position();
			var link = this.href;

			// does it already exist in the shortlist? if not, make that ajax call.
			if(($('.shortlistChoices li[data-classid=' + listid + ']') == null)
				| ($('.shortlistChoices li[data-classid=' + listid + ']').length == 0)) {

				$.ajax({
					type: 'GET',
					url:  link,
					success: function(msg) {
						//Make the order confirmation box appear
						$('.confirmOrderDialog .confirm-txt').text('Thanks for adding: ');
						$('.confirmOrderDialog .className').show().text($('.classItem[data-classid=' + listid + '] .summary').text());
						$('.confirmOrderDialog .classDate').show().text($('.classItem[data-classid=' + listid + '] .class-item-info-l > .date a:first').text());
						$('.classItem[data-classid=' + listid + '] .enrolAction').addClass('enrol-added-class').text('Added');

						$('.confirmOrderDialog').css({
							top: buttonPos.top,
							right: '150px'
						});

						$('.confirmOrderDialog').stop(true, false).fadeIn('fast');

						$(document).on('click', '.confirm-close-wrapper .closeButton', function() {
							$('.confirmOrderDialog').fadeOut('fast');
							return false;
						});

						$('#shortlist').replaceWith($.trim(msg));
						slideShowShortlist();

						// Send event to Google Analytics for this add to cart event
						//TODO: listid should be replaced by Course.code-class.code
						if(dataLayer) {
							dataLayer.push({'event': 'ShoppingCart', 'eventAction': 'Add', 'eventValue': listid.toString()});
						}
					}
				});

		} else {
				// Else, let them know that it's already on their shortlist and get them to go to checkout
				$('.confirmOrderDialog .confirm-txt').text('You\'ve already added this class to your shortlist. Do you want to proceed to checkout?');
				$('.confirmOrderDialog .className').empty();
				$('.confirmOrderDialog .classDate').empty()

				$('.confirmOrderDialog').css({
					top: buttonPos.top,
					right: '150px'
				});

				$('.confirmOrderDialog').stop(true, false).fadeIn('fast');
				$(document).on('click', '.confirm-close-wrapper .closeButton', function() {
					$('.confirmOrderDialog').fadeOut('fast');
					return false;
				});
			}
			return false;
		});

		/* Remove an item from the shortlist*/
		$(document).on('click', '#shortlist .deleteItem a', function() {
			var fThis = $(this);
			fThis.parent().addClass('loading');
			var link = this.href;
			var id = $(this).parents('li').attr('data-classid');

			$.ajax({
				type: 'GET',
				url:  link,
				success: function(msg) {
					$('.classItem[data-classid=' + id + '] .enrolAction').removeClass('enrol-added-class').text('Enrol Now');
					$('#shortlist').replaceWith(msg);
					slideShowShortlist();
				}
			});
			return false;
		});

		// Drop our shortlisted items in the shortlist box
		$(document).on('click', 'li.onshortlist-x a.cutitem', function() {
			var link = this.href;
			var itemId = this.id.match(/(\d+)/)[1];

			$.ajax({
				type: 'GET',
				url:  link,
				success: function() {
					window.parent.location.reload(true);
				}
			});
			return false;
		});

		$(document).on('click', '.course_modules > span', function() {
		  $(this).toggleClass('active');
		  $(this).next('ul').slideToggle(500);
	  });
	});
})(jQuery);