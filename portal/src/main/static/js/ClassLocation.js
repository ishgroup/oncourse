import jQuery, * as $ from "./jquery-1.11.2";

var $j = jQuery.noConflict();

var ClassLocation = function () {
};

ClassLocation.prototype = {
    classLocationTab: null,
    courseClassId: null,
    initMap: function () {
        $j.ajax({
            type: "GET",
            url: "/portal/class.classlocation:getlocation/" + this.courseClassId,
            async: true,
            cache: false,
            success: function (data) {
                var mapOptions = {
                    zoom: 14,
                    center: new google.maps.LatLng(data.latitude, data.longitude),
                    mapTypeId: google.maps.MapTypeId.ROADMAP
                };

                map = new google.maps.Map(document.getElementById("venue"),
                    mapOptions);

                var marker = new google.maps.Marker({
                    position: new google.maps.LatLng(data.latitude, data.longitude),
                    map: map
                });
            },
            error: function (data) {
                console.log(data)
            }
        });
    },

    init: function () {
        var self = this;
        var path = window.location.pathname.split('/');
        this.courseClassId = path.length > 0 ? path[3] : null;
        if (this.courseClassId) {
            this.classLocationTab = $j("li#class-location-tab");
            if(path.length == 5 && path[4] == "location") {
                self.initMap()
            }
            this.classLocationTab.on('click', function () {
                self.initMap()
            })
        }
    }
};

$j(document).ready(function () {
    if ($j('div#venue').length) {
        new ClassLocation().init();
    }
});
