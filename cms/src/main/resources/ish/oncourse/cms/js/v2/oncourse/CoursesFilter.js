//goog.provide('CoursesFilter');

var $j = jQuery.noConflict();

/**
 * The function inserts "/" in front of the path. It needs for IE.
 * @param pathname
 * @returns {String} returns the adjusted path
 */
function adjustPathName(pathname) {
    var paths = pathname.split("/");

    if (paths.length > 0 && paths[0] == "courses") {
        return "/"+pathname;
    }
    return pathname;
}
/**
 *
 * @param pathname
 * @returns {boolean} true if the pathname is "courses" path
 */
function isCoursesPath(pathname) {
    var paths = pathname.split("/");
    return paths.length > 1 && paths[1] == "courses";
}

CoursesUrlFormat = function () {
};

CoursesUrlFormat.prototype = {
    format: function (request) {
        var url = "/courses";
        if (request.browseTag) {
            url += request.browseTag;
        }

        var parameters = [];

        if (request.s) {
            parameters.push("s=" + request.s);
        }

        $j.each(request.tags, function (index, tag) {
            parameters.push("tag=" + tag);
        });

        $j.each(request.locations, function (index, location){
            parameters.push("near=" + location);
        });

        if (request.km) {
            parameters.push("km=" + request.km);
        }

        if (request.day) {
            parameters.push("day=" + request.day);
        }

        if (request.time) {
            parameters.push("time=" + request.time);
        }

        if (request.before) {
            parameters.push("before=" + request.before);
        }


        if (parameters.length > 0) {
            url += "?";
            url += parameters.join("&");
        }
        return url;
    },

    parse: function (url) {
        var parser = document.createElement('a'),
            searchObject = {},
            queries,
            split,
            browseTag,
            tags = [],
            locations = [],
            day,
            time,
            before,
            km, s;
        // Let the browser do the work
        parser.href = url.toLowerCase();
        var pathname = adjustPathName(parser.pathname);
        if (isCoursesPath(pathname)) {
            queries = parser.search.replace(/^\?/, '').split('&');
            $j.each(queries, function (index, query) {
                split = query.split('=');
                switch (split[0]) {
                    case "tag":
                        tags.push(split[1]);
                        break;
                    case "near":
                        locations.push(split[1]);
                        break;
                    case "km":
                        km = split[1];
                        break;
                    case "day":
                        day = split[1];
                        break;
                    case "time":
                        time = split[1];
                        break;
                    case "before":
                        before = split[1];
                        break;
                    case "s":
                        s = split[1];
                        break;
                    default :
                        if (searchObject[split[0]] == null) {
                            searchObject[split[0]] = []
                        }
                        searchObject[split[0]].push(split[1]);
                        break;
                }
            });
            browseTag = pathname.replace("/courses", "");
        }

        return {
            protocol: parser.protocol,
            host: parser.host,
            hostname: parser.hostname,
            port: parser.port,
            pathname: parser.pathname,
            search: parser.search,
            tags: tags,
            searchObject: searchObject,
            hash: parser.hash,
            browseTag: browseTag,
            locations: locations,
            day: day,
            time: time,
            before: before,
            km: km,
            s: s
        };
    }
};


CoursesFilter = function () {
    this.subjectOption = "#s";
    this.subjectOptionButton = "#find";
    this.subjectForm = "#search";
    this.optionClass = ".filter-option-control";
    this.clearAllClass = ".courses-filter-clear-all";
    this.clearOptionClass = ".courses-filter-clear-option";
    this.refineOptionId = "#courses-filter-refine-option";
    this.updateHtmlBlockId = "#courses-list";
    this.disableZeroControls = true;


    this.currentLocation = window.location;
    this.comingSoonPeriod = 30;
    this.request = null;
    this.format = new CoursesUrlFormat();
};

