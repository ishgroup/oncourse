/**
*  application.js
*
*	 License: copyright ish group
*  Purpose:
*   Base JS functionality that apply across all onCourse apps.
*
*   - Tell a friend submit link
*   - Advanced autocomplete search
*   - Add discount form
*   - Cookies: get, set, and delete
*   - Modal: Tutor, session and discount
*   - Toggle google location map for classes
*   - Fetch more classes
*
*/

// Makes select tag visible by adding visibility: visible in style attribute
function showSelectBoxes() {
	var selects = document.getElementsByTagName("select");
	for (i = 0; i != selects.length; i++) {
		selects[i].style.visibility = "visible";
	}
}

// Hides select tag by adding visibility: hidden in style attribute
function hideSelectBoxes() {
	var selects = document.getElementsByTagName("select");
	for (i = 0; i != selects.length; i++) {
		selects[i].style.visibility = "hidden";
	}
}

// Makes object and embed tag visible by adding visibility: visible in style attribute
function showFlash() {
	var flashObjects = document.getElementsByTagName("object");
	for (i = 0; i < flashObjects.length; i++) {
		flashObjects[i].style.visibility = "visible";
	}

	var flashEmbeds = document.getElementsByTagName("embed");
	for (i = 0; i < flashEmbeds.length; i++) {
		flashEmbeds[i].style.visibility = "visible";
	}
}

// Hides object and embed tag by adding visibility: hidden in style attribute
function hideFlash(){
	var flashObjects = document.getElementsByTagName("object");
	for (i = 0; i < flashObjects.length; i++) {
		flashObjects[i].style.visibility = "hidden";
	}

	var flashEmbeds = document.getElementsByTagName("embed");
	for (i = 0; i < flashEmbeds.length; i++) {
		flashEmbeds[i].style.visibility = "hidden";
	}

}

//Submits tell a friend form via ajax
function submitTellAFriend() {
	// process form here
	var code = $j("input#code").val();
	var name = $j("input#sendername").val();
	var email = $j("input#senderemail").val();
	var friendname = $j("input#friendname").val();
	var friendemail = $j("input#friendemail").val();
	var message = $j("#message").val();
	var dataString = 'sendername='+ name + '&senderemail=' + email
		+ '&code=' + code + '&friendname='+ friendname
		+ '&friendemail=' + friendemail + '&source=ajax';
	if (message) {
		dataString += '&message=' + message;
	}

	$j.ajax({
		type: "POST",
		url: "/tellAFriend",
		data: dataString,
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			//alert("Error: " + textStatus);
		},
		success: function(msg) {
			// alert("Success: " + msg);
			$j('#popup-content').html(msg);
		}
	});
}

// Toggles add student button/section
function initAddStudentButton() {
	//	Show the "add a student" section
	$j(document.body).on('click', '#addstudent', function() {
		$j('#addstudent-block').slideToggle(400);
	});
}

// Toggles student concession button/section
function initAddConcessionButtons() {
	$j(document.body).on('click', '.add-concession', function() {
		$j('#'+this.id+'-block').slideToggle(400);
	});
}

// Inits autocomplete in advanced search for suburb field
function initSuburbAutoComplete() {
	// if ($j(".suburb-autocomplete")) {
	// 	$j(".suburb-autocomplete").autocomplete({
	// 		source: '/ish/internal/autocomplete.sub',
	// 		minLength: 3,
	// 		select: function(event, ui) {
	// 			setPostcodeAndStateFromSuburb(this.form, ui.item.value);
	// 		}
	// 	});

	// 	$j(".suburb-autocomplete").autocomplete({
	// 		close: function(event, ui) {
	// 			setSuburbAfterAutoComplete(this.form);
	// 		}
	// 	});
	// }

	// // Prevents form submission on pressing of enter in suburb autocomplete field
	// $j(document.body).on('keydown', '.suburb-autocomplete', function(key) {
	// 	if (key.which == 13) {
	// 		key.preventDefault();
	// 		return false;
	// 	}
	// });
}

/*
 *
 * Binds form submission with addDiscountButton ID on click
 * and submits discount form via ajax when clicked on button
 */
