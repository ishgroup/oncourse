import jQuery, * as $ from "./jquery-1.11.2";
import * as moment from "./moment.min";
import "./timepicki";
import "./StudentTimetable.js";
import { updatePercenColours } from "./StudentTimetable.js";

/**
 * Created by Artem on 28/06/2016.
 */

var $j = jQuery.noConflict();

var AttendanceCtrl = function () {
};

AttendanceCtrl.prototype = {

    attendance: null,

    btnGroup: null,
    btnCancel: null,
    btnOk: null,
    dlgConfirm: null,
    btnConfirmYes: null,
    btnConfirmNo: null,
    btnPartialReason: null,
    dlgPartialReason: null,

    txtReasonNote: null,
    txtPartialNote: null,
    datePartialFrom: null,
    datePartialTo: null,

    btnReasonSave: null,
    btnPartialSave: null,

    markState: null,
    markButtons: null,
    partialReasonDialogShows: false,

    destroy: function () {
        this.btnCancel.off();
        this.btnOk.off();
        this.btnPartialReason.off();
        this.btnConfirmYes.off();
        this.btnConfirmNo.off();
        this.btnReasonSave.off();
        this.btnPartialSave.off();
        this.dlgConfirm.hide();
        this.dlgPartialReason.addClass('collapse');

    },


    reset: function () {
        this.btnOk.removeClass('btn-success');
        this.btnOk.removeClass('active');
        this.btnOk.css("visibility", "visible");
        this.btnCancel.removeClass('active');
        this.btnCancel.removeClass('btn-danger');
        this.btnCancel.css("visibility", "visible");
        this.dlgConfirm.hide();
    },

    update: function () {
        switch (this.attendance.type) {
            case 1:
                this.makeOk();
                break;
            case 2:
                this.makeWithReason();
                break;
            case 3:
                this.makeCancel();
                break;
            case 4:
                this.makePartially();
                break;
            default:
                break;
        }
    },

    makeOk: function () {
        this.btnOk.addClass('btn-success active');
        this.btnCancel.removeClass('btn-danger active');
        this.attendance.type = 1;
    },
    makeCancel: function () {
        this.btnCancel.addClass('btn-danger active');
        this.btnOk.removeClass('btn-success active');
        this.attendance.type = 3;
    },
    makePartially: function () {
        var self = this;
        this.btnGroup.closest('div').append('<div class="partially-attended-mark btn-primary">Partially attended</div>');
        this.btnOk.css("visibility", "hidden");
        this.btnCancel.css("visibility", "hidden");
        this.btnGroup.closest('div').children('div.partially-attended-mark').on('click', function () {
            $j(this).remove();
            self.reset();
            self.attendance.type = 0;
            self.saveAttendance();
        })

    },
    makeWithReason: function () {
        var self = this;
        this.btnGroup.closest('div').append('<div class="absent-with-reason-mark btn-primary">Absent with reason</div>');
        this.btnOk.css("visibility", "hidden");
        this.btnCancel.css("visibility", "hidden");
        this.btnGroup.closest('div').children('div.absent-with-reason-mark').on('click', function () {
            $j(this).remove();
            self.reset();
            self.attendance.type = 0;
            self.saveAttendance();
        })
    },

    saveAttendance: function () {

        var self = this;
        $j.ajax({
            type: 'POST',
            url: '/portal/class.classdetailsnew:saveAttendance',
            async: false,
            cache: false,
            data: JSON.stringify(self.attendance),
            contentType: 'application/json',
            processData: false,
            success: function (data) {
                $j('div#student-' + data.studentId + '.mark-buttons').next('.percents-of-attendance').children('span').children('span').text(data.percent);
                updatePercenColours();
                var sessionLink = $j('a#' + data.sessionId);
                sessionLink.text(data.labelText);

                var parentSpan = sessionLink.parent();
                parentSpan.removeClass('edit-roll past-roll');
                parentSpan.removeClass('edit-roll actual-roll');
                parentSpan.addClass(data.labelClass);

                var sessionItem = parentSpan.parent().parent();
                sessionItem.removeClass('actual-course');
                sessionItem.removeClass('past-course');
                sessionItem.addClass(data.timeClass);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                console.log(errorThrown);
            }
        });
    },

    init: function (attendance) {
        this.attendance = attendance;
        this.attendance.sessionStart = moment(this.attendance.sessionStart).second(0).valueOf();
        this.attendance.sessionEnd = moment(this.attendance.sessionEnd).second(0).valueOf();
        this.btnGroup = $j('#student-' + this.attendance.studentId + '> div.btn-group');
        this.btnCancel = this.btnGroup.children('a[is=cancel]');
        this.btnOk = this.btnGroup.children('a[is=ok]');
        this.dlgConfirm = $j('#marking-' + this.attendance.studentId);
        this.btnConfirmYes = this.dlgConfirm.children('.absent-with-reason').children('.btn-success');
        this.btnConfirmNo = this.dlgConfirm.children('.absent-with-reason').children('.btn-danger');
        this.btnPartialReason = $j('#absent-reason-partial-' + this.attendance.studentId);
        this.dlgPartialReason = $j('#absence-' + this.attendance.studentId);
        this.txtReasonNote = $j('#absent-reason-' + this.attendance.studentId + ' textarea');
        this.txtPartialNote = $j('#absent-partial-' + this.attendance.studentId + ' textarea');
        this.markButtons = $j('#student-' + this.attendance.studentId + '.mark-buttons');

        this.txtReasonNote.val(this.attendance.note);
        this.txtPartialNote.val(this.attendance.note);

        if (this.attendance.attendedFrom === null) {
            this.attendance.attendedFrom = this.attendance.sessionStart;
        }

        if (this.attendance.attendedUntil === null) {
            this.attendance.attendedUntil = this.attendance.sessionEnd;
        }

        this.datePartialFrom = $j('#absent-partial-' + this.attendance.studentId + ' input.arrived');
        this.datePartialTo = $j('#absent-partial-' + this.attendance.studentId + ' input.departed');

        this.datePartialFrom.prop("value", moment(this.attendance.attendedFrom).format('hh : mm : a').toUpperCase());
        this.datePartialTo.prop("value", moment(this.attendance.attendedUntil).format('hh : mm : a').toUpperCase());

        this.datePartialFrom.timepicki({
            start_time: [moment(this.attendance.attendedFrom).format('hh'),
                moment(this.attendance.attendedFrom).format('mm'),
                moment(this.attendance.attendedFrom).format('a').toUpperCase()],
            increase_direction: 'up',
            step_size_minutes: 5
        });

        this.datePartialTo.timepicki({
            start_time: [moment(this.attendance.attendedUntil).format('hh'),
                moment(this.attendance.attendedUntil).format('mm'),
                moment(this.attendance.attendedUntil).format('a').toUpperCase()],
            increase_direction: 'up',
            step_size_minutes: 5
        });

        this.btnReasonSave = $j('#absent-reason-' + this.attendance.studentId + ' button');
        this.btnPartialSave = $j('#absent-partial-' + this.attendance.studentId + ' button');

        this.reset();
        this.update();

        var self = this;

        this.btnOk.on('click', function () {
            if (self.attendance.type == 0) {
                self.makeOk();
                self.saveAttendance();
            } else if (self.attendance.type != 1) {
                self.markState = true;
                self.dlgConfirm.show();
            }
        });

        this.btnCancel.on('click', function () {
            if (self.attendance.type == 0) {
                self.makeCancel();
                self.saveAttendance();
            } else if (self.attendance.type != 3) {
                self.markState = false;
                self.dlgConfirm.show();
            }
        });

        this.btnConfirmYes.on('click', function () {
            switch (self.markState) {
                case true:
                    self.makeOk();
                    self.saveAttendance();
                    break;
                case false:
                    self.makeCancel();
                    self.saveAttendance();
                    break;
                default:
                    break;
            }
            self.dlgConfirm.hide();
        });

        this.btnConfirmNo.on('click', function () {
            self.dlgConfirm.hide();
        });

        this.btnPartialReason.on('click', function () {
            if (self.partialReasonDialogShows) {
                self.dlgPartialReason.addClass('collapse');
                self.markButtons.removeClass('collapse');
                self.partialReasonDialogShows = false;
            } else {
                self.dlgPartialReason.removeClass('collapse');
                self.markButtons.addClass('collapse');
                self.partialReasonDialogShows = true;
            }
        });

        this.btnReasonSave.on('click', function () {

            if (self.txtReasonNote.val().length > 0) {
                self.attendance.type = 2;
                self.attendance.note = self.txtReasonNote.val();
                self.makeWithReason();
                self.saveAttendance();
                self.btnPartialReason.click();
            }
            else {
                alert('You must specify reason for absence.');
                return false;
            }
        });

        this.btnPartialSave.on('click', function () {

            self.attendance.attendedFrom = moment(moment(self.attendance.attendedFrom).format('MM-DD-YYYY ') + self.datePartialFrom.val(), 'MM-DD-YYYY hh : mm : a').second(0).valueOf();
            self.attendance.attendedUntil = moment(moment(self.attendance.attendedUntil).format('MM-DD-YYYY ') + self.datePartialTo.val(), 'MM-DD-YYYY hh : mm : a').second(0).valueOf();

            if (self.datePartialFrom.val().length <= 0) {
                alert('You have no ARRIVED time!');
                return false;
            }

            if (self.datePartialTo.val().length <= 0) {
                alert('You have no DEPARTED time!');
                return false;
            }

            if (self.attendance.attendedFrom < self.attendance.sessionStart) {
                alert('Arrived time must be after the session starts.');
                return false;
            } else if (self.attendance.attendedFrom > self.attendance.sessionEnd) {
                alert('Arrived time must be before the session ends.');
                return false;
            }

            if (self.attendance.attendedUntil > self.attendance.sessionEnd) {
                alert('Departed time must be before the session ends.');
                return false;
            } else if (self.attendance.attendedUntil < self.attendance.sessionStart) {
                alert('Departed time must be after the session starts.');
                return false;
            }

            if (self.attendance.attendedFrom > self.attendance.attendedUntil) {
                alert('Arrived time is greater than departed.');
                return false;
            }

            if (self.attendance.attendedFrom === self.attendance.attendedUntil) {
                alert('Duration is zero.');
                return false;
            }

            self.attendance.type = 4;

            if (self.txtPartialNote.val().length > 0) {
                self.attendance.note = self.txtPartialNote.val();
            }
            self.makePartially();
            self.saveAttendance();
            self.btnPartialReason.click();
        });
    }
};


