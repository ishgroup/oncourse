goog.provide('ClassTimetable');

goog.require('jquery');
goog.require('moment');
goog.require('timepicki');

/**
 * Created by Artem on 28/06/2016.
 */

var $j = jQuery.noConflict();

AttendanceCtrl = function() {
}

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
    datePartialFrom:null,
    datePartialTo:null,

    btnReasonSave: null,
    btnPartialSave: null,

    markState: null,
    partialReasonDialogShows: false,

    destroy:  function() {
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


    reset: function() {
        this.btnOk.removeClass('btn-success');
        this.btnOk.removeClass('active');
        this.btnCancel.removeClass('active');
        this.btnCancel.removeClass('btn-danger');
        this.dlgConfirm.hide();
    },

    update: function() {
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
    
    makeOk: function() {
        this.btnOk.addClass('btn-success active');
        this.btnCancel.removeClass('btn-danger active');
        this.attendance.type = 1;
    },
    makeCancel: function() {
        this.btnCancel.addClass('btn-danger active');
        this.btnOk.removeClass('btn-success active');
        this.attendance.type = 3;
    },
    makePartially: function() {
        var self = this;
        this.btnGroup.closest('div').append('<div class="partially-attended-mark btn-primary">Partially attended</div>');
        this.btnGroup.closest('div').children('div.partially-attended-mark').on('click', function() {
            $j(this).remove();
            self.reset();
            self.attendance.type = 0;
        })

    },
    makeWithReason: function() {
        var self = this;
        this.btnGroup.closest('div').append('<div class="absent-with-reason-mark btn-primary">Absent with reason</div>');
        this.btnGroup.closest('div').children('div.absent-with-reason-mark').on('click', function() {
            $j(this).remove();
            self.reset();
            self.attendance.type = 0;
        }) 
    },
    
    init: function(attendance) {
        this.attendance = attendance;
        this.btnGroup = $j('#' + this.attendance.studentId + '> div.btn-group');
        this.btnCancel = this.btnGroup.children('a[is=cancel]');
        this.btnOk = this.btnGroup.children('a[is=ok]');
        this.dlgConfirm = $j('#marking-' + this.attendance.studentId);
        this.btnConfirmYes = this.dlgConfirm.children('.absent-with-reason').children('.btn-success');
        this.btnConfirmNo = this.dlgConfirm.children('.absent-with-reason').children('.btn-danger');
        this.btnPartialReason = $j('#absent-reason-partial-' + this.attendance.studentId)
        this.dlgPartialReason = $j('#absence-' + this.attendance.studentId);
        this.txtReasonNote = $j('#absent-reason-' + this.attendance.studentId + ' textarea');
        this.txtPartialNote = $j('#absent-partial-' + this.attendance.studentId + ' textarea');

        this.txtReasonNote.val(this.attendance.note);
        this.txtPartialNote.val(this.attendance.note);

        this.datePartialFrom = $j('#absent-partial-' + this.attendance.studentId + ' input.arrived');
        this.datePartialTo = $j('#absent-partial-' + this.attendance.studentId + ' input.departed');
        
        this.datePartialFrom.prop( "value", moment(this.attendance.startDate).format('hh:mm:a').toUpperCase());
        this.datePartialFrom.prop('disabled', 'true');
        
        this.datePartialTo.timepicki({
                        start_time: [moment(this.attendance.endDate).format('hh'), moment(this.attendance.endDate).format('mm'), moment(this.attendance.endDate).format('a').toUpperCase()],
                        increase_direction: 'up',
                        step_size_minutes: 5
                   });

        if (this.attendance.durationMinutes) {
            this.datePartialTo.prop("value",moment(this.attendance.startDate).add(this.attendance.durationMinutes, 'minutes').format('hh:mm:a').toUpperCase());
        } 
        
        this.btnReasonSave = $j('#absent-reason-' + this.attendance.studentId + ' button');
        this.btnPartialSave =  $j('#absent-partial-' + this.attendance.studentId + ' button');
        
        this.reset();
        this.update();

        var self = this;
        
        this.btnOk.on('click', function() {
            if (self.attendance.type == 0) {
                self.makeOk()
            } else if (self.attendance.type != 1) {
                self.markState = true;
                self.dlgConfirm.show();
            }    
        });

        this.btnCancel.on('click', function() {
            if (self.attendance.type == 0) {
                self.makeCancel()
            } else if (self.attendance.type != 3) {
                self.markState = false;
                self.dlgConfirm.show();
            }
        });

        this.btnConfirmYes.on('click', function() {
            switch (self.markState) {
                case true:
                    self.makeOk();
                    break;
                case false:
                    self.makeCancel();
                    break;
                default:
                    break;
            }
            self.dlgConfirm.hide();
        });

        this.btnConfirmNo.on('click', function() {
            self.dlgConfirm.hide();
        });

        this.btnPartialReason.on('click', function() {
            if (self.partialReasonDialogShows) {
                self.dlgPartialReason.addClass('collapse');
                self.partialReasonDialogShows = false;
            } else {
                self.dlgPartialReason.removeClass('collapse');
                self.partialReasonDialogShows = true;
            }
        });

        this.btnReasonSave.on('click', function(){
            
            if (self.txtReasonNote.val().length > 0) {
                self.attendance.type = 2;
                self.attendance.note = self.txtReasonNote.val();
                self.makeWithReason();
                self.btnPartialReason.click();
            }
            else {
                alert('You must specify reason for absence.');
                return false;
            }
        });

        this.btnPartialSave.on('click', function() {

            if (self.datePartialTo.val().length <= 0) {
                alert('You have no DEPARTED time!');
                return false;
            }
            
            var departed = moment(moment(self.attendance.endDate).format('MM-DD-YYYY ') + self.datePartialTo.val(), 'MM-DD-YYYY hh : mm : a');
            
            if (departed > self.attendance.endDate) {
                alert('Departed time must be before the session ends.');
                return false;
            } else if (departed < self.attendance.startDate) {
                alert('Departed time must be after the session starts.');
                return false;
            }
            
            self.attendance.type = 4;
            self.attendance.durationMinutes = moment.duration(departed.diff(self.attendance.startDate)).asMinutes();

            if (self.txtPartialNote.val().length > 0) {
                self.attendance.note = self.txtPartialNote.val();
            }
            self.makePartially();
            self.btnPartialReason.click();
        });
    }
}


ClassTimetable = function () {
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
                $j.each(data, function(index, attendance) {
                    var attendanceItem =  new AttendanceCtrl()
                    attendanceItem.init(attendance);
                    self.attendanceItems.push(attendanceItem)

                });
            },
            error: function (jqXHR, textStatus, errorThrown) {
                window.location.reload();
            }
        });
    },

    init: function () {
        var self = this;
        
        $j('.future-roll a').on('click', function() {
           $j('[id^=class-description-]').hide();
           var id = $j(this).attr('id');
           $j('#class-description-' +  id).show()
        });
        
        $j('.edit-roll a').on('click', function () {
            $j(this).parents('.past-course, .actual-course').append('<button class="btn btn-primary vertical-center btn-xs btn-finish">Finish</button>').addClass('past-course-diff');
            $j('.edit-roll a').css('pointer-events', 'none');
            $j('.future-roll a').css('pointer-events', 'none');
            $j('#class-roll-captions, .mark-percents').hide();
            $j('[id^=class-description-]').hide();
            self.id = $j(this).attr('id');
            $j('#class-description-' +  self.id).show();
            $j('.mark-buttons').removeClass('collapse');


            self.updateAttendence(self.id);

            $j('button.btn-finish').on('click', function () {
                $j(this).parents('.past-course, .actual-course').removeClass('past-course-diff');
                $j('#class-roll-captions, .mark-percents').show();
                $j('#class-description-' +  self.id).hide();
                $j('.mark-buttons').addClass('collapse');
                $j('.absent-with-reason-mark').remove();
                $j('.partially-attended-mark').remove();
                $j('.edit-roll a').css('pointer-events', 'auto');
                $j('.future-roll a').css('pointer-events', 'auto');
                $j('button.btn-finish').remove();
                var response = [];
                $j.each(self.attendanceItems, function(index, attendanceItem) {
                    response.push(attendanceItem.attendance);
                    attendanceItem.destroy();
                });

                $j.ajax({
                    type: 'POST',
                    url: '/portal/class.classdetailsnew:setAttendences',
                    async: false,
                    cache: false,
                    data: JSON.stringify(response),
                    contentType: 'application/json',
                    processData: false,
                    success: function () {
                        window.location.reload();
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        console.log(jqXHR);
                        console.log(errorThrown);
                    }
                });
                
                
            });
        });
        
    }
};


$j(document).ready(function () {
    if ($j('.session-wrapper table').length) {
        new ClassTimetable().init();
        if (location.hash && $j('a' + location.hash).length) {
            $j(location.hash).click();
            location.hash = '';
        }
    }
});
