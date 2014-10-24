goog.provide('surveys');

goog.require('jquery');
goog.require('jquery.raty');

$j = jQuery.noConflict();

function initAverageRating(score)
{
    $j('.rating').raty({
        half     : false,
        size     : 24,
        starOff  : '/portal/img/star-off.png',
        starOn   : '/portal/img/star-on.png',
        hints: ['', '', '', '', ''],
        readOnly : true,
        score: score,
        width	 : 90
    });
}

function initializeSurveys()
{
    $j('.rate').raty({
        half     : false,
        size     : 24,
        starOff  : '/portal/img/star-off-big.png',
        starOn   : '/portal/img/star-on-big.png',
        hints: ['', '', '', '', ''],
        width	 : 90,
        click : function()
        {
            refreshAverageRating();
        }
    });

    $j('.rate-submit').click(function()
    {
         saveSurvey();
    });

    setTimeout(function(){
        $j('.rate-class').tooltip({ content: 'Click here to provide reviews' });
    },1000);

    $j('.rate-class').click(function(){
        slideSurveys()
    });
    fillSurvey();
}

//slide survey form
function slideSurveys()
{
    $j('#class-reviews').slideToggle("fast", function()
    {
        fillSurvey();
    });
}

function refreshAverageRating()
{
    initAverageRating(Math.floor(($j(".venue-rate").raty("score") +
        $j(".tutor-rate").raty("score") +
        $j(".course-rate").raty("score"))/3));
}

//get json survey and fill controls
function fillSurvey()
{
    var actionLink = "/portal/class.classdetails.studentSurvey:getSurvey";
    $j.ajax({

        url: actionLink,
        async: false,
        cache: false,
        success: function (data) {
            $j(".venue-rate").raty("score", data.venueScore);
            $j(".tutor-rate").raty("score", data.tutorScore);
            $j(".course-rate").raty("score", data.courseScore);
            $j(".survey-comment").val(data.comment);
            refreshAverageRating();
        },
        error: function (jqXHR, textStatus,errorThrown) {
            window.location.reload();
        }
    });
}

//commit surfey changes
function saveSurvey()
{
    var actionLink = "/portal/class.classdetails.studentSurvey:saveSurvey";
    var data = {
        "comment":$j(".survey-comment").val(),
        "courseScore":$j(".course-rate").find('input[name=score]').val(),
        "tutorScore":$j(".tutor-rate").find('input[name=score]').val(),
        "venueScore":$j(".venue-rate").find('input[name=score]').val()
    };
    $j.ajax({
        url: actionLink,
        async: false,
        cache: false,
        data: data,
        success: function (data) {
            slideSurveys();
        },
        error: function (jqXHR, textStatus,errorThrown) {
            window.location.reload();
        }
    });

}




$j('document').ready(function(){
    initializeSurveys();
});














