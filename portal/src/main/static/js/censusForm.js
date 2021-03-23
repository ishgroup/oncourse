import jQuery, * as $ from "./jquery-1.11.2";
//we need add initialise.js code before the code because initialise.js contains common functionality
// for all input controls (like unbind blur and focus functions)
import "./initialise";

var $j = jQuery.noConflict();

var CensusForm = function () {
};
CensusForm.prototype = {
    data: 0,
    step: 0,
    usiEnable: true,

    initialize: function () {
        var self = this;
        this.loadData();
        if (self.data.values['usiStatus'] !== 'DEFAULT_NOT_SUPPLIED') {
            self.locateUsi();
        }
        $j('.form-control[name=usi]').blur(function () {
            var oldUsi = $j.grep(self.data.values, function (obj) {
                return obj.key === "usi";
            })[0].value;
            var newUsi = $j('.form-control[name=usi]').val();
            if (self.data.values['usiStatus'] != 'VERIFIED' &&
                (self.data.step == 'usi' || self.data.step == 'usiFailed' || self.data.step == 'waitLocate') &&
                oldUsi != newUsi) {
                self.verifyUsi();
                self.reloadByTimeout();
            }
        });
    },

    verifyUsi: function () {
      this.step = 'waitVerify';
      this.data.step = 'waitVerify';
			var self = this;
        var actionLink = "/portal/profile.censusform:usiVerify";
        $j('.form-control[name=usi]').attr('disabled', false);
        var data = $j(".form-control[name=usi]").serialize();
        $j('.form-control[name=usi]').attr('disabled', true);
        $j.ajax({
            url: actionLink,
            type: 'post',
            cache: false,
            data: data,
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

    locateUsi: function () {
          this.step = 'waitLocate';
          this.data.step = 'waitLocate';
    			var self = this;
            var actionLink = "/portal/profile.censusform:usiLocate";
            $j('.form-control[name=usi]').attr('disabled', false);
            var data = $j(".form-control[name=usi]").serialize();
            $j('.form-control[name=usi]').attr('disabled', true);
            $j.ajax({
                url: actionLink,
                type: 'post',
                cache: false,
                data: data,
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
    showData: function () {
        var self = this;
        $j('#usiStatusMessage').addClass('hide');


        if (this.data.message) {

            $j('#usiStatusMessage').text(this.data.message).removeClass('hide');
        }

        $j.each(this.data.values, function (index, value) {
            self.fillControl(value);
        });

        if (this.step === 'waitVerify') {
            this.usiEnable = false;
        }
        $j('.form-control[name=usi]').attr('disabled', !this.usiEnable);
    },

    fillUsiStatus: function (value) {
        var text = "not verified";
        $j('#usiControl').removeClass("usi-verified usi-not-verified usi-failed-verify");
        var styleClass = "label-warning";
        this.usiEnable = true;
        switch (value.value) {
            case 'DEFAULT_NOT_SUPPLIED':
                text = "not verified";
                styleClass = "usi-not-verified";
                break;
            case 'NON_VERIFIED':
                text = "verification failed";
                styleClass = "usi-failed-verify";
                break;
            case 'VERIFIED':
                text = "verified";
                styleClass = "usi-verified";
                this.usiEnable = false;
                break;
        }
        $j("#usiStatus").text(text);
        $j('#usiControl').addClass(styleClass);
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