function initAddDiscountForm() {
	$j(document.body).on('click', '#addDiscountButton', function() {
		var actionLink = "/ui/adddiscount:adddiscountevent";
		var f = $j("[id*=addDiscountForm]")[0];
		var data = $j(f).serialize();
		var refreshId = "#addDiscountZone";

		$j.ajax({
			type: "GET",
			url: actionLink,
			data: data,
			async: false,
			//if the parameter is not set internet explorer loads content from cache
			cache: false,
			success: function(data) {
				if (data.content) {
					$j(refreshId).html(data.content);
					initAddDiscountForm();
				}
			},
			error:  function(data) {
			}
		});
	});
}

// Get value of the cookie
function getCookie( name ) {
	var start = document.cookie.indexOf(name + "=");
	var len = start + name.length + 1;
	if ((!start) && (name != document.cookie.substring(0,name.length))) {
		return null;
	}

	if (start == -1) {
		return null;
	}

	var end = document.cookie.indexOf(';', len);
	if (end == -1) {
		end = document.cookie.length;
	}

	return unescape( document.cookie.substring( len, end ) );
}

// Sets a cookie
function setCookie( name, value, expires, path, domain, secure ) {
	var today = new Date();
	today.setTime(today.getTime());

	if (expires) {
		expires = expires * 1000 * 60 * 60 * 24;
	}

	var expires_date = new Date(today.getTime() + (expires));
	document.cookie = name+'='+ escape(value) +
		((expires) ? ';expires=' + expires_date.toGMTString() : '') + //expires.toGMTString()
		((path) ? ';path=' + path : '') +
		((domain) ? ';domain=' + domain : '') +
		((secure) ? ';secure' : '');
}

// Deletes a cookie
function deleteCookie( name, path, domain ) {
	if (getCookie(name)) {
		document.cookie = name + '=' +
		((path) ? ';path=' + path : '') +
		((domain) ? ';domain=' + domain : '') +
		';expires=Thu, 01-Jan-1970 00:00:01 GMT';
	}
}


