import jQuery, * as $ from "./jquery-1.11.2";

var $j = jQuery.noConflict();

$j(document).ready(function () {
    init();
});

function init() {


    updatePercenColours();
    $j('.show-roll a').on('click', function() {

        $j(this).parents('.past-course, .future-course').append('<button class="btn btn-primary vertical-center btn-xs btn-finish">CLOSE</button>').addClass('past-course-diff');
        $j('.show-roll a').css('pointer-events', 'none');
        $j('[id^=class-description-]').hide();
        var id = $j(this).attr('id');
        $j('#class-description-' +  id).show()

        $j('button.btn-finish').on('click', function () {
            $j(this).parents('.past-course, .future-course').removeClass('past-course-diff');
            $j('#class-description-' +  id).hide();
            $j('.show-roll a').css('pointer-events', 'auto');
            $j('button.btn-finish').remove();
        });
    });

}

function updatePercenColours() {
    $j('.percents-of-attendance span, .current-attendance span').each( function(){
        parent = $j(this).parent();
        var percent = $j(this).text();

        if (percent>90){
            parent.addClass('full-percents');
        }
        else if (percent>50) {
            parent.addClass('half-percents');
        }
        else{
            parent.addClass('mimimum-percents');
        }
    });
}
