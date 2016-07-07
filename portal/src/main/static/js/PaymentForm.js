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
    someBodyElseAlreadyPaidThisInvoice: "someBodyElseAlreadyPaidThisInvoice",
    thereIsProcessedPayment: "thereIsProcessedPayment",
    thereAreNotUnbalancedInvoices: "thereAreNotUnbalancedInvoices",
    invalidCardNumber: "invalidCardNumber",
    invalidCardName: "invalidCardName",
    invalidCardCvv: "invalidCardCvv",
    invalidCardDate: "invalidCardDate"
};

var PaymentStatus = {
    IN_TRANSACTION: 'IN_TRANSACTION',
    FAILED: 'FAILED',
    FAILED_CARD_DECLINED: 'FAILED_CARD_DECLINED',
    SUCCESS: 'SUCCESS'
};

PaymentForm = function () {
};

PaymentForm.prototype = {
    divNextPaymentAmountTemplate: Handlebars.compile("Next amount due is <span id='next-payment-amount'>{{amount}}</span> on <span id='next-payment-due-date'>{{dateDue}}</span>"),
    divNextPaymentMessageTemplate: Handlebars.compile("Next amount due is <span id='next-payment-amount'>{{amount}}</span> on <span id='next-payment-due-date'>{{dateDue}}</span>"),
    thereIsProcessedPaymentTemplate: Handlebars.compile("<span>You have a processed payment. You should finish this porcess before next payment</span>"),
    someBodyElseAlreadyPaidThisInvoiceTemplate: Handlebars.compile("<span>Your invoice what you have tried to pay for is already paid</span>"),
    paymentSuccessfulTemplate: Handlebars.compile("<span>Your payment was successful</span>"),
    paymentFailedTemplate: Handlebars.compile("<span>Your payment was failed</span>"),
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
            this.divPaymentMessage.html(this.divNextPaymentAmountTemplate({
                amount: (this.response.amount).toLocaleString("en-Au", {
                    style: "currency",
                    currency: "AUD",
                    minimumFractionDigits: 2
                }),
                dateDue: moment(this.response.dateDue).format('D MMMM YYYY')
            }));
            if (this.response.dateDue < Date.now()) {
                this.divPaymentMessage.addClass('warning-msg').append(' was overdue');
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
        this.inputNumber.inputmask("9999 9999 9999 9999", {placeholder: " "});
    },

    initInputName: function () {
        this.inputName = $j("div#payment-controls input[name='name']");
    },

    initInputCvv: function () {
        this.inputCvv = $j("div#payment-controls input[name='cvv']");
        this.inputCvv.inputmask("9999", {placeholder: " "});
    },

    initInputDate: function () {
        this.inputDate = $j("div#payment-controls input[name='date']");
        this.inputDate.inputmask("m/y", {placeholder: ' '});
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
            case WarningMessage.thereIsProcessedPayment:
                this.divPaymentMessage.html(this.thereIsProcessedPaymentTemplate());
                this.hidePaymentControls();
                break;
            case WarningMessage.someBodyElseAlreadyPaidThisInvoice:
                this.divPaymentMessage.html(this.someBodyElseAlreadyPaidThisInvoiceTemplate());
                this.hidePaymentControls();
                break;
            default:
                this.paymentForm.hide();
        }
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
                    this.divPaymentMessage.html(this.paymentSuccessfulTemplate())
                } else {
                    this.divPaymentMessage.html(this.paymentFailedTemplate())
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
    }
};

jQuery(document).ready(
    function () {
        if ( $j('div#payment-form').length > 0) {
            var form = new PaymentForm();
            form.init();
            form.sendRequest(false);
        }
    });