CoursesFilter.prototype = {
    init: function () {
        this.request = this.format.parse(this.currentLocation.href);

        this.updateControlValues();
        this.addControlListeners();
    },

    getControlBy: function (path) {
        return $j(this.optionClass + "[data-path='" + path + "']");
    },

    addSearchFormListeners: function () {
        var self = this;

        $j("body").on('click', self.subjectOptionButton, function(e) {
            e.preventDefault();
            self.request.s = $j(self.subjectOption).val();
            self.loadCourses();
        });

        $j("body").on('blur', self.subjectOption, function(e) {
            e.preventDefault();
            self.request.s = $j(self.subjectOption).val();
        });

        $j("body").on('submit', self.subjectForm, function(e) {
            e.preventDefault();
            self.request.s = $j(self.subjectOption).val();
            self.loadCourses();
        });
    },

    handleClearOptionClass: function(path) {
        var control = this.getControlBy(path);
        if (control.length > 0) {
            control.prop('checked', false);
            this.changeFilter(control)
        } else {
            var index = this.request.locations.indexOf(path);
            if (index > -1) {
                this.request.locations.splice(index, 1);
            }
            index = this.request.tags.indexOf(path);
            if (index > -1) {
                this.request.tags.splice(index, 1);
            }
            this.loadCourses();
        }
    },

    addControlListeners: function () {
        var self = this;

        self.addSearchFormListeners();

        $j("body").on('click', this.clearOptionClass, function(e) {
            e.preventDefault();
            var path = $j(this).data("path");
            self.handleClearOptionClass(path);
        });

        $j("body").on('click', this.clearAllClass, function (e) {
            e.preventDefault();
            window.location.href = "/courses"
        });

        $j(this.optionClass).on('click', function () {
            self.changeFilter(this);
        });

        $j(this.optionClass).next('a').on('click', function (e) {
            e.preventDefault();
            $j(this).prev().trigger("click");
        });

        $j(this.refineOptionId).on('change', function (e) {
            self.request.day = null;
            self.request.time = null;
            self.request.before = null;
            var value = $j(this).val();
            switch (value) {
                case "coming_soon":
                    self.request.before = self.comingSoonPeriod;
                    break;
                case "weekend":
                    self.request.day = "weekend";
                    break;
                case "weekday":
                    self.request.day = "weekday";
                    break;
                case "evening":
                    self.request.time = "evening";
                    break;
            }
            self.loadCourses();
        })
    },

    updateControlValues: function () {
        var self = this;
        this.getControlBy(this.request.browseTag).prop('checked', true);
        $j.each(this.request.tags, function (index, tag) {
            self.getControlBy(tag).prop('checked', true);
        });

        $j.each(this.request.locations, function (index, path) {
            var control =  self.getControlBy(path);
            control.prop('checked', true);
            var tabid = self.getControlBy(path).parents("section [id^='loc']").data('tabid');
            $j('#' + tabid).attr('checked', true)
        });

        if (this.disableZeroControls) {
            $j(this.optionClass + "[data-counter=0]").prop('disabled', true);
        }

        this.updateRefineControl();
    },

    updateRefineControl: function () {
        if (this.request.before) {
            $j(this.refineOptionId).val("coming_soon");
        } else if (this.request.day) {
            $j(this.refineOptionId).val(this.request.day);
        } else if (this.request.time) {
            $j(this.refineOptionId).val(this.request.time);
        } else {
            $j(this.refineOptionId).val("no_refine");
        }
    },

    removeTag: function (tag) {
        var index = this.request.tags.indexOf(tag);
        if (index > -1) {
            this.request.tags.splice(index, 1);
        }
        this.getControlBy(tag).prop('checked', false);
    },

    getControlType: function(control) {
        var id = $j(control).attr("id");
        if (id.indexOf("tag_") == 0) {
            return "tag"
        } else  if (id.indexOf("location_") == 0) {
            return "location"
        }
    },


    removeCondition: function (control) {
        switch (this.getControlType(control)) {
            case "tag":
                this.removeTagCondition(control);
                break;
            case "location":
                this.removeLocationCondition(control);
                break;
        }
    },

    addCondition: function (control) {
        switch (this.getControlType(control)) {
            case "tag":
                this.addTagCondition(control);
                break;
            case "location":
                this.addLocationCondition(control);
                break;
        }
    },

    removeLocationCondition: function (control) {

        var path = $j(control).data('path');
        var index = this.request.locations.indexOf(path);
        if (index > -1) {
            this.request.locations.splice(index, 1);
        }
        $j(control).prop('checked', false);
        this.request.km = null;
    },

    addLocationCondition: function (control) {
        this.request.locations.push($j(control).data('path'));
        this.request.km = null;
    },

    removeTagCondition: function(control) {
        var tag = $j(control).data('path');

        if (this.request.browseTag == tag) {
            this.request.browseTag = null;
            if (this.request.tags.length > 0) {
                this.request.browseTag = this.request.tags[0];
                this.request.tags.splice(0, 1);
            }
        } else {
            this.removeTag(tag);
        }
    },

    addTagCondition: function(control) {
        var self = this;
        var tag = $j(control).data('path');
        if (this.request.browseTag) {
            if (this.isParentTag(this.request.browseTag, tag)) {
                this.getControlBy(this.request.browseTag).prop('checked', false);
                this.request.browseTag = tag;
                var currentTags = this.request.tags.slice();
                $j.each(currentTags, function (index, currentTag) {
                    if (self.isParentTag(currentTag, tag)) {
                        self.removeTag(currentTag);
                    }
                })
            } else {
                var index = this.request.tags.indexOf(tag);
                if (index == -1) {
                    this.request.tags.push(tag);
                }
            }
        } else {
            this.request.browseTag = tag;
        }
    },

    isParentTag: function (currentTag, newTag) {
        return currentTag.indexOf(newTag) == 0 || newTag.indexOf(currentTag) == 0;
    },

    changeFilter: function (control) {
        var self = this;
        if ($j(control).prop("checked")) {
            this.addCondition(control);
        } else {
            this.removeCondition(control);
        }
        this.loadCourses();
    },

    loadCourses: function () {
        var self = this;
        var url = this.format.format(this.request);
        //if (isCoursesPath(this.parser.pathname)) {
        //    $j.ajax({
        //        type: "GET",
        //        url: url,
        //        async: false,
        //        cache: false,
        //        headers: {
        //            updateCoursesByFilter: true
        //        },
        //        success: function (data) {
        //            $j(self.updateHtmlBlockId).replaceWith(data);
        //        },
        //        error: function (data) {
        //            console.log(data);
        //        }
        //    });
        //    history.pushState(null, null, url);
        //    this.updateCounters();
        //} else {
            window.location.href = url;
        //}
    },

    updateCounters: function () {
        var self = this;
        var url = "/coursecounters";
        var options = {};
        var data = {
            request: JSON.stringify(this.request),
            options: JSON.stringify(options)
        };
        $j.ajax({
            type: "GET",
            url: url,
            data: data,
            async: false,
            cache: false,
            success: function (data) {
                $j(self.optionClass).next("label").next("span").text("(0)");
                $j.each(data, function(index, value){
                    if (value.counter > 0) {
                        console.log(value.id);
                    }
                    $j("#tag_"+value.id).next("label").next("span").text("("+value.counter+")");
                })
            },
            error: function (data) {
                console.log(data);
            }
        });
    }
}


jQuery(document).ready(
    function () {
        var coursesFilter = new CoursesFilter();
        coursesFilter.init();
    });
