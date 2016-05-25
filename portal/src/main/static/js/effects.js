goog.provide('effects');
goog.require('jquery');
goog.require('bootstrap.checkbox');
goog.require('timepicki');

// THIS IS JS BY DARYA


var $j = jQuery.noConflict();
$j(document).ready(function() {

// I have some trouble with object/svg
// I want to add classes to svg inside objects to change styles
// If you find the best way, just do it

// for adding class to svg inside object
  function SetClassToSvg(objects, classname){
    [].forEach.call(objects, function(item){
      item.contentDocument;
      $j('svg', item).attr("class", classname);
    });
  };

  SetClassToSvg($j('li.active object'), 'active');
  SetClassToSvg($j('.past-sessions .past-course object'), 'past-session');

// colors of different percents attendance
  $j('.percents-of-attendance span').each( function(){
    parent = $j(this).parent();
    percent = $j(this).text();

    if (percent>90){
      parent.addClass('full-percents');
    }
    else if (percent>50) {
      parent.addClass('half-percents');
    }
    else{
      parent.addClass('mimimum-percents');
    };
  });

// when you edit roll in Timetable it's happen
// PROGRAMMER: IT DOES NOT INCLUDE APP LOGIC, ONLY JS EFFECTS TO SHOW SOME DETAILS; REMOVE THAT AFTER YOU WORK
  $j('.edit-roll a').on('click', function(){
    $j(this).parents('.past-course, .actual-course').append('<button class="btn btn-primary vertical-center btn-xs btn-finish">Finish</button>').addClass('past-course-diff');
    $j('.edit-roll a').css( 'pointer-events', 'none' );
    $j('#class-roll-captions, .mark-percents').hide();
    $j('#class-description, .mark-checkbox-div').show();

    if ($j(this).closest('.past-course').length){$j('.class-desc-section').addClass('past-roll-desc').removeClass('actual-roll-desc'); };
    if ($j(this).closest('.actual-course').length){$j('.class-desc-section').addClass('actual-roll-desc').removeClass('past-roll-desc'); };
    $j('.add-rem-collapse').attr("data-toggle", "collapse");

    $j('button.btn-finish').on('click', function(){
      $j(this).parents('.past-course, .actual-course').removeClass('past-course-diff');
      $j(this).remove();
      $j('.edit-roll a').css( 'pointer-events', 'auto' );
      $j('#class-roll-captions, .mark-percents').show();
      $j('#class-description, .mark-checkbox-div').hide();
      $j('.add-rem-collapse').attr("data-toggle", "");
    });
  });

// checkboxer
// YOU CAN SEE THAT IN BOOTSRAP-CHECKBOXER-DOCS
  $j('.mark-checkbox').checkboxpicker({
    html: true,
    offLabel: '<span class="glyphicon glyphicon-remove">',
    onLabel: '<span class="glyphicon glyphicon-ok">'
  });

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

// timepicker
// I THINK YOU HAVE NOW QUESTION ABOUT IT
  $j(".time_element").timepicki();

// save absent reason when you mark students
// SOMETHING LIKE THAT; IT IS NOT WORKING CODE
// PROGRAMMER: MOVE THIS CODE INSIDE ENOTHER FILE, IF YOU WANT
  $j('#absent-reason button').on('click', function(){
    if ( $j('#absent-reason textarea').val().length > 0 ){
      //save reason
    }
    else {
      // FOR EXAMPLE, I HAVE NO TEMPLATES
      alert('You have no reason!');
      return false;
    }
  });

  // it's change icon(up/down) when you want mark in outcome part
  $j('.marking-list').on('click', function(){
    $j(this).children('.glyphicon').toggleClass('glyphicon-chevron-up glyphicon-chevron-down')
  });
});
