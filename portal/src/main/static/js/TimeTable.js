import jQuery, * as $ from "./jquery-1.11.2";
import "./jquery.calendario";
import "./initialise";

var $j = jQuery.noConflict();
var timetable;

$j(document).ready(function () {


    if ($j('#mytimetablepage').length) {

        timetable = new MyTimetable();
        timetable.init($j('#team-timetable').length > 0);


        $j('#team-timetable').on('click', function(){
            timetable.destroy();
            $j('#team-timetable').remove();
            $j('h1.timetable-header > a').text('Timetable');
            $j('div.main-container > div.row > div.col-lg-3.col-md-5.col-sm-12').append("<div class='page-header'><h4>Your Classes</h4></div><div class='your-classes-list'></div>");

            timetable =  new MyTimetable();
            timetable.init(false);
        });
    }
});

var MyTimetable = function() {
}

MyTimetable.prototype = {
    month: null,
    year: null,
    cal: null,
    allClasses: null,
    showTeam: null,
    sessionsOffset: null,
    sessionsPageSize: null,


    init: function(showTeam) {
        this.showTeam = showTeam;
        this.allClasses = [];
        $j('#wrap').addClass('timetable');

        $j('div.sync-to-device').on('click', function () {
            window.location.href =  $j(this).attr('value');
        });

        $j('#calendar').empty();

         this.cal = $j('#calendar').calendario({
                checkUpdate: false,
                weekabbrs: [ 'S', 'M', 'T', 'W', 'T', 'F', 'S' ],
                displayWeekAbbr: true,
                startIn: 0
            }),
          this.month = $j('#custom-month').html(this.cal.getMonthName()),
          this.year = $j('#custom-year').html(this.cal.getYear());

        this.updateMonthYear();

        var self = this;

        $j('#custom-next').on('click', function () {
            self.cal.gotoNextMonth();
            self.updateMonthYear();
        });
        $j('#custom-prev').on('click', function () {
            self.cal.gotoPreviousMonth();
            self.updateMonthYear();
        });
    },

    updateMonthYear: function() {
        this.month.html(this.cal.getMonthName() + " ");
        this.year.html(this.cal.getYear());
        this.getDataForMonth();
        this.refreshColors($j("div.fc-calendar-event > span"), true);
        if (!this.showTeam) {
            this.renderClasses();
        }
        this.renderSessions();
    },

    getDataForMonth: function() {
        var self = this;
        $j.ajax({
            cache: false,
            async: false,
            dataType: "json",
            url: '/portal/timetable:getcalendarevents/' + this.showTeam + '/' + this.cal.getMonthName() +'-'+ this.cal.getYear(),
            type: 'GET',
            success: function (data) {
                self.cal.setData(data);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                window.location.reload();
                console.log("Unexpected exception");
            }
        });
    },

    renderClasses: function() {
        var refreshId = '.your-classes-list';
        $j(refreshId).empty();
        var self = this;
        $j.ajax({
            type: "GET",
            url: '/portal/timetable:getyourclasses/' + this.cal.getMonthName() +'-'+ this.cal.getYear(),
            async: false,
            //if the parameter is not set internet explorer loads content from cache
            cache: false,
            success: function(data) {
                if (data.content)
                {
                    $j(refreshId).html(data.content);
                    self.refreshColors($j(refreshId + ' .marker'), false);
                }
            }
        });
    },

    renderSessions: function() {
        $j('#accordion').empty();
        this.sessionsOffset = 0;
        this.sessionsPageSize = 10;

        this.nextPage();


        $j(window).scroll(function() {
            if(($j('#accordion').innerHeight() < ($j(window).scrollTop() + $j(window).height() - $j('#header-holder').height())) && $j('#showMoreCourses').length ) {
                $j("#showMoreCourses").replaceWith("<div class='loading text-center'><img src='/portal/img/loading.gif'/></div>");
                timetable.nextPage();
            }
        });
    },

    nextPage: function() {
        var refreshId = '#accordion';
        var self = this;
        $j.ajax({
            type: "GET",
            url: '/portal/timetable:getSessions/' + this.showTeam + '/' + this.cal.getMonthName() +'-'+ this.cal.getYear() + '/' + this.sessionsPageSize + '/' + this.sessionsOffset ,
            async: true,
            //if the parameter is not set internet explorer loads content from cache
            cache: false,
            success: function(data) {
                if (data.content) {
                    $j(refreshId).append(data.content);
                    self.splitMonthsDays();
                    if (self.showTeam) {

                        if ($j(data.content).find('.class-card').length == 10) {
                            $j(refreshId).append("<a id='showMoreCourses' style='display:none;'>");
                            self.sessionsOffset += self.sessionsPageSize;
                        }
                    }

                    $j('.point-marker').off();
                    self.initMarkerHandler();
                    self.refreshColors($j(refreshId + ' .class-card'), false);
                }
                $j("div.loading").remove();
            }
        });
    },

    splitMonthsDays: function() {

        var monthHeadCode;
        $j('.month-name').each(function() {
            if (monthHeadCode == null) {
                monthHeadCode = $j(this).attr('data-year-month');
            } else if (monthHeadCode == $j(this).attr('data-year-month')) {
                $j(this).remove();
            } else {
                monthHeadCode = $j(this).attr('data-year-month');
            }
        });

        var dayHeadCode;
        $j('.date-of-classes').each(function() {
            if (dayHeadCode == null) {
                dayHeadCode = $j(this).attr('data-month-day');
            } else if (dayHeadCode == $j(this).attr('data-month-day')) {
                $j(this).css('border-top', 'white');
                $j(this).children('.day-of-class').css('color', '#cccaca');
                $j(this).children('.week-day-of-class').css('color', '#cccaca');

            } else {
                dayHeadCode = $j(this).attr('data-month-day');
            }
        });

    },

    refreshColors: function(elements, updateParent) {
        var self = this;
        elements.each(function() {
            var classId = $j(this).attr('value');
            var colourIndex;

            if (self.allClasses.indexOf(classId) < 0) {
                self.allClasses.push(classId);
            }
            colourIndex = self.allClasses.indexOf(classId)%10;
            if (updateParent) {
                $j(this).parent().addClass('class-colour-' + colourIndex);
            } else {
                $j(this).addClass('class-colour-' + colourIndex);
            }
        });
    },


    initMarkerHandler: function() {
        $j('.point-marker').on('click', function() {
            var sessionId = $j(this).attr('value');
            var state = $j(this).next('.class-description').attr('aria-expanded');
            var cell = $j('span#sessionId-' + sessionId).parent();

            if (state == "false") {
                cell.css('background-color', cell.css('border-color'));
                cell.parent().siblings('span.fc-date').addClass('white-text');
            } else {
                cell.css('background-color', '');
                cell.parent().siblings('span.fc-date').removeClass('white-text')
            }
        });
    },

    destroy: function() {
        $j('div.sync-to-device').off();
        $j('#custom-next').off();
        $j('#custom-prev').off();
    }
}





