goog.provide('PaymentForm');

goog.require('jquery');
goog.require('jquery.inputmask');
goog.require('moment');
goog.require('handlebars');

var $j = jQuery.noConflict();

var Action = {
    init: 'init',
    make: 'make',
    update: 'update'
};


var Status = {
    card: 'card',
    wait: 'wait',
    result: 'result'
};

var WarningMessage = {
    thisInvoiceAlreadyPaid: "thisInvoiceAlreadyPaid",
    thereIsPaymentInTransaction: "thereIsPaymentInTransaction",
    thereAreNotUnbalancedInvoices: "thereAreNotUnbalancedInvoices",
    invalidCardNumber: "invalidCardNumber",
    invalidCardName: "invalidCardName",
    invalidCardCvv: "invalidCardCvv",
    invalidCardDate: "invalidCardDate",
    amountLessThan20Dollars: "amountLessThan20Dollars",
    amountLessThanOwing: "amountLessThanOwing",
    amountMoreThanOwing: "amountMoreThanOwing",
    amountWrong: "amountWrong"
};

var PaymentStatus = {
    IN_TRANSACTION: 'IN_TRANSACTION',
    FAILED: 'FAILED',
    FAILED_CARD_DECLINED: 'FAILED_CARD_DECLINED',
    SUCCESS: 'SUCCESS'
};

var Message = {
    overduePaymentAmount: Handlebars.compile("Overdue: <span id='next-payment-amount'>{{amount}}</span> on <span id='next-payment-due-date'>{{dateDue}}</span>"),
    nextPaymentAmount: Handlebars.compile("Next amount due is <span id='next-payment-amount'>{{amount}}</span> on <span id='next-payment-due-date'>{{dateDue}}</span>"),
    thisInvoiceAlreadyPaid: Handlebars.compile("<span>Your invoice what you have tried to pay for is already paid</span>"),
    thereIsPaymentInTransaction: Handlebars.compile("<span>You have a payment already being processed by the system. You should finish this process before next payment</span>"),
    invalidCreditCard: Handlebars.compile("<span><h2>Your payment failed</h2> " +
        "<p>Please check your credit card details or credit balance and try again. In particular, check the CVV and expiry date have been entered correctly.</p></span>"),
    paymentSuccessful: Handlebars.compile("<span>" +
        " <h2>Payment <span>&#187;</span> Successful</h2>" +
        " <p>Your payment was <strong>SUCCESSFUL</strong> and recorded in our system against reference number <strong>W{{paymentId}}</strong>.</p>" +
        " </span>"),
    paymentFailed: Handlebars.compile("<span><h2>Your payment failed</h2> " +
        "<p>Please check your credit card details or credit balance and try again. In particular, check the CVV and expiry date have been entered correctly.</p></span>"),
    waitResult: Handlebars.compile("<span>Payment is now being processed against the bank. Please wait...</span>"),
    amountLessThan20Dollars:  Handlebars.compile("<span>The minimum amount for a payment is $20. Please correct it and try again. </span>"),
    amountLessThanOwing: Handlebars.compile("<span>Payment amount can't be less than total owing for invoice. Please correct it and try again. </span>"),
    amountMoreThanOwing: Handlebars.compile("<span>Payment amount can't be greater than total owing for invoice. Please correct it and try again. </span>"),
    amountWrong: Handlebars.compile("<span>Payment amount is wrong. Please correct it and try again. </span>")


};

PaymentForm = function () {
};

