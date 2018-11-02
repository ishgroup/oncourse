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
        $j("div[data='" + self.id + "'].class-reviews").parent().find(".rating").raty({
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
			  var readOnly = $j("div[data='" + self.id + "'].class-reviews").prop("data-readonly");
          
        if ($j("div[data='" + self.id + "'].class-reviews").find("span.netPromoterScore-rate").length) {
					this.initNetPromoterScoreRate($j("div[data='" + self.id + "'].class-reviews").find("span.netPromoterScore-rate"), $j("div[data='" + self.id + "'].class-reviews").find("span.netPromoterScore-rate").attr("data-value"), readOnly);
				}
        if ($j("div[data='" + self.id + "'].class-reviews").find("span.venue-rate").length) {
          this.initRate($j("div[data='" + self.id + "'].class-reviews").find("span.venue-rate"), $j("div[data='" + self.id + "'].class-reviews").find("span.venue-rate").attr("data-value"), readOnly);
        }
        if ($j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate").length) {
            this.initRate($j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate"), $j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate").attr("data-value"), readOnly);
        }
        if ($j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate").length) {
            this.initRate($j("div[data='" + self.id + "'].class-reviews").find("span.course-rate"), $j("div[data='" + self.id + "'].class-reviews").find("span.course-rate").attr("data-value"), readOnly);
        }
      
        $j("div[data='" + self.id + "'].class-reviews").find("button.rate-submit").click(function () {
            self.saveSurvey();
        });

			  $j("span[data='" + self.id + "'].rate-class").click(function () {
            self.slideSurveys()
        });
        this.fillSurvey();

        if (readOnly) {
             $j("div[data='" + self.id + "'].class-reviews").parent().find("span.rate-class").tooltip({ content: 'Click here to see reviews', position: {
                     my: "center bottom",
                     at: "center top-20"
                 }
             });
        }
        else {
            $j("div[data='" + self.id + "'].class-reviews").parent().find("span.rate-class").tooltip({ content: 'Click here to provide reviews', position: {
                    my: "center bottom",
                    at: "center top-20"
                }
            });
        }
        
			  if ($j("div[data='" + self.id + "'].class-reviews").find("span.netPromoterScore-rate").length) {
					this.refreshNetPromoterScore($j("div[data='" + self.id + "'].class-reviews").find("span.netPromoterScore-rate").attr("data-value"))
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
			  if ($j("div[data='" + self.id + "'].class-reviews").find("span.venue-rate").length) {
					venue = $j("div[data='" + self.id + "'].class-reviews").find("span.venue-rate").raty("score")
        }
        if ($j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate").length) {
					tutor = $j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate").raty("score")
        }
        if ($j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate").length) {
          course = $j("div[data='" + self.id + "'].class-reviews").find("span.course-rate").raty("score")
        }
      
        this.initAverageRating(Math.floor( venue + tutor +course / 3));
    },

    refreshNetPromoterScore: function(score) {
			var self = this;

			var customPlaceholder = $j("div[data='" + self.id + "'].class-reviews").prop("data-placeholder");
			if (customPlaceholder) {
				$j("div[data='" + self.id + "'].class-reviews").find("textarea.survey-comment").attr('placeholder', customPlaceholder);

			} else {
				var placeholderMessage = '';
				if (score >= 1 && score <= 6) {
					placeholderMessage = 'Please tell us how we could have improved your experience.';
				} else {
					if (score >= 7 && score <= 10) {
						placeholderMessage = 'What did you most enjoy about your experience.';
					}
				}
				$j("div[data='" + self.id + "'].class-reviews").find("textarea.survey-comment").attr('placeholder', placeholderMessage);
			}
		},

    fillSurvey: function () {
        var self = this;
        if ($j("div[data='" + self.id + "'].class-reviews").find("span.netPromoterScore-rate").length) {
					$j("div[data='" + self.id + "'].class-reviews").find("span.netPromoterScore-rate").raty("score", $j("div[data='" + self.id + "'].class-reviews").find("span.venue-netPromoterScore").attr("data-value"));
				}
			  if ($j("div[data='" + self.id + "'].class-reviews").find("span.venue-rate").length) {
					$j("div[data='" + self.id + "'].class-reviews").find("span.venue-rate").raty("score", $j("div[data='" + self.id + "'].class-reviews").find("span.venue-rate").attr("data-value"));
				}
			  if ($j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate").length) {
					$j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate").raty("score", $j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate").attr("data-value"));
				}
			  if ($j("div[data='" + self.id + "'].class-reviews").find("span.course-rate").length) {
					$j("div[data='" + self.id + "'].class-reviews").find("span.course-rate").raty("score", $j("div[data='" + self.id + "'].class-reviews").find("span.course-rate").attr("data-value"));
				}
        this.refreshAverageRating();
    },

    //commit surfey changes
    saveSurvey: function () {
        
        var self = this;

        if ($j("div[data='" + self.id + "'].class-reviews > div > div.alert-danger").length) {
					$j("div[data='" + self.id + "'].class-reviews > div > div.alert-danger").remove();
        }
        var actionLink = "/portal/class.classdetailsnew.surveys:saveSurvey/" + self.id;
        var data = {

            "netPromoterScore": $j("div[data='" + self.id + "'].class-reviews").find("span.netPromoterScore-rate").find('input[name=score]').val(),
            "comment": $j("div[data='" + self.id + "'].class-reviews").find("textarea.survey-comment").val(),
            "courseScore": $j("div[data='" + self.id + "'].class-reviews").find("span.course-rate").find('input[name=score]').val(),
            "tutorScore": $j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate").find('input[name=score]').val(),
            "venueScore": $j("div[data='" + self.id + "'].class-reviews").find("span.venue-rate").find('input[name=score]').val()
        };

			  $j("div[data='" + self.id + "'].class-reviews").find("input[name='customField']").each(function (index, element) {
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