var ClassTimetable = function () {
};

ClassTimetable.prototype = {
    id: null,
    attendanceItems: [],

    updateAttendence: function (id) {
        var self = this;
        var actionLink = "/portal/class.classdetailsnew:getAttendences/" + id;
        $j.ajax({
            url: actionLink,
            async: false,
            cache: false,
            contentType: 'application/json',
            processData: false,
            success: function (data) {
                $j.each(data, function (index, attendance) {
                    var attendanceItem = new AttendanceCtrl();
                    $j('#student-' + attendance.studentId).removeClass('collapse');
                    attendanceItem.init(attendance);
                    self.attendanceItems.push(attendanceItem)
                });
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(textStatus);
                console.log(errorThrown);
                window.location.reload();
            }
        });
    },

    scrollTo: function (element) {
        $j('html, body').animate({
            scrollTop: element.offset().top
        }, 800);
    },

    init: function () {
        var self = this;

        $j('.future-roll a').on('click', function () {
            $j('[id^=class-description-]').hide();
            var id = $j(this).attr('id');
            var classDescription = $j('#class-description-' + id);
            classDescription.show();
            self.scrollTo(classDescription);
            return false;
        });

        $j('.edit-roll a').on('click', function () {
            $j(this).parents('.past-course, .actual-course').append('<button class="btn btn-primary vertical-center btn-xs btn-finish">Close</button>').addClass('past-course-diff');
            $j('.class-roll-content .close-section').append('<button class="btn btn-primary btn-finish">Close</button>');
            $j('.edit-roll a').css('pointer-events', 'none');
            $j('.future-roll a').css('pointer-events', 'none');
            $j('#class-roll-captions, .mark-percents').hide();
            $j('[id^=class-description-]').hide();
            self.id = $j(this).attr('id');
            var classDescription = $j('#class-description-' + self.id);
            classDescription.show();

            self.updateAttendence(self.id);

            $j('button.btn-finish').on('click', function () {
                $j('button.btn-finish').parents('.past-course, .actual-course').removeClass('past-course-diff');
                $j('#class-roll-captions, .mark-percents').show();
                $j('#class-description-' + self.id).hide();
                $j('.mark-buttons').addClass('collapse');
                $j('.absent-with-reason-mark').remove();
                $j('.partially-attended-mark').remove();
                $j('.edit-roll a').css('pointer-events', 'auto');
                $j('.future-roll a').css('pointer-events', 'auto');
                $j('button.btn-finish').remove();
                $j.each(self.attendanceItems, function (index, attendanceItem) {
                    attendanceItem.destroy();
                });
            });
            self.scrollTo(classDescription);
            return false;
        });

    }
};


$j(document).ready(function () {
    if ($j('.session-wrapper.table').length) {
        new ClassTimetable().init();
        if (location.hash && $j('a' + location.hash).length) {
            $j(location.hash).click();
            location.hash = '';
        }
    }
});
