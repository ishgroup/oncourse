var $j = jQuery.noConflict();


PaymentForm = function () {
};


var Status = {
    READY_TO_PROCESS: 'READY_TO_PROCESS',
    FILL_CC_DETAILS: 'FILL_CC_DETAILS',
    CHOOSE_ABANDON_OTHER: 'CHOOSE_ABANDON_OTHER',
    DPS_PROCESSING: 'DPS_PROCESSING',
    FAILED: 'FAILED',
    SUCCESS: 'SUCCESS',
    DPS_ERROR: 'DPS_ERROR',
    ERROR: 'ERROR',
    WARNING: 'WARNING'
};

var Action = {
    MAKE_PAYMENT: 'MAKE_PAYMENT',
    CANCEL: 'CANCEL',
    TRY_OTHER: 'TRY_OTHER'
};

PaymentForm.prototype = {
    request: {
        sessionId: null,
        name: null,
        number: null,
        cvv: null,
        month: null,
        year: null,
        action: null
    },

    response: {
        cardNameError: null,
        cardNumberError: null,
        cardCVVError: null,
        cardExpiryDateError: null,
        status: null,
        paymentId: null
    },

    inputNumber: null,
    inputName: null,
    inputCvv: null,
    inputMonth: null,
    inputYear: null,
    
    errorIcon: '<img class="t-error-icon" alt="" src="/services/assets/development/tapestry/spacer.gif">',
    progress: '<div class="pay-process"> <h2 class="page-title">Payment is now being processed against the bank. Please wait...</h2> <p>This page will update automatically</p></div>',

    init: function () {
        var self = this;

        $j('#pay-button').on('click', function () {
            self.makePayment()
        });

        $j('#cancel').on('click', function () {
            self.cancelPayment()
        });
        
        this.initHandlers();

        this.request.sessionId =  window.location.pathname.split('/')[3];

        this.inputNumber = $j("div#paymentDetailsForm input[name='creditCardNumber']");
        this.inputNumber.inputmask("9999 9999 9999 9999", { "placeholder": " " });

        this.inputName  = $j("div#paymentDetailsForm input[name='creditCardName']");
        this.inputCvv  = $j("div#paymentDetailsForm input[name='creditCardCVV']");
        this.inputMonth  = $j("div#paymentDetailsForm select[name='expiryMonth']");
        this.inputYear  = $j("div#paymentDetailsForm select[name='expiryYear']");

        this.request = $j.extend(true, {}, this.request);
    },

    makePayment: function () {
        this.request.name = this.inputName.val();
        this.request.number = this.inputNumber.inputmask('unmaskedvalue');
        this.request.month = this.inputMonth.val();
        this.request.year = this.inputYear.val();
        this.request.cvv = this.inputCvv.inputmask('unmaskedvalue');
        this.request.action = Action.MAKE_PAYMENT;

        this.cleanValidation();
        this.hidePaymentForm();
        this.showProgress();
        this.sendRequest();
    },

    cancelPayment: function () {
        this.request.action = Action.CANCEL;
        $j(".pay-form").empty();
        this.showProgress();
        this.sendRequest();
    },

    tryOtherCard: function () {
        this.request.action = Action.TRY_OTHER;
        $j(".pay-form").empty();
        this.showProgress();
        this.sendRequest();
    },


    sendRequest: function () {
        var self = this;

        $j.ajax({
            type: 'POST',
            url: '/services/payment:processAction',
            async: true,
            cache: false,
            data: JSON.stringify(this.request),
            contentType: 'application/json',
            processData: false,
            success: function (data) {
                self.removeProgress();
                self.response = data;
                switch (self.response.status) {
                    case Status.WARNING:
                        self.showPaymentForm();
                        self.processWarning();
                        break;
                    case Status.FAILED:
                    case Status.ERROR:
                    case Status.DPS_ERROR:
                    case Status.SUCCESS:
                    case Status.CHOOSE_ABANDON_OTHER:
                        self.removePaymentForm();
                        self.refreshResult();
                        self.initHandlers();
                        break;
                    case Status.READY_TO_PROCESS:
                    case Status.FILL_CC_DETAILS:
                        location.reload();
                        break;
                    default:
                        location.reload();
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                console.log(errorThrown);
                location.reload();
            }
        });
    },

    refreshResult: function () {
        var self = this;

        $j.ajax({
            type: 'POST',
            url: '/services/payment:getResultBlock/' + self.request.sessionId,
            async: false,
            cache: false,
            success: function (data) {
                $j(data.content).appendTo($j(".pay-form"))

            },
            error: function (jqXHR, textStatus, errorThrown) {
                location.reload();
            }
        });
    },
    


    processWarning: function () {
        if (this.response.cardNameError != null) {
           $j("#name-error").text(this.response.cardNameError);
           $j(this.inputName).after(this.errorIcon)
        }
        if (this.response.cardNumberError != null) {
            $j("#number-error").text(this.response.cardNumberError);
            $j(this.inputNumber).after(this.errorIcon)
        }
        if (this.response.cardCVVError != null) {
            $j("#cvv-error").text(this.response.cardCVVError);
            $j(this.inputCvv).after(this.errorIcon)
        }
        if (this.response.cardExpiryDateError != null) {
            $j("#date-error").text(this.response.cardExpiryDateError);
            $j(this.inputMonth).after(this.errorIcon)
            $j(this.inputYear).after(this.errorIcon)
        }   
    },
    
    cleanValidation: function () {
        $j("#name-error").text('');
        $j("#number-error").text('');
        $j("#cvv-error").text('');
        $j("#date-error").text('');
        $j(".t-error-icon").remove();
    },

    hidePaymentForm: function () {
        if (!$j("#paymentDetailsForm").hasClass('hide')) {
            $j("#paymentDetailsForm").addClass('hide');
        }
    },

    showPaymentForm: function () {
        if ($j("#paymentDetailsForm").hasClass('hide')) {
            $j("#paymentDetailsForm").removeClass('hide');
        }
    },

    removePaymentForm: function () {
        if ($j("#paymentDetailsForm").length > 0) {
            $j("#paymentDetailsForm").remove()
        }
    },

    initHandlers: function () {
        var self = this;
        if ($j("#pay-retry").length > 0) {
            $j("#pay-retry").on('click', function () {
                self.tryOtherCard()
            });
        }
        if ($j("#pay-abandon").length > 0) {
            $j("#pay-abandon").on('click', function () {
                self.cancelPayment()
            });
        }
    },

    showProgress: function () {
        if ($j(".pay-process").length == 0) {
            $j(this.progress).appendTo($j(".pay-form"))
        }
    },

    removeProgress: function () {
        if ($j(".pay-process").length > 0) {
            $j(".pay-process").remove();
        }
    }
};

$j(document).ready(function() {
    if($j('.pay-form').length > 0) {
        var form = new PaymentForm();
        form.init();
    }
});
