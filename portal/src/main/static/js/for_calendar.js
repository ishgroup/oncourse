goog.provide('for_calendar');

goog.require('initialise');


var $j = jQuery.noConflict();

var codropsEvents;
var success;
$j(document).ready(function() {
if($j('.calendar-container').length){
$j.ajax({
  cache: false,
  async: false,
  dataType: "json",
  url: '/portal/timetable.calendar:getcalendarevents',
  type: 'GET',
  success: function(data)
    {
     codropsEvents = data;
    },
    error: function (jqXHR, textStatus,errorThrown) {
        window.location.reload();
    }
});
}


$j(function() {
	if($j('.calendar-container').length){
        var cal = $j( '#calendar' ).calendario( {
        caldata : codropsEvents,

        onDayClick: function( date, allDay, jsEvent, view ) {
        var myDate = new Date();

        for ( var i = 1; i < myDate.getDate(); i++ ) {
        //console.log($(".fc-row .fc-date:nth-child("+i+")"));
        }
        }

        }),
        $month = $j( '#custom-month' ).html( cal.getMonthName() ),
        $year = $j( '#custom-year' ).html( cal.getYear() );

        $j( '#custom-next' ).on( 'click', function() {
        cal.gotoNextMonth( updateMonthYear );
        } );
        $j( '#custom-prev' ).on( 'click', function() {
        cal.gotoPreviousMonth( updateMonthYear );
        } );
        $j( '#custom-current' ).on( 'click', function() {
        cal.gotoNow( updateMonthYear );
        } );

        function updateMonthYear() {
        $month.html( cal.getMonthName() );
        $year.html( cal.getYear() );
        }
	}
});


});

