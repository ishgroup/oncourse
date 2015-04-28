/**
 *  shortlist.js
 *
 *  Events and AJAX responses used to create/add/delete to the shortlist.
 *
 */


function slideShowShortlist() {

    if ($j('.shortListOrder').size() > 0) {

        $j('.shortListOrder').slideUp();

        $j('.shortlistActionShow').click(function () {
            $j('.shortListOrder').fadeIn('fast');
            $j(this).hide();
            $j('.shortlistActionHide').show().addClass('active');
        });

        $j('.shortlistActionHide').click(function () {
            $j('.shortListOrder').fadeOut('fast');
            $j(this).removeClass('active').hide();
            $j('.shortlistActionShow').show();
        });

    }

}

function refreshShortList() {
    /**
     * Refreshes the shortList component and if the courseClassToRefresh!=null,
     * refreshes the shortlistControl for this class.
     */
    $j.ajax({
        type: "GET",
        url: '/refreshShortList',
        success: function (msg) {
            $j('#shortlist').replaceWith($j.trim(msg));
            slideShowShortlist();
        }
    });
}


$j(document).ready(function () {


    slideShowShortlist();

    $j(document).on("click", ".closebutton", function () {
        $j(this).parents(".dialogContainer").fadeOut("fast");
    });

    // Add items to the shortlist return a response to the user
    $j(document).on("click touchend", '.enrolAction:not(.disabled)', function (e) {
        e.preventDefault();
        //grab the classid of the enrol button that's just been clicked.
        var listid = $j(this).parents(".classItem").data("classid");
        var buttonPos = $j('.classItem[data-classid=' + listid + ']').position();
        var link = this.href;

        // does it already exist in the shortlist? if not, make that ajax call.
        if (($j('.shortlistChoices li[data-classid=' + listid + ']') == null)
            | ($j('.shortlistChoices li[data-classid=' + listid + ']').length == 0)) {

            $j.ajax({
                type: "GET",
                url: link,
                success: function (msg) {
                    //Make the order confirmation box appear
                    $j(".confirmOrderDialog .confirm-txt").text("Thanks for adding: ");

                    $j(".confirmOrderDialog .className").show().text($j('.classItem[data-classid=' + listid + '] .summary').text());

                    $j(".confirmOrderDialog .classDate").show().text($j('.classItem[data-classid=' + listid + '] .class-item-info-l > .date a:first').text());

                    $j('.classItem[data-classid=' + listid + '] .enrolAction').addClass('enrol-added-class').text("Added");

                    $j(".confirmOrderDialog").css({
                        top: buttonPos.top,
                        right: "150px"
                    });

                    $j(".confirmOrderDialog").stop(true, false).fadeIn("fast");
                    $j('.confirm-close-wrapper .closeButton').click(function () {
                        $j(".confirmOrderDialog").fadeOut("fast");
                        e.preventDefault();
                    });
                    $j('#shortlist').replaceWith($j.trim(msg));
                    slideShowShortlist();

                    if (typeof dataLayer !== 'undefined') {
                        dataLayer.push({
                            'event': 'ShoppingCart',
                            'eventAction': 'Add',
                            'eventValue': listid.toString()
                        });
                    }

                }
            });
        } else {
            // Else, let them know that it's already on their shortlist and get them to go to checkout
            $j(".confirmOrderDialog .confirm-txt").text("You've already added this class to your shortlist. Do you want to proceed to checkout?");
            $j(".confirmOrderDialog .className").empty();
            $j(".confirmOrderDialog .classDate").empty()

            $j(".confirmOrderDialog").css({
                top: buttonPos.top,
                right: "150px"
            });

            $j(".confirmOrderDialog").stop(true, false).fadeIn("fast");
            $j('.confirm-close-wrapper .closeButton').click(function () {
                $j(".confirmOrderDialog").fadeOut("fast");
                e.preventDefault();
            });
        }
    });


    /* Remove an item from the shortlist */
    $j(document).on("click", '#shortlist .deleteItem a', function (e) {
        e.preventDefault();

        var fThis = $j(this);
        fThis.parent().addClass('loading');
        var link = this.href;
        var id = $j(this).parents('li').attr('data-classid');

        $j.ajax({
            type: "GET",
            url: link,
            success: function (msg) {

                if ($j('.classItem[data-classid=' + id + '] .enrolAction').attr("data") == 'application') {
                    $j('.classItem[data-classid=' + id + '] .enrolAction').removeClass('enrol-added-class').text("Apply Now");
                } else {
                    $j('.classItem[data-classid=' + id + '] .enrolAction').removeClass('enrol-added-class').text("Enrol Now");
                }

                $j('#shortlist').replaceWith(msg);
                slideShowShortlist();
            }
        });
    });


    // Drop our shortlisted items in the shortlist box
    $j('li.onshortlist-x a.cutitem').click(function () {
        var link = this.href;
        var itemId = this.id.match(/(\d+)/)[1];
        $j.ajax({
            type: "GET",
            url: link,
            success: function () {
                window.parent.location.reload(true);
            }
        });
        return false;
    });

    $j(".course_modules > span").click(function () {
        $j(this).toggleClass("active");
        $j(this).next("ul").slideToggle(500);
    });

});	