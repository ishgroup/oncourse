/**
*  shortlist.js
*
*  Events and AJAX responses used to create/add/delete to the shortlist.
*
*/

function slideShowShortlist() {
	if($j('.shortListOrder').length > 0) {
		$j('.shortListOrder').slideUp();

		$j(document).on('click', '.shortlistActionShow', function() {
			$j('.shortListOrder').fadeIn('fast');
			$j(this).hide();
			$j('.shortlistActionHide').show().addClass('active');
		});

		$j(document).on('click', '.shortlistActionHide', function() {
			$j('.shortListOrder').fadeOut('fast');
			$j(this).removeClass('active').hide();
			$j('.shortlistActionShow').show();
		});
	}

}

function refreshShortList() {
	/**
	* Refreshes the shortList component and if the courseClassToRefresh!=null,
	* refreshes the shortlistControl for this class.
	*/
	$j.ajax({
		type: 'GET',
		url:  '/refreshShortList',
		success: function(msg) {
			$j('#shortlist').replaceWith($j.trim(msg));
			slideShowShortlist();
		}
	});
}

(function($j) {
	$j(document).ready(function() {
		slideShowShortlist();

		$j(document).on('click', '.closebutton', function() {
			$j(this).parents('.dialogContainer').fadeOut('fast');
		});

		// Add items to the shortlist return a response to the user
		$j(document).on('click touchend', '.enrolAction:not(.disabled)', function() {
			//grab the classid of the enrol button that's just been clicked.
			var listid = $j(this).parents('.classItem').data('classid');
			var buttonPos = $j('.classItem[data-classid=' + listid + ']').position();
			var link = this.href;

			// does it already exist in the shortlist? if not, make that ajax call.
			if(($j('.shortlistChoices li[data-classid=' + listid + ']') == null)
				| ($j('.shortlistChoices li[data-classid=' + listid + ']').length == 0)) {

				$j.ajax({
					type: 'GET',
					url:  link,
					success: function(msg) {
						//Make the order confirmation box appear
						$j('.confirmOrderDialog .confirm-txt').text('Thanks for adding: ');
						$j('.confirmOrderDialog .className').show().text($j('.classItem[data-classid=' + listid + '] .summary').text());
						$j('.confirmOrderDialog .classDate').show().text($j('.classItem[data-classid=' + listid + '] .class-item-info-l > .date a:first').text());
						$j('.classItem[data-classid=' + listid + '] .enrolAction').addClass('enrol-added-class').text('Added');

						$j('.confirmOrderDialog').css({
							top: buttonPos.top,
							right: '150px'
						});

						$j('.confirmOrderDialog').stop(true, false).fadeIn('fast');

						$j(document).on('click', '.confirm-close-wrapper .closeButton', function(e) {
							e.preventDefault();
							$j('.confirmOrderDialog').fadeOut('fast');
							return false;
						});

						$j('#shortlist').replaceWith($j.trim(msg));
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
				$j('.confirmOrderDialog .confirm-txt').text('You\'ve already added this class to your shortlist. Do you want to proceed to checkout?');
				$j('.confirmOrderDialog .className').empty();
				$j('.confirmOrderDialog .classDate').empty()

				$j('.confirmOrderDialog').css({
					top: buttonPos.top,
					right: '150px'
				});

				$j('.confirmOrderDialog').stop(true, false).fadeIn('fast');
				$j(document).on('click', '.confirm-close-wrapper .closeButton', function(e) {
					e.preventDefault();
					$j('.confirmOrderDialog').fadeOut('fast');
					return false;
				});
			}
			return false;
		});

		/* Remove an item from the shortlist*/
		$j(document).on('click', '#shortlist .deleteItem a', function() {
			var fThis = $j(this);
			fThis.parent().addClass('loading');
			var link = this.href;
			var id = $j(this).parents('li').attr('data-classid');

			$j.ajax({
				type: 'GET',
				url:  link,
				success: function(msg) {
					$j('.classItem[data-classid=' + id + '] .enrolAction').removeClass('enrol-added-class').text('Enrol Now');
					$j('#shortlist').replaceWith(msg);
					slideShowShortlist();
				}
			});
			return false;
		});

		// Drop our shortlisted items in the shortlist box
		$j(document).on('click', 'li.onshortlist-x a.cutitem', function() {
			var link = this.href;
			var itemId = this.id.match(/(\d+)/)[1];

			$j.ajax({
				type: 'GET',
				url:  link,
				success: function() {
					window.parent.location.reload(true);
				}
			});
			return false;
		});

		$j(document).on('click', '.course_modules > span', function() {
		  $j(this).toggleClass('active');
		  $j(this).next('ul').slideToggle(500);
	  });
	});
})(jQuery);