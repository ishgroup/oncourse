/**
* coursesFilter.js
*
* License: copyright ish group
* Purpose:
*  Contains functionality related to Faceted Search for courses
*/

/**
* The function inserts "/" in front of the path. It is required for IE.
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

/**
*
* @param item
* @return {string} in lowercase decoded tags url
*/
function filterItem(item) {
  return escape(decodeURIComponent(item)).toLowerCase();
}

function weekDays() {
  return ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
}

/**
* Format courses url
*
*/
CoursesUrlFormat = function () {
};

CoursesUrlFormat.prototype = {
  /**
  *
  * @param request
  * @return {string} return formatted request url
  *
  */
  format: function (request) {
    var url = "/courses";
    var parameters = [];

    if (request.browseTag) {
      url += request.browseTag;
    }

    if (request.s) {
      parameters.push("s=" + request.s);
    }

    $j.each(request.tags, function (index, tag) {
      parameters.push("tag=" + tag);
    });

    $j.each(request.locations, function (index, location){
      parameters.push("near=" + location);
    });

    $j.each(request.durations, function (index, duration){
      parameters.push("duration=" + duration);
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

    if (request.site) {
      parameters.push("site=" + request.site);
    }

    if (parameters.length > 0) {
      url += "?";
      url += parameters.join("&");
    }

    return url;
  },

  /**
  *
  * @param url
  * @return {object}, Generate object from search string and returns
  * { protocol, host, hostname, port, pathname, search, tags, searchObject, hash, browseTag, locations, day, time, before, km, s, site }
  */
  parse: function (url) {
    var parser = document.createElement('a'),
      searchObject = {},
      queries,
      split,
      browseTag,
      tags = [],
      locations = [],
      durations = [],
      day,
      time,
      before,
      km, s, site;
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
            locations.push(filterItem(split[1]));
            break;
          case "duration":
            durations.push(filterItem(split[1]));
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
          case "site":
            site = split[1];
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
      durations: durations,
      day: day,
      time: time,
      before: before,
      km: km,
      s: s,
      site: site
    };
  }
};

/**
* Create courses filter with global options
*
*/
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
  // Init courses filter
  init: function () {
    this.request = this.format.parse(this.currentLocation.href);

    this.updateControlValues();
    this.addControlListeners();
  },

  // Get option control tags data path
  getControlBy: function (path) {
    return $j(this.optionClass + "[data-path='" + path + "']");
  },

  // Handler of courses filter
  addSearchFormListeners: function () {
    var self = this;

    $j(document).on('click', self.subjectOptionButton, function(e) {
      e.preventDefault();
      self.request.s = $j(self.subjectOption).val();
      self.loadCourses();
    });

    $j(document).on('blur', self.subjectOption, function(e) {
      e.preventDefault();
      self.request.s = $j(self.subjectOption).val();
    });

    $j(document).on('submit', self.subjectForm, function(e) {
      e.preventDefault();
      self.request.s = $j(self.subjectOption).val();
      self.loadCourses();
    });
  },

  // Handler of clear option search
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
      index = this.request.durations.indexOf(path);
      if (index > -1) {
        this.request.durations.splice(index, 1);
      }
      index = this.request.tags.indexOf(path);
      if (index > -1) {
        this.request.tags.splice(index, 1);
      }
      var s = this.request.s !== undefined && this.request.s.includes(path);
      if (s === true) {
        this.request.s = undefined;
      }
      var km = this.request.km !== undefined && this.request.km.includes(path);
      if (km === true) {
        this.request.km = undefined;
      }
      var day = this.request.day !== undefined && weekDays().includes(path);
      if (day === true) {
        this.request.day = undefined;
      }
      var time = this.request.time !== undefined && this.request.time.includes(path);
      if (time === true) {
        this.request.time = undefined;
      }
      var before = this.request.before !== undefined && this.request.before.includes(path);
      if (before === true) {
        this.request.before = undefined;
      }
      var site = this.request.site !== undefined && this.request.site.includes(path);
      if (site === true) {
        this.request.site = undefined;
      }
      this.loadCourses();
    }
  },

  // Handler of change filter and clear search filter
  addControlListeners: function () {
    var self = this;

    self.addSearchFormListeners();

    $j(document).on('click', this.clearOptionClass, function(e) {
      e.preventDefault();
      var path = decodeURIComponent($j(this).data("path"));
      self.handleClearOptionClass(path);
    });

    $j(document).on('click', this.clearAllClass, function (e) {
      e.preventDefault();
      window.location.href = "/courses"
    });

    $j(document).on('click', this.optionClass, function () {
      self.changeFilter(this);
    });

    $j(document).find(this.optionClass).next('a').on('click', function (e) {
      e.preventDefault();
      $j(this).prev().trigger("click");
    });

    // Handler of refine options filter
    $j(document).on('change', this.refineOptionId, function (e) {
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
    });
  },

  // Update filter option values
  updateControlValues: function () {
    var self = this;

    this.getControlBy(filterItem(this.request.browseTag)).prop('checked', true);
    $j.each(this.request.tags, function (index, tag) {
      self.getControlBy(filterItem(tag)).prop('checked', true);
    });

    $j.each(this.request.locations, function (index, path) {
      var control =  self.getControlBy(filterItem(path).replace(/%20/g ,' '));
      control.prop('checked', true);
      var tabid = self.getControlBy(filterItem(path).replace(/%20/g ,' ')).parents("section [id^='loc']").data('tabid');

      $j('#' + tabid).attr('checked', true);
      $j('label[for="' + tabid + '"]').addClass('location-active');
    });

    $j.each(this.request.durations, function (index, path) {
      var control = self.getControlBy(filterItem(path));
      control.prop('checked', true);
    });

    if (this.disableZeroControls) {
      $j(this.optionClass + "[data-counter=0]").prop('disabled', true);
    }

    if(this.request.site !== '') {
      self.getControlBy(this.request.site).prop('checked', true);
      self.getControlBy(this.request.site).addClass('checked');
    }

    this.updateRefineControl();
  },

  // Update refine option values
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

  // Remove checked tag
  removeTag: function (tag) {
    var tagText = unescape(decodeURIComponent(tag));
    tagText = tagText.replace(/&/g, '%26');

    var index = this.request.tags.indexOf(tagText);

    if (index > -1) {
      this.request.tags.splice(index, 1);
    }
    this.getControlBy(filterItem(tag)).prop('checked', false);
  },

  // Get control type
  getControlType: function(control) {
    var id = $j(control).attr("id");
    if (id.indexOf("tag_") == 0) {
      return "tag"
    } else  if (id.indexOf("location_") == 0) {
      return "location"
    } else  if (id.indexOf("site_") == 0) {
      return "site"
    } else  if (id.indexOf("duration_") == 0) {
      return "duration"
    }
  },

  // Remove item on control type
  removeCondition: function (control) {
    switch (this.getControlType(control)) {
      case "tag":
        this.removeTagCondition(control);
        break;
      case "location":
        this.removeLocationCondition(control);
        break;
      case "site":
        this.removeSiteCondition(control);
        break;
      case "duration":
        this.removeDurationCondition(control);
        break;
    }
  },

  // Add search condition on control type
  addCondition: function (control) {
    switch (this.getControlType(control)) {
      case "tag":
        this.addTagCondition(control);
        break;
      case "location":
        this.addLocationCondition(control);
        break;
      case "site":
        this.addSiteCondition(control);
        break;
      case "duration":
        this.addDurationCondition(control);
        break;
    }
  },

  // Uncheck searched location
  removeLocationCondition: function (control) {
    var path = filterItem($j(control).data('path'));
    var index = this.request.locations.indexOf(path);

    if (index > -1) {
      this.request.locations.splice(index, 1);
    }

    $j(control).prop('checked', false);
    this.request.km = null;
  },

  // Add location condition search
  addLocationCondition: function (control) {
    this.request.locations.push(filterItem($j(control).data('path')));
    this.request.km = null;
  },

  // Remove tags from search request
  removeTagCondition: function(control) {
    var tag = $j(control).data('path').replace(/\s/g, '%20');

    if (filterItem(this.request.browseTag) == tag) {
      this.request.browseTag = null;
      if (this.request.tags.length > 0) {
        this.request.browseTag = this.request.tags[0];
        this.request.tags.splice(0, 1);
      }
    } else {
      this.removeTag(tag);
    }
  },

  // Add tags search to search request
  addTagCondition: function(control) {
    var self = this;
    var tag = decodeURIComponent($j(control).data('path'));
    tag = tag.replace(/&/g, '%26');

    if (this.request.browseTag) {
      if (this.isParentTag(this.request.browseTag, tag)) {
        this.getControlBy(filterItem(this.request.browseTag)).prop('checked', false);
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

  // Add site condition to search request
  addSiteCondition: function (control) {
    this.request.site = $j(control).data('path');
  },

  addDurationCondition: function (control) {
    this.request.durations.push(filterItem($j(control).data('path')));
  },

  // Remove site condition from search request
  removeSiteCondition: function (control) {
    this.request.site = '';
  },

  removeDurationCondition: function (control) {
    var path = filterItem($j(control).data('path'));

    var index = this.request.durations.indexOf(path);

    if (index > -1) {
      this.request.durations.splice(index, 1);
    }
  },

  // Checking parent tag
  isParentTag: function (currentTag, newTag) {
    return currentTag.indexOf(newTag) == 0 || newTag.indexOf(currentTag) == 0;
  },

  // Update search filter and toggle checked class to search item,
  // load courses based on filter
  changeFilter: function (control) {
    var self = this;
    if ($j(control).prop("checked")) {
      this.addCondition(control);
      $j(control).parent().addClass('checked');
    } else {
      this.removeCondition(control);
      $j(control).parent().removeClass('checked');
    }
    this.loadCourses();
  },

  // Load courses based on formatted request url
  loadCourses: function () {
    var self = this;
    var url = this.format.format(this.request);
    window.location.href = url;
  },

  // Update courses counters
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
        $j(self.optionClass).next("label").next("span").text("0");
        $j.each(data, function(index, value){
          $j("#tag_"+value.id).next("label").next("span").text(value.counter);
        })
      },
      error: function (data) {
        //console.log(data);
      }
    });
  }
};

