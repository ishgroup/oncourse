goog.provide('attendances');

goog.require('initialise');


var $j = jQuery.noConflict();

function init() {
    $j(".session-list > li .mark-roll").click(function (event) {
        var actionLink = "/portal/class.classdetails:onsetsession/" + event.target.id;
        $j.ajax({

            url: actionLink,
            async: false,
            cache: false,
            success: function (data) {
                show()
            },
            error: function (jqXHR, textStatus,errorThrown) {
                window.location.reload();
            }
        });
    });

    $j('.finish').click(function () {
        hide();
    });
}


function fillAttendences(session) {

    //fill session start date
    $j('#sessionDate').html(session.startDate);

    //update attendances buttons
    $j.each(session.attendances, function (index, attendance) {
        var control = $j('#' + attendance.studentId);

        control.attr({"value": attendance.type});

        var buttonOk = control.children('.attendance').children('.btn-ok');
        var buttonCancel =control.children('.attendance').children('.btn-cancel');

        //reset attendance buttons
        buttonOk.removeClass('btn-primary active');
        buttonCancel.removeClass('btn-danger active');
        buttonOk.addClass('btn-default');
        buttonOk.addClass('btn-default');

        if (attendance.type == 1) {
            buttonOk.removeClass('btn-default');
            buttonOk.addClass('btn-primary active');
        }
        else if (attendance.type > 1) {
            buttonCancel.removeClass('btn-default');
            buttonCancel.addClass('btn-danger active');
        }
    });
}


function hide() {


    data = new Object;
    $j('.attendance-button-field').each(function (index, element) {
        var id = element.id;

        if ($j(element).children('.attendance').children('.btn-ok').hasClass('btn-primary active')) {

            data[id] = 1
        }
        else if ($j(element).children('.attendance').children('.btn-cancel').hasClass('btn-danger active')) {

            if ($j(element).attr("value") == 1 || $j(element).attr("value") == 0) {
                data[id] = 3
            } else {
                data[id] = $j(element).attr("value")
            }

        }
        else {
            data[id] = 0
        }


    });


    $j(".session-wrapper, .course-detail").slideDown();
    $j('.class-roll-content .info-text').hide();
    $j(".session-list > li").removeClass('active');
    $j(".session-info").show();
    //$j(".past-sessions").show();
    $j('.attendance-content').hide('slow');

    var el = $j('.attendance');
    el.removeClass('act');


    $j.ajax({
        type: "POST",
        url: '/portal/class.classdetails:setAttendences',
        data: data,
        dataType: "json",
        async: false,
        cache: false,
        error: function (jqXHR, textStatus,errorThrown) {
            window.location.reload();
        }
    });

    return false;
}


function show() {
    $j(".session-wrapper, .course-detail").slideUp();
    $j(".class-roll-content .info-text").show();
    //$j(".past-sessions").hide();
    $j(".session-info").hide();
    $j('.attendance-content').fadeIn('slow');

    var el = $j('.attendance');
    el.addClass('act');

    $j.ajax({
        cache: false,
        async: false,
        dataType: "json",
        url: '/portal/class.classdetails:getAttendences',
        type: 'GET',
        success: function (data) {
            fillAttendences(data);
        },
        error: function (jqXHR, textStatus,errorThrown) {
            window.location.reload();
        }

    });

    return false;
}


$j(document).ready(function () {
    init();
});
