goog.provide('CensusForm');
goog.require('jquery');

var $j = jQuery.noConflict();

CensusForm = function () {
};
CensusForm.prototype = {
    data: 0,

    initialize: function () {
        var self = this;
        this.loadData();
        $j('.form-control[name=usi]').blur(function () {
            var oldUsi = $j.grep(self.data.values, function(obj) {
                return obj.key === "usi";
            })[0].value;
            var newUsi = $j('.form-control[name=usi]').val();
            if (self.data.values['usiStatus'] != 'VERIFIED' &&
                self.data.step == 'step1' &&
                oldUsi != newUsi)
            {
                self.verifyUsi();
            }
        });
    },

    verifyUsi: function () {
        var self = this;

        var actionLink = "/portal/profile.censusform:usiVerify";
        $j('.form-control[name=usi]').attr('disabled', false);
        var data = $j(".form-control[name=usi]").serialize();
        $j('.form-control[name=usi]').attr('disabled', true);
        $j.ajax({
            url: actionLink,
            type: 'post',
            async: false,
            cache: false,
            data: data,
            success: function (data) {
                self.data = data;
                self.showData();
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
            self.verifyUsi();
        }, 5000);
    },

    loadData: function () {
        var self = this;

        var actionLink = "/portal/profile.censusform:value";
        $j.ajax({
            url: actionLink,
            type: 'post',
            async: false,
            cache: false,
            success: function (data) {
                self.data = data;
                self.showData();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(textStatus);
                console.log(errorThrown);
                window.location.reload();
            }
        });
    },
    showData: function () {
        var self = this;
        $j('#usiStatusMessage').addClass('hide');


        if (this.data.message) {

            $j('#usiStatusMessage').text(this.data.message).removeClass('hide');
        }

        $j.each(this.data.values, function (index, value) {
            self.fillControl(value);
        });


        if (this.data.step == 'wait') {
            $j('.form-control[name=usi]').attr('disabled', true);
            self.reloadByTimeout();
        }
    },

    fillUsiStatus: function (value) {
        var text = "not verified";
        $j('#usiStatus').removeClass("bs-callout-warning bs-callout-error bs-callout-success");
        var styleClass = "bs-callout-warning";
        var disabled = false;
        switch (value.value) {
            case 'DEFAULT_NOT_SUPPLIED':
                text = "not verified";
                styleClass = "bs-callout-warning";
                disabled = false;
                break;
            case 'NON_VERIFIED':
                text = "verification failed";
                styleClass = "bs-callout-error";
                disabled = false;
                break;
            case 'VERIFIED':
                text = "verified";
                styleClass = "bs-callout-success";
                disabled = true;
                break;
        }
        $j("#usiStatus").text(text);
        $j('#usiStatus').addClass(styleClass);
        $j('.form-control[name=usi]').attr('disabled', disabled);
    },

    fillControl: function (value) {
        var control = $j('.form-control[name=' + value.key + ']');
        control.val(value.value);
        if (value.key == "usiStatus") {
            this.fillUsiStatus(value);
        }
    }
};

$j('document').ready(function () {
    if ($j('[id^=censusForm]').length != 0) {
        new CensusForm().initialize();
    }
});