(function($j) {
	$j(document.body).ready(function() {
		initAddStudentButton();
		initSuburbAutoComplete();
		initAddConcessionButtons();
		initAddDiscountForm();

		// Trigger click event on pressing enter when for add student enrol when cursor is not in suburb autocomplete field
		$j(document.body).on('keydown', 'fieldset#student_enrol_credentials input,fieldset#student_enrol_details input', function(e) {
			if (e.keyCode == 13 && $j(this).attr('class').indexOf('suburb-autocomplete') == -1) {
				$j("#add-student-enrol").trigger('click');
			}
		});

		//Bind slide toggle effect for dynamic created toggler class
		$j(document.body).on('click', '.toggler', function() {
			$j(this).toggleClass("clicked");
			$j(this).toggleClass('toggler-expanded');
			$j(this).nextAll('.post-collapse:first').slideToggle(400);
			$j(this).nextAll('.collapse:first').slideToggle(400);
		});

		// Display hidden matching classes
		$j(document.body).on('click', '.more-classes-link', function() {
			$j(this).hide();
			$j(this).nextAll('.more-classes').slideToggle(400);
		});

		initOtherClassesControl();

		// hide the hints and errors at the start
		$j('.reason', '.hint').toggleClass('hidden-text');

		initHints();

		// When a user logs in, show them the company fields or the individual's fields
		$j(document.body).on('click', "#iscompany", function() {
			$j(this).nextAll('.company-details:first').slideToggle(400);
			$j(this).nextAll('.user-details:first').slideToggle(400);
		});

		// show default text in text fields with class defaultText using "default" attribute
		$j(document.body).on('focus', ".defaultText", function(srcc) {
			if ($j(this).val() == $j(this).attr("default")) {
				$j(this).removeClass("defaultTextActive");
				$j(this).val("");
			}
		});

		$j(document.body).on('blur', '.defaultText', function() {
			if ($j(this).val() == "") {
				$j(this).addClass("defaultTextActive");
				$j(this).val($j(this).attr("default"));
			}
		});
		$j(".defaultText").trigger('blur');

		// clear default values when submitting any form
		$j(document.body).on('submit', 'form', function() {
			clearDefaultValues();
			return true;
		});

		// Advanced search
		$j(document.body).on('click', '.show-advanced-search,div#advanced-search-background', function() {
			toggleAdvancedSearch();
		});

		$j(document.body).on('click', '#cancel-search', function() {
			toggleAdvancedSearch();
		});

		//Fill up input value in search form on pressing enter button and submitting the form.
		$j(document.body).on('keyup', '#adv_keyword', function(e) {
			text = $j(this).val();
			$j("form#search > input[type=text]").val(text);
			if (e.keyCode == 13) $j('form#search2').submit();
		});

		if($j('input.quicksearch').length > 0) {
			$j('input.quicksearch').quickSearch("/advanced/keyword");
		}

		// Bind submission of tell-a-friend form on click event
		// Have to do it this way because thickbox is incompatible with Wonder Ajax
		$j(document.body).on('click', '#submitTellAFriendLink', function() {
			submitTellAFriend();
			return false;
		});

		// On the course detail page, display the class info if it is small,
		// or collapse it if it is too big, with a twiddle to expand
		$j("#course_detail .detail_for_class").each(function(i) {
			// only do this collapsing on the course list page
			elem = $j(this);
			if (elem.height() > 50) {
				var id = elem.attr('id');
				elem.before('<div class="show_more_class_info" style="clear:both;" id=x' + id + '><a href="javascript:void();"  >more</a></div>');
				$j('#x'+id).live('click', function() {
					$j('#' + id).show();
					this.hide();
				});
				elem.hide();
			} else {
				this.show();
			}
		});

		/**
		 * Generate tooltips
		 * If you give any HTML element the class "tooltip"
		 * then its title attribute will be displayed over the cursor on hover
		 * [Requires jquery.tooltip plugin]
		 */
		// $j('.tooltip').tooltip({
		// 	top: 0,
		// 	track: true,
		// 	delay: 0,
		// 	showURL: false,
		// 	showBody: " - ",
		// 	fade: 250
		// });

		// Show link content in modal using iframe
		$j(document.body).on('click', '.nyromodalreload', function(e) {
			e.preventDefault();
			var href = $j(this).attr('href');

			if (Ish) {
				var closeFn = Ish.modal.openModal({
					content: '<iframe src="'+ href +'" width="100%" height="100%">'
						+'<p>Your browser does not support iframes.</p>'
					+'</iframe>',
					animation: 'flip',
					duration: 600,
					width: 1000,
					height: 400
				});
			}
		});

		// Show link content in modal
		var _modalCalled = false;
		$j(document.body).on('click', '.nyromodal', function(e) {
			e.preventDefault();
			var href = $j(this).attr('href');

			if(!_modalCalled) {
				_modalCalled = true;
				$j.ajax({
					url: href,
					type: 'GET',
					methodType: 'text',
					success: function(data) {
						if(data !== undefined) {
							if (Ish) {
								var closeFn = Ish.modal.openModal({
									content: data,
									animation: 'flip',
									duration: 600,
									width: 1000,
									height: 400/*,
									onClose: function() { }*/
								});
								_modalCalled = false;
							}
						}
					},
					error: function(error) {
						_modalCalled = false;
					}
				});
			}
		});

		// Hide the location map on sites page then reveal it when its control is clicked.
		$j('.collapsedLocationMap').hide();
		$j(document.body).on('click', '.showLocationMap', function() {
			$j('.showLocationMap').hide();
			$j('#location').show();
			$j('#location').removeClass('collapsedLocationMap');
			mapLoadForID('map');
			return false;
		});

		// Display google map and zoom map for related site' location
		$j(document.body).on('click', '.location-course', function() {
			var rel = $j(this).attr('rel');
			if(rel != '') {
				zoomMapForSite(rel);
			}
		});

		// Show modal for session
		$j(document.body).on('click', 'a.timeline', function(e) {
			e.preventDefault();
			e.stopPropagation();
			var id = '#sessions_for_class_'+$j(this).parents('.classItem').attr('data-classid');

			if (Ish) {
				var closeFn = Ish.modal.openModal({
					content: $j(id).html(),
					animation: 'flip',
					duration: 600,
					width: 1000,
					height: 400/*,
					onClose: function() { }*/
				});
			}
		});

		$j(document.body).on('click', '#overlay, #timelineClose', function(e) {
			$j('overlay').hide();
			$j('timeline-wrap').style.visibility = 'hidden';
			return false;
		});

		// Add HTML5 placeholder in ie8
		$j(".ie8 input[type='text'], .ie9 input[type='text']").each(function() {
			if ($j(this).val() == "" && $j(this).attr("placeholder") != "") {
				$j(this).val($j(this).attr("placeholder"));
				$j(this).on('focus', function() {
					if ($j(this).val() == $j(this).attr("placeholder")) $j(this).val("");
				});
				$j(this).on('blur', function() {
					if ($j(this).val() == "") $j(this).val($j(this).attr("placeholder"));
				});
			}
		});

		// Load more courses / show message
		var _processing = false;
		$j(document.body).on('click', '#showMoreCourses', function() {
			$j('#showMore').append("<div class='message'></div>");
			var link = this.href;
			_processing = true;

			$j.ajax({
				type: "GET",
				url:  link,
				success: function(msg) {
					$j('#showMore').replaceWith(msg);

					// Since this call possibly returns react markers,
					// we should told react about it.
					if (window.Ish) {
						window.Ish.react.bootstrap();
					}
					if($j.trim($j('#sitesParameter').text()) != '') {
						$j.ajax({
							type: "GET",
							url:  "/coursesSitesMap?sites="+$j('#sitesParameter').text(),
							success: function(msg1) {
								$j('#sitesMap > #focus-map').replaceWith($j(msg1).children('#focus-map'));
								_processing = false;
							}
						});
					}
					else {
						_processing = false;
						clearInterval(loadCourses);
					}
				}
			});
			//initOtherClassesControl();
			return false;
		});

		// On scroll trigger a show more courses when #showMoreCourses visible
		$j(window).on('scroll', function() {
			if(($j('#content').innerHeight() + $j("#showMore").height()) < ($j(window).scrollTop() + $j(window).height() - $j('#header').height()) && _processing == false) {
				$j('#showMoreCourses').trigger('click');
			}
		});

		function IpadScroll() {
			if(($j('#content').innerHeight() + $j("#showMore").height()) < ($j(window).scrollTop() + $j(window).height() + $j('#header').height()) && _processing == false) {
				$j('#showMoreCourses').trigger('click');
			}
		}

		/*if( /iPhone|iPad/i.test(navigator.userAgent)) {
			document.addEventListener("scroll", IpadScroll, false);
		}*/

		var loadCourses = setInterval(function() {
			if( /iPhone|iPad/i.test(navigator.userAgent)) {
				IpadScroll();
			}
		}, 1000);

	});

	$j(document).on('click', '.course_modules > span', function() {
		$j(this).toggleClass('active');
		$j(this).next('ul').slideToggle(500);
	});

})(jQuery);