PaymentForm.prototype = {
    request: {
        paymentInId: null,
        invoiceId: null,
        action: null,
        card: {
            name: null,
            number: null,
            cvv: null,
            date: null,
            amount: null
        }
    },
    response: {
        amount: null,
        dateDue: null,
        invoiceId: null,
        paymentId: null,
        paymentStatus: null,
        status: null,
        validationResult: {
            warning: null,
            error: null
        }
    },
    divPaymentMessage: null,
    inputAmount: null,
    inputNumber: null,
    inputName: null,
    inputCvv: null,
    inputDate: null,
    buttonPay: null,
    paymentForm: null,
    paymentControls: null,
    divPaymentProgressBar: null,

    makePayment: function () {
        this.hidePaymentControls();
        this.divPaymentProgressBar.show();
        this.setMessage(Message.waitResult());

        this.request.invoiceId = this.response.invoiceId;
        this.request.paymentInId = this.response.paymentInId;
        this.request.action = Action.make;
        this.request.card.name = this.inputName.val();
        this.request.card.number = this.inputNumber.inputmask('unmaskedvalue');
        this.request.card.date = this.inputDate.val();
        this.request.card.cvv = this.inputCvv.inputmask('unmaskedvalue');
        this.request.card.amount = this.inputAmount.inputmask('unmaskedvalue');

        this.sendRequest(true);
    },

    setMessage: function (html, cssClass) {
        this.divPaymentMessage.html(html);
        this.divPaymentMessage.removeClass();
        if (cssClass) {
            this.divPaymentMessage.addClass(cssClass);
        }
    },

    hidePaymentControls: function () {
        $j("input").prop('disabled', true);
        this.paymentControls.hide();
        this.buttonPay.parent().hide();
    },

    showPaymentControls: function () {
        $j("input").removeAttr('disabled');
        this.paymentControls.show();
        this.buttonPay.parent().show();
    },

    fillDivNextPayment: function () {
        if (this.response.amount > 0.01) {
            var context = {
                amount: (this.response.amount).toLocaleString("en-Au", {
                    style: "currency",
                    currency: "AUD",
                    minimumFractionDigits: 2
                }),
                    dateDue: moment(this.response.dateDue).format('D MMMM YYYY')
            };
            if (this.response.dateDue < Date.now()) {
                this.divPaymentMessage.addClass('warning-msg');
                this.divPaymentMessage.html(Message.overduePaymentAmount(context));
            } else {
                this.divPaymentMessage.html(Message.nextPaymentAmount(context));
            }
            this.divPaymentMessage.show();
        } else {
            this.divPaymentMessage.html('');
            this.divPaymentMessage.hide();
        }
    },

    initInputAmount: function () {
        this.inputAmount = $j("div#payment-controls input[name='amount']");
        this.inputAmount.inputmask("currency", {
            groupSeparator: ',',
            placeholder: '0.00',
            numericInput: false,
            autoGroup: true
        });
    },

    initInputNumber: function () {
        this.inputNumber = $j("div#payment-controls input[name='number']");
        this.inputNumber.inputmask("9999 9999 9999 9999", { placeholder: " ", showMaskOnHover: false, showMaskOnFocus: false });
    },

    initInputName: function () {
        this.inputName = $j("div#payment-controls input[name='name']");
    },

    initInputCvv: function () {
        this.inputCvv = $j("div#payment-controls input[name='cvv']");
        this.inputCvv.inputmask("9999", { placeholder: " ", showMaskOnHover: false, showMaskOnFocus: false });
    },

    initInputDate: function () {
        this.inputDate = $j("div#payment-controls input[name='date']");
        this.inputDate.inputmask("m/y", { placeholder: " ", showMaskOnHover: false, showMaskOnFocus: false });
    },

    fillCardDetails: function () {
        this.inputAmount.val(this.request.card.amount);
        this.inputName.val(this.request.card.name);
        this.inputNumber.val(this.request.card.number);
        this.inputCvv.val(this.request.card.cvv);
        this.inputDate.val(this.request.card.date ? moment(this.request.card.date).format('MM/YYYY') : '');

        this.fillDivNextPayment();
    },

    init: function () {
        var self = this;
        $j("input").removeAttr('disabled');

        this.buttonPay = $j('#pay-button');

        this.buttonPay.on('click', function () {
            self.makePayment()
        });

        this.paymentForm = $j('div#payment-form');
        this.paymentControls = $j('div#payment-controls');
        this.divPaymentMessage = $j('div#payment-message');
        this.divPaymentProgressBar = $j('#payment-progress-bar');
        this.divPaymentProgressBar.hide();

        this.request = $j.extend(true, {}, this.request);
        this.request.action = Action.init;

        this.initInputAmount();
        this.initInputName();
        this.initInputNumber();
        this.initInputCvv();
        this.initInputDate();
    },

    sendRequest: function (async) {
        var self = this;
        $j.ajax({
            type: 'POST',
            url: '/portal/history.finance.paymentform:process',
            async: async,
            cache: false,
            data: JSON.stringify(this.request),
            contentType: 'application/json',
            processData: false,
            success: function (data) {
                self.response = data;
                self.processResponse()

            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                console.log(errorThrown);
                self.processError();
            }
        });
    },

    processError: function () {
        console.log(this.response.validationResult.error);
        this.paymentForm.hide()
    },

    processWarning: function () {
        switch (this.response.validationResult.warning) {
            case WarningMessage.thereAreNotUnbalancedInvoices:
                this.paymentForm.hide();
                break;
            case WarningMessage.thereIsPaymentInTransaction:
                this.setMessage(Message.thereIsPaymentInTransaction(), 'warning-msg');
                this.hidePaymentControls();
                break;
            case WarningMessage.thisInvoiceAlreadyPaid:
                this.setMessage(Message.thisInvoiceAlreadyPaid(), 'warning-msg');
                this.hidePaymentControls();
                break;
            case WarningMessage.amountLessThan20Dollars:
                this.showWarning(Message.amountLessThan20Dollars());
                break;
            case WarningMessage.amountLessThanOwing:
                this.showWarning(Message.amountLessThanOwing());
                break;
            case WarningMessage.amountMoreThanOwing:
                this.showWarning(Message.amountMoreThanOwing());
                break;
            case WarningMessage.amountWrong:
                this.showWarning(Message.amountWrong());
                break;
            case WarningMessage.invalidCardName:
            case WarningMessage.invalidCardNumber:
            case WarningMessage.invalidCardCvv:
            case WarningMessage.invalidCardDate:
                this.showWarning(Message.invalidCreditCard())
                break;
            default:
                this.paymentForm.hide();
        }
    },

    showWarning: function(message) {
        this.setMessage(message, 'warning-msg');
        this.hidePaymentControls();
        this.divPaymentProgressBar.hide();
        this.reloadPage(10000);
    },

    updateView: function () {
        this.paymentForm.show();
        switch (this.response.status) {
            case Status.card:
                this.request.card.amount = this.response.amount;
                this.showPaymentControls();
                this.divPaymentProgressBar.hide();
                this.fillCardDetails();
                break;
            case Status.wait:
                this.hidePaymentControls();
                this.divPaymentProgressBar.show();
                break;
            case Status.result:
                this.hidePaymentControls();
                this.divPaymentProgressBar.hide();
                if (this.response.paymentStatus == PaymentStatus.SUCCESS) {
                    this.setMessage(Message.paymentSuccessful({paymentId: this.response.paymentId}));
                } else {
                    this.setMessage(Message.paymentFailed(), 'warning-msg');
                    this.reloadPage(15000);
                }
                break;
        }
    },

    processResponse: function () {
        if (this.response.validationResult.error) {
            this.processError();
        } else {
            if (this.response.validationResult.warning) {
                this.processWarning();
            } else {
                this.updateView();
            }
        }
    },

    reloadPage: function(delay) {
        setTimeout(function () {
            window.location.reload();
        }, delay);
    }
};

jQuery(document).ready(
    function () {
        if ($j('div#payment-form').length > 0) {
            var form = new PaymentForm();
            form.init();
            form.sendRequest(false);
        }
    });