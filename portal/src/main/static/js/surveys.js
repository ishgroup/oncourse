goog.provide('surveys');

goog.require('jquery');
goog.require('jquery.raty');

var $j = jQuery.noConflict();

var document = window.document,


    Survey = function () {
    };

Survey.prototype = {
    survey: 0,
    initAverageRating: function (score) {
        $j('.rating').raty({
            half: false,
            size: 24,
            starOff: '/portal/img/star-off.png',
            starOn: '/portal/img/star-on.png',
            hints: ['', '', '', '', ''],
            readOnly: true,
            score: score,
            width: 90
        });
    },

    initRate: function (rateHtml, score, readOnly) {
        rateHtml.raty({
            half: false,
            size: 24,
            starOff: '/portal/img/star-off-big.png',
            starOn: '/portal/img/star-on-big.png',
            hints: ['', '', '', '', ''],
            width: 90,
            readOnly: readOnly,
            score: score,
            click: function () {
                self.refreshAverageRating();
            }
        });
    },

    initializeSurveys: function () {
        this.loadSurvey();
        var self = this;

        this.initRate($j(".venue-rate"), this.survey.venueScore, this.survey.readOnly);
        this.initRate($j(".tutor-rate"), this.survey.tutorScore, this.survey.readOnly);
        this.initRate($j(".course-rate"), this.survey.courseScore, this.survey.readOnly);

        $j('.rate-submit').click(function () {
            self.saveSurvey();
        });

        setTimeout(function () {
            if (self.survey.readOnly) {
                $j('.rate-class').tooltip({ content: 'Click here to see reviews' });
            }
            else {
                $j('.rate-class').tooltip({ content: 'Click here to provide reviews' });
            }
        }, 1000);

        $j('.rate-class').click(function () {
            self.slideSurveys()
        });
        this.fillSurvey();
    },
    //slide survey form
    slideSurveys: function () {
        var self = this;
        $j('#class-reviews').slideToggle("fast", function () {
            self.loadSurvey();
            self.fillSurvey();
        });
    },

    refreshAverageRating: function () {
        this.initAverageRating(Math.floor(($j(".venue-rate").raty("score") +
            $j(".tutor-rate").raty("score") +
            $j(".course-rate").raty("score")) / 3));
    },
    fillSurvey: function () {
        $j(".venue-rate").raty("score", this.survey.venueScore);
        $j(".tutor-rate").raty("score", this.survey.tutorScore);
        $j(".course-rate").raty("score", this.survey.courseScore);
        $j(".survey-comment").val(this.survey.comment);
        this.refreshAverageRating();
    },

    //commit surfey changes
    saveSurvey: function () {
        var self = this;
        var actionLink = "/portal/class.classdetails.surveys:saveSurvey";
        var data = {
            "comment": $j(".survey-comment").val(),
            "courseScore": $j(".course-rate").find('input[name=score]').val(),
            "tutorScore": $j(".tutor-rate").find('input[name=score]').val(),
            "venueScore": $j(".venue-rate").find('input[name=score]').val()
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
                window.location.reload();
            }
        });
    },

    loadSurvey: function () {
        var self = this;
        //get json survey and fill controls
        var actionLink = "/portal/class.classdetails.surveys:getSurvey";
        $j.ajax({

            url: actionLink,
            async: false,
            cache: false,
            success: function (data) {
                self.survey = data;
            },
            error: function (jqXHR, textStatus, errorThrown) {
                window.location.reload();
            }
        });
    }
};


$j('document').ready(function () {
    new Survey().initializeSurveys();
});
