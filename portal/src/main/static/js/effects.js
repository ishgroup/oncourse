goog.provide('effects');
goog.require('jquery');
goog.require('bootstrap.checkbox');
goog.require('timepicki');

// THIS IS JS BY DARYA

var $j = jQuery.noConflict();
$j(document).ready(function() {


// diff classes for diff percents on dashboard
  function DashboardAttendance(){
    dashboardPercent = $j('.dashboard-card.persent-of-attendance .percent').text();
    dashboardAttendanceCard = $j('.dashboard-card.persent-of-attendance');

    if ( dashboardPercent < "75%" ){
      dashboardAttendanceCard.addClass('low-percent-card');
    }
    else if ( dashboardPercent < "85%" ){
      dashboardAttendanceCard.addClass('middle-percent-card');
    }
    else {
      dashboardAttendanceCard.addClass('top-percent-card');
    }
  };

  DashboardAttendance();


// for clarity how should it looks - payments modal effects
// PROGRAMMER: THIS IS EFFECTS SHOULD BE REMOVED AFTER YOU WORK (HOW I SEE TASK19455-2, ONLY SHOW!!!)
    $j('#pay-button').on('click', function(){
      setTimeout(
        function() {
          // I HAVE NO REASON CREATE NEW TEMPLATES FOR SIMPLE DEMONSTRATION
          $j('.modal-body').html('<h3 class="text-center">Succes!</h3>');
        },
        2000);
    });


  // it's change icon(up/down) when you want mark in outcome part
  $j('.marking-list').on('click', function(){
    $j(this).children('.glyphicon').toggleClass('glyphicon-chevron-up glyphicon-chevron-down');
  });
});
