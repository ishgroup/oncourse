import jQuery, * as $ from "./jquery-1.11.2";
//we need add initialise.js code before the code because initialise.js contains common functionality
// for all input controls (like unbind blur and focus functions)
import "./initialise";
import "./timepicki";


var $j = jQuery.noConflict();

var Usi = function () {
};

Usi.prototype = {
    data: 0,
    step: 0,
    uniqueKey: 0,


    initialize: function () {
        var self = this;
			  this.step = $j("#usiStep").attr('data-step');
			  this.uniqueKey = document.location.pathname.split('/')[3]
        $j("#usi-next").click(function () {
            self.next();
        });

	    if (this.step) {
            this.loadData();
        }

        if ($j('input#dateOfBirth') && $j('input#dateOfBirth').length) {
            $j('input#dateOfBirth').datepicker({
                dateFormat: "dd/mm/yy",
                selectOtherMonths: true,
                selectOtherYear:true,
                changeMonth: true,
                changeYear: true,
                yearRange: "c-100:c",
                maxDate: 0
            });
        }

    },

    showData: function () {
        var self = this;

        $j('.form-group').removeClass('has-error');
        $j('small.help-block').empty();
        $j.each(this.data.values, function (index, value) {
            self.fillControl(value);
        });
			  $j('#main-container').find('.error-message').remove();
      if (this.data.errors && this.data.errors.length) {
          var errorsHtml = '';
					$j.each(this.data.errors, function (index, value) {
            errorsHtml += '<li>' + value + '</li>';
					});

					$j('#main-container').prepend($j('<div class="error-message"><ul>' + errorsHtml + '</ul></div>'))
				}
    },

    fillControl: function (value) {
        var control = $j('.form-control[name=' + value.key + ']');

        if (value.error) {
            var parent = control.closest('.form-group');
            parent.addClass('has-error');
            parent.find('small.help-block').text(value.error);
        }
        else {
            if (value.options) {
                control.empty();
                $j.each(value.options, function (index, option) {
                    control.append($j("<option></option>")
                        .attr("value", option.key)
                        .text(option.value));
                });
            }
            if (value.required) {
                var label = $j('.control-label[for=' + value.key + ']');
                label.text(label.text() + ' *');
            }
        }
        if (value.key == 'isMale') {
            $j('#female.form-control').prop("checked", value.value != "true");
            $j('#male.form-control').prop("checked", value.value == "true");
        }
        else {
            control.val(value.value);
        }
    },
    loadData: function () {
        var self = this;

        var actionLink = "/portal/usi/usi:value" + "/" + self.step + "/" + self.uniqueKey;
        $j.ajax({
            url: actionLink,
            type: 'post',
            async: false,
            cache: false,
            success: function (data) {
                self.data = data;
							  self.step = data.step;
                self.showData();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(textStatus);
                console.log(errorThrown);
                window.location.reload();
            }
        });
    },

    verifyUsi: function() {
			$j('#usi-main-form').hide();
			$j('#main-container').find('.error-message').remove();
			$j('.usi-progress-verify').show();
      var self = this;
      var data = $j(".form-control").serialize();

			$j.ajax({
            url: '/portal/usi/usi:verifyUsi' + "/" + self.step + "/" + self.uniqueKey,
            type: 'post',
            cache: false,
				    data: data,
            success: function (data) {
							$j('#usi-main-form').show();
							$j('.usi-progress-verify').hide();
							self.step = data.step;
							self.data = data;
              if (self.step === 'usi') {
								self.showData();
              } else {
								self.reload();
              }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(textStatus);
                console.log(errorThrown);
                window.location.reload();
            }
        });
    },

    locateUsi: function() {
    			$j('#usi-main-form').hide();
    			$j('#main-container').find('.error-message').remove();
    			$j('.usi-progress-locate').show();
          var self = this;
          var data = $j(".form-control").serialize();

    			$j.ajax({
                url: '/portal/usi/usi:locateUsi' + "/" + self.step + "/" + self.uniqueKey,
                type: 'post',
                cache: false,
    				    data: data,
                success: function (data) {
    							$j('#usi-main-form').show();
    							$j('.usi-progress-locate').hide();
    							self.step = data.step;
    							self.data = data;
    					        self.reload();
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log(textStatus);
                    console.log(errorThrown);
                    window.location.reload();
                }
            });
        },

    next: function () {
        var self = this;
        var data = $j(".form-control").serialize();
        var actionLink = "/portal/usi/usi:next" + "/" + self.step + "/" + self.uniqueKey;
        $j.ajax({
            url: actionLink,
            type: 'post',
            async: false,
            cache: false,
            data: data,
            success: function (data) {
                var oldStep = self.data.step;
                self.data = data;
							  self.step = data.step;
                self.showData();
                if (data.step === 'waitVerify' && oldStep !== 'waitVerify') {
                    self.verifyUsi();
                } else if (data.step == 'waitLocate' && oldStep !== 'waitLocate') {
                    self.locateUsi();
                } else if (!data.hasErrors) {
					self.reload();
				}
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(textStatus);
                console.log(errorThrown);
                window.location.reload();
            }
        });
    },

    reload: function () {
			var self = this;
			var search = document.location.search;
        if (search.indexOf("&step=") !== -1) {
            document.location.search  = search.replace(/(step=).*/, "$1" + self.step)
        } else {
            document.location.search += "&step=" + self.step
				}
    }
};



$j('document').ready(function () {
    if ($j('#usi-page').length != 0) {
        new Usi().initialize();
    }
});

