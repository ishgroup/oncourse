goog.provide('custom');

goog.require('jquery');
goog.require('jquery.ui');
goog.require('jquery.calendario');
goog.require('bootstrap');
goog.require('jquery.flexisel');
goog.require('respond');



var $j = jQuery.noConflict();

function popup_center() {
    $j('.popup').css({
        'margin-top': -($j('.popup').height() / 2)
    });
}

var currentPosition = 0;
var sessionPosition = 0;
var slideWidth;
var slides;
var numberOfSlides;
var showSlides = 3;
var SlidesToShow = 3;
var singleSlideWidth;
var singleSessionWidth;
var isSetSlideWidth = true;

$j(document).ready(function () {
    $j('.class-week li.active').popover({
        selector: '',
        placement: 'bottom',
        trigger: 'hover',
        html: true,
        content: function () {
            return $j(this).find('.tooltip-content').html();
        }
    });
	
	var path_hash = window.location.hash;
	var hash = path_hash.split('#')[1];
	if(hash == 'review') {
		$j('#class-reviews').slideDown();
	}
	
    $j(".next-session-info button").click(function () {
        $j(".more-session").slideToggle('slow');
    });
     $j('.slider-button').click(function () {
        if (!$j(this).hasClass('on')) {
            $j(this).addClass('on').text('as company');
            $j(".user-login").slideUp();
            $j(".company-login").slideDown();
        } else {
            $j(this).removeClass('on').text('as person');
            $j(".user-login").slideDown();
            $j(".company-login").slideUp();
        }
    });
    /* slider start */
	
    /*slideWidth = $j('.session-content > div').width();
    slides = $j('.single-session');
    numberOfSlides = slides.length;
    slides.css({
        'float': 'left',
        'width': slideWidth + 30
    });
    $j('.session-content section').css('width', slideWidth * numberOfSlides + ( numberOfSlides * 30));*/
	
	
	$j('.session-content section').width($j('.session-content > div').width());
	slides = $j('.session-content section > div');
	numberOfSlides = slides.length;
	singleSlideWidth = ($j('.session-content section').width())/showSlides;
	slides.css({'width':singleSlideWidth, 'float': 'left'});
	var slideContentWidth = (slides.outerWidth()) * (slides.length);
	$j('.session-content section').width(slideContentWidth);
	
	
	
	
	
	
    manageControls(currentPosition);
		if(numberOfSlides <= 3)
		$j('.session-content #session-prev').hide();
		$j('.session-content #session-next').hide();
    $j('[id*="session-"]').bind('click', function () {
        currentPosition = ($j(this).attr('id') == 'session-prev') ? currentPosition + 1 : currentPosition - 1;
        $j(".map-conetnt").slideUp('slow');
        manageControls(currentPosition);
        $j('.session-content section').animate({
            'marginLeft': (singleSlideWidth) * (-currentPosition)
        }, 500);
    });

    function manageControls(position) {
        if (position == 0) {
            $j('#session-next').hide();
        } else {
            $j('#session-next').show();
        } if (position == numberOfSlides - showSlides) {
            $j('#session-prev').hide();
        } else {
            $j('#session-prev').show();
        }
    }
    var slideCount = 1;
    slides.each(function () {
        $j(this).find('.row > div').each(function () {
            $j(this).addClass('session-' + slideCount);
            slideCount = slideCount + 1;
        });
    });
	
	/* Timetable Session slider starts */
	function manageControlarrows(position) {
        if (position == 0) {
			$j('#timetable-session-up').css({'visibility':'hidden'});
        } else {
            $j('#timetable-session-up').css({'visibility':'visible'});
        } if (position == slideLength - SlidesToShow) {

            $j('#timetable-session-down').css({'visibility':'hidden'});
        } else {
            $j('#timetable-session-down').css({'visibility':'visible'});
        }
    }
	
	singleSessionWidthCalc = $j('.session-list-content').width();
	//$j('.session-list > li').width(singleSessionWidthCalc);
	$j('.session-list > li').css({'width':(singleSessionWidthCalc / SlidesToShow), 'float': 'left'});
	singleSessionWidth = $j('.session-list > li').width();
	var slideLength = $j('.session-list > li').length;
	
	$j('.list-unstyled.session-list').width(singleSessionWidth * slideLength);
  
	manageControlarrows(sessionPosition);
  if(slideLength <= showSlides) {
    $j('#timetable-session-up, #timetable-session-down').css({'visibility':'hidden'});
  }
	
	$j('[id*="timetable-session-"]').bind('click', function () {
        sessionPosition = ($j(this).attr('id') == 'timetable-session-down') ? sessionPosition + 1 : sessionPosition - 1;
        manageControlarrows(sessionPosition);
		$j('.list-unstyled.session-list').animate({
            'left': (singleSessionWidth) * (-sessionPosition)
        }, 500);
		return false;
    });
	
	/* Timetable Session slider ends */
	
	var courses_length = $j(".classes-content > li").length;
	if(courses_length <= 3)
		$j(".expanded .classes-content + .list-inline .show-all-sessions").hide();
	$j(".expanded .classes-content > li:nth-child(3) ~ li").hide();
	$j('.show-all-sessions').click(function(){
		$j(".expanded .classes-content > li:nth-child(3) ~ li").slideDown();
		$j(this).hide();
		return false;
	});
	
	$j(document).on('click', 'a.event', function () {
        var dataRel = $j(this).attr('href').replace(/#/, "");
		$j('#myModal').modal('hide');
		
        if ($j('section > div[rel=' + dataRel + ']').length == 1) {
			/*$j('.fc-content div a').removeClass('current');
			$j(this).addClass('current');*/
			$j('section > div ul').removeClass('current');
			$j('section div[rel=' + dataRel + '] ul').addClass('current');
			
			//var slideToMove = $j('section div[rel=' + dataRel + ']').index() - 1;
			var slideToMove = $j('section div[rel=' + dataRel + ']').index() + 1;
			
      console.log("Slide to move"+slideToMove);
      console.log("number Of Slides"+numberOfSlides);
      console.log("single Slide Width"+singleSlideWidth);
      
      if(SlidesToShow > 1) {
        if(slideToMove == numberOfSlides) {
          $j('.session-content section').animate({
            'marginLeft': -(slideToMove-SlidesToShow) * singleSlideWidth
          }, 500);
          currentPosition = slideToMove-SlidesToShow;
        }
        else {
          $j('.session-content section').animate({
            'marginLeft': -(slideToMove-(SlidesToShow-1)) * singleSlideWidth
          }, 500);
          currentPosition = slideToMove-SlidesToShow;
        }
      }
      else {
        console.log('test');
        $j('.session-content section').animate({
          'marginLeft': -(slideToMove-1) * singleSlideWidth
        }, 500);
        currentPosition = (slideToMove-1);
      }
			/*if(slideToMove >= numberOfSlides - 3) {
				$j('.session-content section').animate({
					'marginLeft': -(numberOfSlides - 3) * singleSlideWidth
				}, 500);
				currentPosition = (numberOfSlides - 3);
			}
			else {
        $j('.session-content section').animate({
					'marginLeft': -slideToMove * singleSlideWidth
				}, 500);
				currentPosition = slideToMove;
			}*/
			
            //console.log(currentPosition);
            manageControls(currentPosition);
			$j('html, body').animate({
				scrollTop: $j(".session-content").offset().top - 125
			}, 500);
        }
        return false;
    });
	
    /*$j(document).on('click', '.fc-content div a', function () {
        var dataRel = $j(this).attr('href').replace(/#/, "");
		
        if ($j('section > div[rel=' + dataRel + ']').length == 1) {
			$j('.fc-content div a').removeClass('current');
			$j(this).addClass('current');
			$j('section > div').removeClass('current');
			$j('section div[rel=' + dataRel + ']').addClass('current');
			
			var slideToMove = $j('section div[rel=' + dataRel + ']').index() - 1;
			
			if(slideToMove >= numberOfSlides - 3) {
				$j('.session-content section').animate({
					'marginLeft': -(numberOfSlides - 3) * singleSlideWidth
				}, 500);
				currentPosition = (numberOfSlides - 3);
			}
			else {
				$j('.session-content section').animate({
					'marginLeft': -slideToMove * singleSlideWidth
				}, 500);
				currentPosition = slideToMove;
			}
			
            //console.log(currentPosition);
            manageControls(currentPosition);
			$j('html, body').animate({
				scrollTop: $j(".session-content").offset().top - 125
			}, 500);
        }
        return false;
    });*/
    $j('.class-roll-heading').tooltip({
        'html': true,
        'title': '<ul class="list-inline">\
		<li><small>Enrolments: 5</small></li>\
		<li>|</li>\
		<li><small>Maximum: 50</small></li>\
		<li>|</li>\
		<li><small>Minimum: 20</small></li>\
		</ul>'
    });
    $j(".pills-tab a[href='#classroll']").click(function () {
       /* setTimeout(function () {
            var currentPosition = 0;
            var slideWidth = $j('.session-content > div').width();
            var slides = $j('.single-session');
            var numberOfSlides = slides.length;
            slides.css({
                'float': 'left',
                'width': slideWidth
            });
            $j('.session-content section').css('width', slideWidth * numberOfSlides);
            manageControls(currentPosition);
            $j('[id*="session-"]').unbind().bind('click', function () {
                currentPosition = ($j(this).attr('id') == 'session-prev') ? currentPosition + 1 : currentPosition - 1;
                $j(".map-conetnt").slideUp('slow');
                manageControls(currentPosition);
                $j('.session-content section').animate({
                    'marginLeft': slideWidth * (-currentPosition)
                }, 900);
            });
        }, 50);*/
    });
    $j(".single-session li a.venue").click(function () {
        $j(".map-conetnt").slideDown('slow');
        setTimeout(function () {
            google.maps.event.trigger(map, 'resize');
        }, 500);
        $j('html,body').animate({
            scrollTop: $j(".map-conetnt").offset().top
        }, 'slow');
        return false;
    });
	
	/* refresh google map */
	
	$j('.header-tabs li').not('.active').click(function(){
		setTimeout(function () {
      //google map  initialize function is being added for specific page.
      if (typeof initialize == 'function')
      {
          initialize();
      }
      //google.maps.event.trigger(map, 'resize');
    }, 500);
	});
	
    $j(".session-list > li .mark-roll").click(function () {
        /*$j(".session-list > li").removeClass('active');*/
		//$j(".session-list > li").hide();
		$j(".session-wrapper, .course-detail").slideUp();
		$j(".class-roll-content .info-text").show();
		//$j(".past-sessions").hide();
		$j(".session-info").hide();
		$j('.attendance-content').fadeIn('slow');
		/*$j('.list-unstyled.session-list').css({
            'left': 0
        });*/
		//$j('.attendance').width('auto');
		//$j(this).hide().next('.marking-roll').show();
        //$j(this).parents('li').show().addClass('active');
		var el = $j('.attendance');
		el.addClass('act');
        /*var index = 0;
        var timer = window.setInterval(function () {
            if (index < el.length) {
                var indexing = index++;
                el.eq(indexing).addClass('act');
                setTimeout(function () {
                   // el.eq(indexing).removeClass('act');
                }, 200);
            } else {
                window.clearInterval(timer);
            }
        }, 200);*/
		return false;
    });
    $j(".hide-map").click(function () {
        $j(".map-conetnt").slideUp('slow');
    });
    $j('.tooltip-info').tooltip();
	/*$j(".show-attendance").click(function(){
		$j(".attendance-content").show();
		$j(".attendance-content").next().removeClass('col-lg-6 col-sm-6').addClass('col-lg-4 col-sm-4');
		$j(".attendance-content").prev().removeClass('col-lg-6 col-sm-6').addClass('col-lg-4 col-sm-4');
		return false;
	});
		
	$j(".close-attendance").click(function(){
			$j(".attendance-content").hide();
			$j(".attendance-content").next().removeClass('col-lg-4 col-sm-4').addClass('col-lg-6 col-sm-6');
			$j(".attendance-content").prev().removeClass('col-lg-4 col-sm-4').addClass('col-lg-6 col-sm-6');
			return false;
	});*/
    $j(document).on('change', '#agree', function () {
        if ($j(this).is(':checked')) {
            $j('.agree-btn').removeAttr('disabled');
            $j('.decline-content').slideUp('slow');
        } else {
            $j('.agree-btn').attr('disabled', 'disabled');
            $j('.decline-content').slideDown('slow');
        }
    });
	/*$j(".attendance .btn-group button").click(function(){
		var parant = $j(this).parents('.btn-group');
		parant.find("button").removeClass('btn-primary btn-danger').addClass('btn-default');
		if($j(this).not('active').hasClass('btn-ok')) {
			$j(this).removeClass('btn-default').addClass('btn-primary');
		}
		if($j(this).not('active').hasClass('btn-cancel')) {
			$j(this).removeClass('btn-default').addClass('btn-danger');
		}
		parant.find("button").not('.active').addClass('btn-dark');
		parant.find("button").removeClass('active');
		$j(this).addClass('active');
	});*/
	
	$j(".attendance button").click(function(){
		var parant = $j(this).parents('.attendance');
		parant.find("button").removeClass('btn-primary btn-danger').addClass('btn-default');
		
		
		if(!$j(this).hasClass('active')) {
			
			parant.find("button").removeClass('active');
			if($j(this).hasClass('btn-ok')) {
				$j(this).removeClass('btn-default').addClass('btn-primary active');
			}
			
			if($j(this).hasClass('btn-cancel')) {
				$j(this).removeClass('btn-default').addClass('btn-danger active');
			}
			
		} else if($j(this).hasClass('active')){
			if($j(this).hasClass('btn-ok')) {
				$j(this).removeClass('btn-primary active').addClass('btn-default');
			}
			
			if($j(this).hasClass('btn-cancel')) {
				$j(this).removeClass('btn-danger active').addClass('btn-default');
			}
		}
		parant.find("button").not('.active').addClass('btn-dark');
		return false;
	});
	
    /*$j(".attendance button").click(function () {
        if ($j(this).hasClass('btn-disable')) {
            $j(this).removeClass('btn-disable').addClass('btn-ok btn-primary active');
            $j(this).find('span').removeClass('glyphicon-unmarked').addClass('glyphicon-ok');
        } else if ($j(this).hasClass('btn-ok')) {
            $j(this).removeClass('btn-ok btn-primary').addClass('btn-cancel btn-danger active');
            $j(this).find('span').removeClass('glyphicon-ok btn-primary').addClass('glyphicon-remove');
        } else {
            $j(this).removeClass('btn-cancel btn-danger active').addClass('btn-disable');
            $j(this).find('span').removeClass('glyphicon-remove').addClass('glyphicon-unmarked');
        }
    });*/
	
	// show all the sessions 
	
	$j('.info-text .finish, .marking-roll').click(function(){
		$j(".session-wrapper, .course-detail").slideDown();
		//$j('.marking-roll').hide();
		//$j('.mark-roll').show();
		$j('.class-roll-content .info-text').hide();
		//$j(".session-list > li").removeClass('active').show();
		$j(".session-list > li").removeClass('active');
		$j(".session-info").show();
		//$j(".past-sessions").show();
		$j('.attendance-content').hide('slow');
		/*$j('.list-unstyled.session-list').css({
            'left': (singleSessionWidth) * (-sessionPosition)
        });*/
		//$j('.attendance').width(0);
		var el = $j('.attendance');
        var index = 0;
		el.removeClass('act');
        /*var timer = window.setInterval(function () {
            if (index < el.length) {
                var indexing = index++;
                el.eq(indexing).removeClass('act');
                setTimeout(function () {
                   // el.eq(indexing).removeClass('act');
                }, 200);
            } else {
                window.clearInterval(timer);
            }
        }, 200);*/
		return false;
	});
	
	$j('.show-full-description').click(function() {
		var me = $j(this);
		if(!me.hasClass('active')) {
			$j('.class-detail').css({'height':'100%'});
			me.find('.text').html('Less');
			me.find('span.icon-arrow-down').removeClass('icon-arrow-down').addClass('icon-arrow-up');
			$j('.class-detail').find('strong').hide();
			me.addClass('active');
		}
		else {
			$j('.class-detail').css({'height':'34px'});
			$j('.class-detail').find('strong').show();
			me.find('span.icon-arrow-up').removeClass('icon-arrow-up').addClass('icon-arrow-down');
			me.find('.text').html('Show all');
			me.removeClass('active');
		}
		return false;
	});
	
    $fluidEl = $j(".container > .col-lg-9");
    $j('#venue').data('aspectRatio', this.height / this.width);
    $j(window).resize(function () {
        var newWidth = $fluidEl.width();
        $j("#venue").height(newWidth * $j("#venue").data('aspectRatio'));
		/*console.log(newWidth * $j("#venue").data('aspectRatio'));*/
    }).resize();
    $j('.calendar-container').css({
        'height': ($j(window).height()+$j('#footer').height())
    });
    popup_center();
    
    var url = document.location.toString();
    var location_value = url.substring(url.lastIndexOf('/') + 1);
    if(location_value == "location")
      $j('.nav-tabs a[href=#class-location]').tab('show');
    
    /*if (url.match('#')) {
        $j('.nav-tabs a[href=#'+url.split('#')[1]+']').tab('show');
    }*/

    // Change hash for page-reload
    $j('.nav-tabs a').on('shown', function (e) {
        window.location.hash = e.target.hash;
    });
    
    var studentClassRollHeight = 0;
    $j('.student-list > li').each(function(){
      studentClassRollHeight = ($j(this).height() >= studentClassRollHeight) ? $j(this).outerHeight() : studentClassRollHeight;
    });
    $j('.student-list > li').css({'min-height':studentClassRollHeight});
});

$j(document).load(function(){
  if(isSetSlideWidth) {
		var buffer = 0;
		slides.find('ul').removeAttr('style');
		slides.each(function(){
			if($j(this).height()> buffer) {
				buffer = $j(this).height();
			}
		});
		slides.find('ul').delay( 800 ).css({'min-height': buffer});
		isSetSlideWidth = false;
	}
})


$j(window).resize(function () {
  popup_center();
  $j('.calendar-container').css({
      'height': ($j(window).height()+$j('#footer').height())
  });
  
  var studentClassRollHeight = 0;
  $j('.student-list > li').each(function(){
    studentClassRollHeight = ($j(this).height() >= studentClassRollHeight) ? $j(this).outerHeight() : studentClassRollHeight;
  });
  $j('.student-list > li').css({'min-height':studentClassRollHeight});
  
	//console.log($j(window).width());
	if($j(window).width() <= 1186) {
		showSlides = 2;
		SlidesToShow = 2;
    
    if($j(window).width() <= 500) {
			showSlides = 1;
			SlidesToShow = 1;
		}
	}
	else {
		showSlides = 3;
		SlidesToShow = 3;
	}
	
	$j('.session-content section').width($j('.session-content > div').width());
	
	numberOfSlides = slides.length;
	singleSlideWidth = ($j('.session-content section').width())/showSlides;
  if(isSetSlideWidth) {
		var buffer = 0;
		slides.find('ul').removeAttr('style');
		slides.each(function(){
			if($j(this).height()> buffer) {
				buffer = $j(this).height();
			}
		});
		slides.find('ul').css({'min-height': buffer});
	}
	isSetSlideWidth = true;
	slides.css({'width':singleSlideWidth, 'float': 'left'});
	var slideContentWidth = (slides.outerWidth()) * (slides.length);
	$j('.session-content section').width(slideContentWidth);
	
	$j('.session-content section').animate({
		'marginLeft': (singleSlideWidth) * (-currentPosition)
	}, 500);
	
	
	
	// single session width
	var slideLength = $j('.session-list > li').length;
	var singleSessionWidthCalc = $j('.session-list-content').width();
	
	$j('.session-list > li').css({'width':(singleSessionWidthCalc / SlidesToShow), 'float': 'left'});
	singleSessionWidth = $j('.session-list > li').width();
	//console.log(singleSessionWidth);
	$j('.list-unstyled.session-list').width(singleSessionWidth * slideLength);
	
	$j('.list-unstyled.session-list').animate({
		'left': (singleSessionWidth) * (-sessionPosition)
	}, 500);
	
	/*slideWidth = $j('.session-content > div').width();
    slides = $j('.single-session');
    numberOfSlides = slides.length;
    slides.css({
        'float': 'left',
        'width': slideWidth + 30
    });
	$j('.session-content section').animate({
		'marginLeft': (slideWidth + 30) * (-currentPosition)
	}, 900);
	
	$j('.session-content section').css('width', slideWidth * numberOfSlides + ( numberOfSlides * 30));
	*/
  
  if($j('.login-form').length === 1) {
		$j('.login-form').parents('.main-container').addClass('login-container');
	}
  
  if($j('#profile-form').length === 1) {
    $j('#profile-form').parents('.main-container').addClass('profile-container');
  }

});