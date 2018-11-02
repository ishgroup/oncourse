goog.provide('surveys');

goog.require('jquery');
goog.require('jquery.raty');
goog.require('initialise');

var $j = jQuery.noConflict();

   function Survey(id) {
        this.id=id;
    }

Survey.prototype = {
  
    id:0,

    initAverageRating: function (score) {
        var self = this;
        $j("span[data='" + self.id + "'].rating").raty({
            half: false,
            size: 24,
            starOff: '/portal/img/svg_icons/star-off.svg',
            starOn: '/portal/img/svg_icons/star-on.svg',
            hints: ['', '', '', '', ''],
            readOnly: true,
            score: score,
            width: 90
        });
    },

    initRate: function (rateHtml, score, readOnly) {
        var self = this;
        rateHtml.raty({
            half: false,
            size: 24,
            starOff: '/portal/img/svg_icons/star-off-big.svg',
            starOn: '/portal/img/svg_icons/star-on-big.svg',
            hints: ['', '', '', '', ''],
            width: 90,
            readOnly: readOnly,
            score: score,
            click: function (scoreVal, evt) {
                var venueScore = $j("div[data='" + self.id + "'].class-reviews").find("span.venue-rate").raty("score");
                var tutorScore = $j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate").raty("score");
                var courseScore = $j("div[data='" + self.id + "'].class-reviews").find("span.course-rate").raty("score");

                var evtClasses = evt.target.parentElement.className;
                var averageRate = Math.round(
                    (evtClasses.endsWith('venue-rate') ? scoreVal + tutorScore + courseScore :
                        evtClasses.endsWith('tutor-rate') ? venueScore + scoreVal + courseScore :
                            venueScore + tutorScore + scoreVal) / 3);
                self.initAverageRating(averageRate);
            }
        });
    },

    initNetPromoterScoreRate: function (rateHtml, score, readOnly) {
        var self = this;
        rateHtml.raty({
            half: false,
            size: 24,
            starOff: '/portal/img/svg_icons/star-off-big.svg',
            starOn: '/portal/img/svg_icons/star-on-big.svg',
            hints: ['not likely', '', '', '', '', '', '', '', '', 'very likely'],
            readOnly: readOnly,
            number: 10,
            score: score,
            click: function (scoreVal, evt) {
                self.refreshNetPromoterScore(scoreVal);
            }
        });
    },

    initializeSurveys: function () {
        var self = this;
			  var formElement = $j("div[data='" + self.id + "'].class-reviews")
			  var readOnly = $j(formElement).prop("data-readonly");
      
      
        if ($j(formElement).find("span.netPromoterScore-rate").length) {
					this.initNetPromoterScoreRate($j(formElement).find("span.netPromoterScore-rate"), $j(formElement).find("span.netPromoterScore-rate").attr("data-value"), readOnly);
				}
        if ($j(formElement).find("span.venue-rate").length) {
          this.initRate($j(formElement).find("span.venue-rate"), $j(formElement).find("span.venue-rate").attr("data-value"), readOnly);
        }
        if ($j(formElement).find("span.tutor-rate").length) {
            this.initRate($j(formElement).find("span.tutor-rate"), $j(formElement).find("span.tutor-rate").attr("data-value"), readOnly);
        }
        if ($j(formElement).find("span.course-rate").length) {
          this.initRate($j(formElement).find("span.course-rate"), $j(formElement).find("span.course-rate").attr("data-value"), readOnly);
        }
      
        $j(formElement).find("button.rate-submit").click(function () {
            self.saveSurvey();
        });

			  $j("span[data='" + self.id + "'].rate-class").click(function () {
            self.slideSurveys()
        });
			  
        this.fillSurvey();

        if (readOnly) {
             $j(formElement).parent().find("span.rate-class").tooltip({ content: 'Click here to see reviews', position: {
                     my: "center bottom",
                     at: "center top-20"
                 }
             });
        }
        else {
            $j(formElement).parent().find("span.rate-class").tooltip({ content: 'Click here to provide reviews', position: {
                    my: "center bottom",
                    at: "center top-20"
                }
            });
        }
        
			  if ($j(formElement).find("span.netPromoterScore-rate").length) {
					this.refreshNetPromoterScore($j(formElement).find("span.netPromoterScore-rate").attr("data-value"))
				}
    },
    //slide survey form
    slideSurveys: function () {
        var self = this;
        $j("div[data='" + self.id + "'].class-reviews").slideToggle("fast");
    },

    refreshAverageRating: function () {
        var self = this;
        var venue=0;
			  var tutor=0;
			  var course=0;
			  var formElement = $j("div[data='" + self.id + "'].class-reviews")

			  if ($j(formElement).find("span.venue-rate").length) {
					venue = $j(formElement).find("span.venue-rate").raty("score")
        }
        if ($j(formElement).find("span.tutor-rate").length) {
					tutor = $j(formElement).find("span.tutor-rate").raty("score")
        }
        if ($j(formElement).find("span.course-rate").length) {
          course = $j(formElement).find("span.course-rate").raty("score")
        }
      
        this.initAverageRating(Math.floor( venue + tutor +course / 3));
    },

    refreshNetPromoterScore: function(score) {
			var self = this;
			var formElement = $j("div[data='" + self.id + "'].class-reviews")

			var customPlaceholder = $j(formElement).prop("data-placeholder");
			
			if (customPlaceholder) {
				$j(formElement).find("textarea.survey-comment").attr('placeholder', customPlaceholder);
			} else {
				var placeholderMessage = '';
				if (score >= 1 && score <= 6) {
					placeholderMessage = 'Please tell us how we could have improved your experience.';
				} else {
					if (score >= 7 && score <= 10) {
						placeholderMessage = 'What did you most enjoy about your experience.';
					}
				}
				$j(formElement).find("textarea.survey-comment").attr('placeholder', placeholderMessage);
			}
		},

    fillSurvey: function () {
        
        var self = this;
			  var formElement = $j("div[data='" + self.id + "'].class-reviews");
        var netScore = $j(formElement).find("span.netPromoterScore-rate");
        if ($j(netScore).length) {
					$j(netScore).raty("score", $j(netScore).attr("data-value"));
				}
				
			  var venueScore = $j(formElement).find("span.venue-rate");
			  if ($j(venueScore).length) {
					$j(venueScore).raty("score", $j(venueScore).attr("data-value"));
				}
				
        var tutorScore = $j(formElement).find("span.tutor-rate");
        if ($j(tutorScore).length) {
            $j(tutorScore).raty("score", $j(tutorScore).attr("data-value"));
        }
        
        var courseScore = $j(formElement).find("span.course-rate");
        if ($j(courseScore).length) {
            $j(courseScore).raty("score", $j(courseScore).attr("data-value"));
        }
        
        this.refreshAverageRating();
    },

    //commit surfey changes
    saveSurvey: function () {
        
        var self = this;
			  var validationError = $j("div[data='" + self.id + "'].class-reviews > div > div.alert-danger")
			  var formElement = $j("div[data='" + self.id + "'].class-reviews");
        if ($j(validationError).length) {
					$j(validationError).remove();
        }
        var actionLink = "/portal/class.classdetailsnew.surveys:saveSurvey/" + self.id;
        var data = {

            "netPromoterScore": $j(formElement).find("span.netPromoterScore-rate").find('input[name=score]').val(),
            "comment": $j(formElement).find("textarea.survey-comment").val(),
            "courseScore": $j(formElement).find("span.course-rate").find('input[name=score]').val(),
            "tutorScore": $j(formElement).find("span.tutor-rate").find('input[name=score]').val(),
            "venueScore": $j(formElement).find("span.venue-rate").find('input[name=score]').val()
        };

			  $j(formElement).find("input[name='customField']").each(function (index, element) {
					data[$j(element).attr("data-key")] = $j(element).val()
				});
        
        $j.ajax({
            url: actionLink,
            async: false,
            cache: false,
            data: data,
            success: function (data) {
                if (data.error) {
									$j("div[data='" + self.id + "'].class-reviews > div").append("<div class='alert alert-danger' style='display: inline-block; margin-left: 20px; padding: 8px;'>" + data.error + "</div>")
                } else {
									self.slideSurveys();
								}
            },
            error: function (jqXHR, textStatus, errorThrown) {
                // window.location.reload();
                console.log("Unexpected exception");
            }
        });
    }
};


$j('document').ready(function () {
    if ($j('.class-reviews').length != 0) {
        $j('.class-reviews').each(function (index, element) {
            new Survey($j(element).attr("data")).initializeSurveys();
        });
    }
});
