goog.provide('addCollege');

goog.require('jquery');

var $j = jQuery.noConflict();

$j(document).ready(function() {
    initHandles();
});

function initHandles()
{
    initFindCollegeByServiceKey();
    initAddBillingCodeAndCollegeKey();
}

function sendAjaxWithData(actionLink,data,success)
{
    $j.ajax({
        type: "GET",
        url: actionLink,
        data: data,
        async: true,
        //if the parameter is not set internet explorer loads content from cache
        cache: false,
        success: success
    });
}

function initFindCollegeByServiceKey() {

    $j("#newCollege").hide();

    $j("#find").click(function(){
        $j("#newCollege").hide();

        var key = $j("#serviceKey").attr("value");

        var success = function(data) {
            $j("#results").empty();
            $j("#results").append(data["message"]);

            if (data["needCollegeKey"]) {
                $j("#collegeId").val(data["collegeId"]);
                $j("#newCollege").show();
            }
        }

        var actionLink = $j(".findServiceKeyLink").attr('href');
        var data = "key=" + key;

        sendAjaxWithData(actionLink,data,success);
    })
}

function initAddBillingCodeAndCollegeKey() {

    $j("#addNewCollege").click(function() {
        var billingCode = $j("#billingCode").attr("value");
        var collegeKey = $j("#collegeKey").attr("value");
        var collegeId = $j("#collegeId").attr("value");

        if ( !billingCode || !collegeKey ) {
            $j("#newCollegeValidationResult").empty();
            $j("#newCollegeValidationResult").append("Billing Code and College Key must not be empty");
            return;
        }

        var success = function(data) {
            $j("#newCollegeValidationResult").empty();
            $j("#newCollegeValidationResult").append(data["message"]);

            if (data._tapestry && data._tapestry.redirectURL)
                window.location.replace(data._tapestry.redirectURL);
        }

        var actionLink = $j(".addBillingCodeAndCollegeKeyLink").attr('href');
        var data = "billingCode=" + billingCode + "&collegeKey=" + collegeKey  + "&collegeId=" + collegeId;

        sendAjaxWithData(actionLink,data,success);
    })
}
