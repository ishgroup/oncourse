goog.provide('CoursesFilter');

var $j = jQuery.noConflict();

CoursesUrlFormat = function () {
};

CoursesUrlFormat.prototype = {
    format: function (request) {
        var url = "/courses";
        if (request.browseTag) {
            url += request.browseTag;
        }
        if (request.tags.length > 0) {
            url += "?";
            $j.each(request.tags, function (index, tag) {
                if (index > 0) {
                    url += "&"
                }
                url += "tag=" + tag;
            });
        }
        return url;
    },

    parse: function (url) {
        var parser = document.createElement('a'),
            searchObject = {},
            queries, split, i, browseTag, tags = [];
        // Let the browser do the work
        parser.href = url;
        // Convert query string to object
        queries = parser.search.replace(/^\?/, '').split('&');
        $j.each(queries, function (index, query) {
            split = query.split('=');
            switch (split[0]) {
                case "tag":
                    tags.push(split[1]);
                    break;
                default :
                    if (searchObject[split[0]] == null) {
                        searchObject[split[0]] = []
                    }
                    searchObject[split[0]].push(split[1]);
                    break;
            }
        });
        browseTag = parser.pathname.replace("/courses", "");

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
            browseTag: browseTag
        };
    }
};


CoursesFilter = function () {
    this.optionClass = ".courses-filter-option"
    this.clearAllClass = ".courses-filter-clear-all";
    this.clearClass = ".courses-filter-clear";
    this.updateHtmlBlockId = "#courses-list";
    this.currentLocation = window.location;
    this.request = null;
    this.format = new CoursesUrlFormat();

};

CoursesFilter.prototype = {
    init: function () {
        this.request = this.format.parse(this.currentLocation.href);

        this.updateControlValues();
        this.addControlListeners();
    },

    addControlListeners: function () {
        var self = this;

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
    },

    updateControlValues: function () {
        var self = this;
        if (this.request.pathname.startsWith('/courses')) {
            $j(this.optionClass + "[data-path='" + this.request.browseTag + "']").prop('checked', true);
            $j.each(this.request.tags, function (index, tag) {
                $j(self.optionClass + "[data-path='" + tag + "']").prop('checked', true);
            });
        }
    },

    removeTag: function (tag) {
        var index = this.request.tags.indexOf(tag);
        if (index > -1) {
            this.request.tags.splice(index, 1);
        }
        $j(this.optionClass + "[data-path='" + tag + "']").prop('checked', false);
    },


    removeCondition: function (control) {
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

    addCondition: function (control) {
        var self = this;
        var tag = $j(control).data('path');
        if (this.request.browseTag) {
            if (this.isParentTag(this.request.browseTag, tag)) {
                $j(this.optionClass + "[data-path='" + this.request.browseTag + "']").prop('checked', false);
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
        return currentTag.startsWith(newTag) || newTag.startsWith(currentTag);
    },

    changeFilter: function (control) {
        var self = this;
        if ($j(control).prop("checked")) {
            this.addCondition(control);
        } else {
            this.removeCondition(control);
        }
        var url = this.format.format(this.request);
        $j.ajax({
            type: "GET",
            url: url,
            async: false,
            //if the parameter is not set internet explorer loads content from cache
            cache: false,
            headers: {
                updateCoursesByFilter: true
            },
            success: function (data) {
                $j(self.updateHtmlBlockId).replaceWith(data);
            },
            error: function (data) {
                console.log(data);
            }
        });
        history.pushState(null, null, url);
    },
}


jQuery(document).ready(
    function () {
        var coursesFilter = new CoursesFilter();
        coursesFilter.init();
    });