(function($) {
  $(document).ready(function () {

    $('.side-box').each(function(key, box) {
      $(box).find('.locations-list>ul').each(function(lKey, list) {
        var _list_title = $(list).find('>li>span').text();
        var _list_class = $(list).attr('class') + (lKey+1);
        var _list_template = '<input id="'+ _list_class +'" type="radio" name="tabs"><label for="'+ _list_class +'">'+ _list_title +'</label>';

        $(list).find('>li>span').remove();
        $(list).attr({ id: _list_class + '_tags', 'data-tabid': _list_class });
        $(_list_template).insertBefore($(list));
      });
    });

    $('.tags-list .filter-option.site-option').each(function(key, value) {
      var _label = $(value).find('label.filter-option-label');
      var _for = _label.attr('for'), _path = _label.data('path');

      var _input_checkbox = '<input type="checkbox" data-counter="0" data-path="'+ _path +'" class="filter-option-control" name="'+ _for +'" id="'+ _for +'">';
      var _option_counter = '<span class="filter-option-counter">(0)</span>';

      $(value).prepend(_input_checkbox);
      $(value).append(_option_counter);

      if(_path !== '') {
        $.ajax({
          type: "GET",
          url: '/courses?site=' + _path,
          async: false,
          cache: false,
          success: function (data) {
            if($(data).find('div.courseItem') !== undefined) {
              var _total_option_counter = $(data).find('div.courseItem').length;
              $(value).find('.filter-option-counter').text(_total_option_counter);
              $(value).find('.filter-option-control').attr('data-counter', _total_option_counter);
            }
          },
          error: function (data) {
            //console.log(data);
          }
        });
      }
    });

    var coursesFilter = new CoursesFilter();
    coursesFilter.init();
    // $('#courses-filter-refine-option').customSelect({customClass:'courses-filter-refine-custom'});
    $('.filters-container ul ul li ul.tag-list').has('li').parent().find('>label').addClass('hasOptionChild');

    $('.courses-menu .filters-container .side-box .filter-option').each(function(key, value) {
      var childList = $(value).find('>.tag-list>li');

      var filterCount = $(this).find('>.filter-option-counter');
      filterCount.text(filterCount.text().replace(/([\(|\)])+/g, ''));

      if(childList.length > 0) {
        $.each(childList, function(cKey, cValue) {
          filterCount = $(cValue).find('>.filter-option>.filter-option-counter');
          filterCount.text(filterCount.text().replace(/([\(|\)])+/g, ''));
        });
      }
    });

  });
})(jQuery);
