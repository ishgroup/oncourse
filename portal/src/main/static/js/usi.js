goog.provide('usi');
goog.require('jquery');

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

        $j('input.form-control').removeClass('t-error');
        $j('input.form-control').parent().removeClass('validate has-error').addClass('valid');
        $j.each(this.data.values, function (index, value) {
            self.fillControl(value);
        });
    },

    fillControl: function (value) {
        var control = $j('input[name=' + value.key + ']');
        if (value.error) {
            control.parent().removeClass('valid');
            control.parent().addClass('validate has-error');
            control.addClass('t-error');
            control.next('span.validate-text').text(value.error);
        }
        else {
            control.val(value.value);
            if (value.options) {

            }
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
                console.log (textStatus);
                console.log (errorThrown);
                window.location.reload();
            }
        });
    },

    reloadByTimeout: function () {
        var self = this;
        setTimeout(function () {
            self.next()
        }, 5000);
    },

    next: function () {
        var self = this;
        var data = $j("input.form-control").serialize();
        var actionLink = "/portal/usi/usi:next";
        $j.ajax({
            url: actionLink,
            type: 'post',
            async: false,
            cache: false,
            data: data,
            success: function (data) {
                self.data = data;
                self.showData();
                if (!data.hasErrors)
                    window.location.reload();

                if (data.step == 'wait') {
                    self.reloadByTimeout();
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log (textStatus);
                console.log (errorThrown);
                window.location.reload();
            }
        });
    }
};


$j('document').ready(function () {
    if ($j('#usi-page')) {
        new Usi().initialize();
    }
});

