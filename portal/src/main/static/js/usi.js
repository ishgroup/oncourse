goog.provide('usi');
goog.require('jquery');
//we need add initialise.js code before the code because initialise.js contains common functionality
// for all input controls (like unbind blur and focus functions)
goog.require('initialise');



var $j = jQuery.noConflict();

Usi = function () {
};

Usi.prototype = {
    data: 0,
    initialize: function () {
        var self = this;

        $j("#usi-next").click(function () {
            self.next();
        });
        this.loadData();
    },

    showData: function () {
        var self = this;

        $j('.form-group').removeClass('has-error');
        $j('small.help-block').empty();
        $j.each(this.data.values, function (index, value) {
            self.fillControl(value);
        });
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

        var actionLink = "/portal/usi/usi:value";
        $j.ajax({
            url: actionLink,
            type: 'post',
            async: false,
            cache: false,
            success: function (data) {
                self.data = data;
                self.showData();
                if (data.step == 'wait') {
                    self.reloadByTimeout();
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(textStatus);
                console.log(errorThrown);
                window.location.reload();
            }
        });
    },

    reloadByTimeout: function () {
        var self = this;
        setTimeout(function () {
            window.location.reload();
        }, 5000);
    },

    verifyUsi: function()
    {
            $j.ajax({
                url: '/portal/usi/usi:verifyUsi',
                type: 'post',
                cache: false,
                success: function (data) {
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
        var actionLink = "/portal/usi/usi:next";
        var currentStep = this.data.step;
        $j.ajax({
            url: actionLink,
            type: 'post',
            async: false,
            cache: false,
            data: data,
            success: function (data) {
                var oldStep = self.data.step;
                self.data = data;
                if (data.step == 'wait' && oldStep != 'wait')
                {
                    self.verifyUsi();
                }
                self.showData();
                if (!data.hasErrors)
                    window.location.reload();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(textStatus);
                console.log(errorThrown);
                window.location.reload();
            }
        });
    }
};



$j('document').ready(function () {
    if ($j('#usi-page').length != 0) {
        new Usi().initialize();
    }
});

