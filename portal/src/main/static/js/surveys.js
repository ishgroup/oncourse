goog.provide('surveys');

goog.require('jquery');
goog.require('jquery.raty');
goog.require('initialise');

var $j = jQuery.noConflict();

   function Survey(id) {
        this.id=id;
    }

Survey.prototype = {

    survey: 0,
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
                var averageRate = Math.floor(
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
        this.loadSurvey();
        var self = this;

        this.initNetPromoterScoreRate($j("div[data='" + self.id + "'].class-reviews").find("span.netPromoterScore-rate"), this.survey.netPromoterScore, this.survey.readOnly);
        this.initRate($j("div[data='" + self.id + "'].class-reviews").find("span.venue-rate"), this.survey.venueScore, this.survey.readOnly);
        this.initRate($j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate"), this.survey.tutorScore, this.survey.readOnly);
        this.initRate($j("div[data='" + self.id + "'].class-reviews").find("span.course-rate"), this.survey.courseScore, this.survey.readOnly);

        $j("div[data='" + self.id + "'].class-reviews").find("button.rate-submit").click(function () {
            self.saveSurvey();
        });

        $j("div[data='" + self.id + "'].class-reviews").parent().find("span.rate-class").click(function () {
            self.slideSurveys()
        });
        this.fillSurvey();

        if (self.survey.readOnly) {
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
        this.refreshNetPromoterScore(this.survey.netPromoterScore)
    },
    //slide survey form
    slideSurveys: function () {
        var self = this;
        $j("div[data='" + self.id + "'].class-reviews").slideToggle("fast", function () {
            // self.loadSurvey();
            // self.fillSurvey();
        });
    },

    refreshAverageRating: function () {
        var self = this;
        this.initAverageRating(Math.floor(
           ($j("div[data='" + self.id + "'].class-reviews").find("span.venue-rate").raty("score") +
            $j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate").raty("score") +
            $j("div[data='" + self.id + "'].class-reviews").find("span.course-rate").raty("score")) / 3));
    },

    refreshNetPromoterScore: function(score) {
      var self = this;
        //var score = $j("div[data='" + self.id + "'].class-reviews").find("span.netPromoterScore-rate").raty('score');
        var placeholderMessage = '';
        if (score >= 1 && score <= 6) {
            placeholderMessage = 'Please tell us how we could have improved your experience.';
        } else {
            if (score >= 7 && score <= 10) {
                placeholderMessage = 'What did you most enjoy about your experience.';
            }
        }
        $j("div[data='" + self.id + "'].class-reviews").find("textarea.survey-comment").attr('placeholder', placeholderMessage);
    },

    fillSurvey: function () {
        var self = this;
        $j("div[data='" + self.id + "'].class-reviews").find("span.netPromoterScore-rate").raty("score", this.survey.netPromoterScore);
        $j("div[data='" + self.id + "'].class-reviews").find("span.venue-rate").raty("score", this.survey.venueScore);
        $j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate").raty("score", this.survey.tutorScore);
        $j("div[data='" + self.id + "'].class-reviews").find("span.course-rate").raty("score", this.survey.courseScore);
        $j("div[data='" + self.id + "'].class-reviews").find("textarea.survey-comment").val(this.survey.comment);
        this.refreshAverageRating();
    },

    //commit surfey changes
    saveSurvey: function () {
        var self = this;
        var actionLink = "/portal/class.classdetailsnew.surveys:saveSurvey/" + self.id;
        var data = {

            "netPromoterScore": $j("div[data='" + self.id + "'].class-reviews").find("span.netPromoterScore-rate").find('input[name=score]').val(),
            "comment": $j("div[data='" + self.id + "'].class-reviews").find("textarea.survey-comment").val(),
            "courseScore": $j("div[data='" + self.id + "'].class-reviews").find("span.course-rate").find('input[name=score]').val(),
            "tutorScore": $j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate").find('input[name=score]').val(),
            "venueScore": $j("div[data='" + self.id + "'].class-reviews").find("span.venue-rate").find('input[name=score]').val()
        };
        $j.ajax({
            url: actionLink,
            async: false,
            cache: false,
            data: data,
            success: function (data) {
                self.slideSurveys();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                // window.location.reload();
                console.log("Unexpected exception");
            }
        });
    },

    loadSurvey: function () {
        var self = this;
        //get json survey and fill controls
        var actionLink = "/portal/class.classdetailsnew.surveys:getSurvey/" + self.id;
        $j.ajax({

            url: actionLink,
            async: false,
            cache: false,
            success: function (data) {
                self.survey = data;
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