function initHints(parentBlockId){
	parentBlockToUpdate='';
	if(parentBlockId!=null){
		parentBlockToUpdate = '#'+parentBlockId+' ';
	}

	// Show hints on entering the field if the input is not invalid
	jQuery(document).on('focus blur', parentBlockToUpdate+'span.valid input', function() {
		var parent = $j(this).siblings('.validate-text');
		if(parent.find('.hint').length==0) {
			//for the inputs that have one more image before hint (like calendar tapestry component)
			parent=parent.next();
		}
		parent.find('.hint').toggleClass('hidden-text');
		//$j(this).parent().nextAll('.hint:first').toggle();
	});

	// Show reason for errors on entering the field if the input IS invalid
	jQuery(document).on('focus blur', parentBlockToUpdate+'span.validate input', function() {
		var parent = $j(this).siblings('.validate-text');
		if(parent.find('.reason').length==0){
			//for the inputs that have one more image before hint (like calendar tapestry component)
			parent=parent.next();
		}
		parent.find('.reason').toggleClass('hidden-text');
	});

	jQuery(document).on('focus blur', parentBlockToUpdate+'span.validate select', function() {
		var parent = $j(this).siblings('.validate-text');
		if(parent.find('.reason').length==0){
			//for the inputs that have one more image before hint (like calendar tapestry component)
			parent=parent.next();
		}
		parent.find('.reason').toggleClass('hidden-text');
	});
}

function initOtherClassesControl(){

	// display other location classes
	$j(document.body).on('click', '.other-classes-control', function() {
		$j(this).hide();
		$j(this).nextAll('.other-classes').slideToggle(400);
	});

	// display hidden cancelled or full classes
	$j(document.body).on('click', '.full-classes-control', function() {
		$j(this).hide();
		$j(this).nextAll('.full-classes').slideToggle(400);
	});
}
