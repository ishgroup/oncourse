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
        var self = this;
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
            $j("div[data='" + self.id + "'].class-reviews").parent().find("span.rate-class").tooltip({ content: 'Click here to see reviews' });
        }
        else {
            $j("div[data='" + self.id + "'].class-reviews").parent().find("span.rate-class").tooltip({ content: 'Click here to provide reviews' });
        }

    },
    //slide survey form
    slideSurveys: function () {
        var self = this;
        $j("div[data='" + self.id + "'].class-reviews").slideToggle("fast", function () {
            self.loadSurvey();
            self.fillSurvey();
        });
    },

    refreshAverageRating: function () {
        var self = this;
        this.initAverageRating(Math.floor(
           ($j("div[data='" + self.id + "'].class-reviews").find("span.venue-rate").raty("score") +
            $j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate").raty("score") +
            $j("div[data='" + self.id + "'].class-reviews").find("span.course-rate").raty("score")) / 3));
    },
    
    fillSurvey: function () {
        var self = this;
        $j("div[data='" + self.id + "'].class-reviews").find("span.venue-rate").raty("score", this.survey.venueScore);
        $j("div[data='" + self.id + "'].class-reviews").find("span.tutor-rate").raty("score", this.survey.tutorScore);
        $j("div[data='" + self.id + "'].class-reviews").find("span.course-rate").raty("score", this.survey.courseScore);
        $j("div[data='" + self.id + "'].class-reviews").find("textarea.survey-comment").val(this.survey.comment);
        this.refreshAverageRating();
    },

    //commit surfey changes
    saveSurvey: function () {
        var self = this;
        var actionLink = "/portal/class.classdetails.surveys:saveSurvey/" + self.id;
        var data = {            
            
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
                window.location.reload();
            }
        });
    },

    loadSurvey: function () {
        var self = this;
        //get json survey and fill controls
        var actionLink = "/portal/class.classdetails.surveys:getSurvey/" + self.id;
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
    if ($j('.class-reviews').length != 0) {
        $j('.class-reviews').each(function (index, element) {
            new Survey($j(element).attr("data")).initializeSurveys();
        });
    }
});
