$j(document).ready(function() {

// I have some trouble with object/svg
// I want to add classes to svg inside objects to change styles
// If you find the best way, just do it

  function SetClassToSvg(objects, classname){
    [].forEach.call(objects, function(item){
      item.contentDocument;
      $('svg', item).attr("class", classname);
    });
  };

  SetClassToSvg(jQuery('li.active object'), 'active');
  SetClassToSvg(jQuery('.past-sessions .past-course object'), 'past-session');

  jQuery('.percents-of-attendance span').each( function(){
    parent = $(this).parent();
    percent = $(this).text();

    if (percent>90){
      parent.css('color', '#8FB830');
    }
    else if (percent>50) {
      parent.css('color', '#c8706b');
    }
    else{
      parent.css('color', '#991111');
    };
  });

  jQuery('.edit-roll a').on('click', function(){
    jQuery(this).parents('.past-course, .actual-course').append('<button class="btn btn-primary vertical-center btn-xs btn-finish">Finish</button>').addClass('past-course-diff');
    jQuery('.edit-roll a').css( 'pointer-events', 'none' );
    jQuery('#class-roll-captions, .mark-percents').hide();
    jQuery('#class-description, .mark-checkbox-div').show();

    if (jQuery(this).closest('.past-course').length){jQuery('.class-desc-section').addClass('past-roll-desc').removeClass('actual-roll-desc'); };
    if (jQuery(this).closest('.actual-course').length){jQuery('.class-desc-section').addClass('actual-roll-desc').removeClass('past-roll-desc'); };



    jQuery('button.btn-finish').on('click', function(){
      jQuery(this).parents('.past-course, .actual-course').removeClass('past-course-diff');
      jQuery(this).remove();
      jQuery('.edit-roll a').css( 'pointer-events', 'auto' );
      jQuery('#class-roll-captions, .mark-percents').show();
      jQuery('#class-description, .mark-checkbox-div').hide();
    });
    });

    jQuery('.mark-checkbox').checkboxpicker({
    html: true,
    offLabel: '<span class="glyphicon glyphicon-remove">',
    onLabel: '<span class="glyphicon glyphicon-ok">'
    });

// for clarity how should it looks - payments modal effects
    jQuery('#pay-button').on('click', function(){
      setTimeout(
        function() {
          jQuery('.modal-body').html('<h3 class="text-center">Succes!</h3>');
        },
        2000);
    });

// timepicker
  jQuery(document).ready(function(){
    jQuery(".time_element").timepicki();
  });

});
