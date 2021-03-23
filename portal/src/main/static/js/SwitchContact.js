import jQuery, * as $ from "./jquery-1.11.2";

var $j = jQuery.noConflict();

$j(document).ready(function () {
    $j('a.switch-contact').on('click', function(){
        var contactId = $j(this).attr('data-contact');
        $j.ajax({
            type: "POST",
            url: "/portal/errorpage.pagestructure.bodystructure.bodyheader.loginuser.switchcontact:selectcontact/" + contactId,
            async: true,
            cache: false,
            success: function () {
                location.reload();
            },
            error: function (data) {
                console.log(data);
            }
        });
    });
});
