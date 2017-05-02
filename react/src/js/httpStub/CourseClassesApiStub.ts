import {DefaultHttpService, HttpService} from "../common/services/HttpService";
import { CourseClass } from "../model/CourseClass";
import { CourseClassesParams } from "../model/CourseClassesParams";
import { ModelError } from "../model/ModelError";
import {CourseClassesApi} from "../http/CourseClassesApi";

export class CourseClassesApiStub extends CourseClassesApi {

  getCourseClasses(courseClassesParams: CourseClassesParams): Promise<CourseClass[]> {
    return Promise.resolve([{
      "id": "5034198",
      "course": {"id": "5004600", "code": null, "name": "Biotics", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": false,
      "availableEnrolmentPlaces": 0,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "253.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "227.70",
          "discountValue": "25.30",
          "title": "10% COS discount"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "227.70",
          "discountValue": null,
          "title": "Old Salt Discount"
        }
        ],
        "hasTax": true
      }
    }, {
      "id": "5034392",
      "course": {"id": "5004619", "code": null, "name": "Template Course", "description": null},
      "code": null,
      "start": "2014-11-19T22:00:00Z",
      "end": "2017-12-28T23:00:00Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 993,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "0.00",
        "feeOverriden": null,
        "appliedDiscount": null,
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "0.00",
          "discountValue": null,
          "title": "Old Salt Discount"
        }
        ],
        "hasTax": false
      }
    }, {
      "id": "5034434",
      "course": {"id": "5004600", "code": null, "name": "Biotics", "description": null},
      "code": null,
      "start": "2015-02-11T09:32:09Z",
      "end": "2031-09-11T10:32:09Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 961,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {"fee": "10.00", "feeOverriden": null, "appliedDiscount": null, "possibleDiscounts": [], "hasTax": true}
    }, {
      "id": "5034534",
      "course": {"id": "5004659", "code": null, "name": "Dog Training Course", "description": null},
      "code": null,
      "start": "2015-07-29T11:35:00Z",
      "end": "2017-07-29T12:35:00Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 986,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "111.00",
          "discountValue": "-110.00",
          "title": "Negative Discount"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }, {
      "id": "5037232",
      "course": {"id": "5004706", "code": null, "name": "Massive VET Course", "description": null},
      "code": null,
      "start": "2016-06-01T03:00:00Z",
      "end": "2017-07-04T04:00:00Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 67,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "450.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "330.00",
          "discountValue": "120.00",
          "title": "$300 Discount"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }, {
      "id": "5037236",
      "course": {"id": "5004706", "code": null, "name": "Massive VET Course", "description": null},
      "code": null,
      "start": "2016-07-01T10:54:37Z",
      "end": "2018-07-01T15:29:00Z",
      "hasAvailablePlaces": false,
      "availableEnrolmentPlaces": 0,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "450.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "330.00",
          "discountValue": "120.00",
          "title": "$300 Discount"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }, {
      "id": "5037292",
      "course": {"id": "5004633", "code": null, "name": "100$ Course 1", "description": null},
      "code": null,
      "start": "2016-03-16T07:12:19Z",
      "end": "2017-05-05T08:12:19Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 85,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "25.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "300.00",
          "discountValue": "-275.00",
          "title": "$300 Discount"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "37.50",
          "discountValue": null,
          "title": "Concession Discount 1"
        }
        ],
        "hasTax": false
      }
    }, {
      "id": "5037296",
      "course": {"id": "5004600", "code": null, "name": "Biotics", "description": null},
      "code": null,
      "start": "2017-05-01T08:34:31Z",
      "end": "2017-05-01T09:34:31Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 95,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "50.00",
        "feeOverriden": null,
        "appliedDiscount": null,
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "44.99",
          "discountValue": null,
          "title": "Old Salt Discount"
        }
        ],
        "hasTax": true
      }
    }, {
      "id": "5037347",
      "course": {"id": "5004714", "code": null, "name": "Portal Curse", "description": null},
      "code": null,
      "start": "2011-08-01T10:00:54Z",
      "end": "2020-09-03T11:00:54Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 59,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "104.76",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "102.15",
          "discountValue": "2.61",
          "title": "Auto Discounterino"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "94.29",
          "discountValue": null,
          "title": "Old Salt Discount"
        }
        ],
        "hasTax": true
      }
    }, {
      "id": "5037349",
      "course": {"id": "5004722", "code": null, "name": "Certificated Course 2 (Qual)", "description": null},
      "code": null,
      "start": "2016-10-15T08:32:43Z",
      "end": "2018-10-28T09:32:43Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 85,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "100.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "97.00",
          "discountValue": "3.00",
          "title": "Glo.Bal Discounterino 3%"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }, {
      "id": "5037350",
      "course": {"id": "5004706", "code": null, "name": "Massive VET Course", "description": null},
      "code": null,
      "start": "2017-05-10T03:00:00Z",
      "end": "2018-06-12T04:00:00Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 77,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "250.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "330.00",
          "discountValue": "-80.00",
          "title": "$300 Discount"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }, {
      "id": "5037368",
      "course": {"id": "5004722", "code": null, "name": "Certificated Course 2 (Qual)", "description": null},
      "code": null,
      "start": "2016-12-27T08:32:43Z",
      "end": "2017-09-17T10:32:43Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 97,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "100.00",
        "feeOverriden": null,
        "appliedDiscount": null,
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "90.00",
          "discountValue": null,
          "title": "Old Salt Discount"
        }
        ],
        "hasTax": true
      }
    }, {
      "id": "5038308",
      "course": {"id": "5004706", "code": null, "name": "Massive VET Course", "description": null},
      "code": null,
      "start": "2017-04-27T10:54:37Z",
      "end": "2019-04-27T15:29:00Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 3,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "450.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "330.00",
          "discountValue": "120.00",
          "title": "$300 Discount"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }, {
      "id": "5038311",
      "course": {"id": "5004619", "code": null, "name": "Template Course", "description": null},
      "code": null,
      "start": "2015-09-15T23:00:00Z",
      "end": "2018-10-24T23:00:00Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 999,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "0.00",
        "feeOverriden": null,
        "appliedDiscount": null,
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "0.00",
          "discountValue": null,
          "title": "Old Salt Discount"
        }
        ],
        "hasTax": false
      }
    }, {
      "id": "5038314",
      "course": {"id": "5004623", "code": null, "name": "Year Course", "description": null},
      "code": null,
      "start": "2015-10-16T12:48:09Z",
      "end": "2017-10-05T13:48:09Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 17,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "363.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "326.70",
          "discountValue": "36.30",
          "title": "10% COS discount"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }, {
      "id": "5038320",
      "course": {"id": "5004605", "code": null, "name": "World Saving", "description": null},
      "code": null,
      "start": "2015-10-16T15:23:00Z",
      "end": "2017-08-30T16:23:00Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 999,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "0.00",
        "feeOverriden": null,
        "appliedDiscount": null,
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "0.00",
          "discountValue": null,
          "title": "Old Salt Discount"
        }
        ],
        "hasTax": false
      }
    }, {
      "id": "5038325",
      "course": {"id": "5004600", "code": null, "name": "Biotics", "description": null},
      "code": null,
      "start": "2018-02-25T07:34:31Z",
      "end": "2018-02-25T08:34:31Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "50.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "160.00",
          "discountValue": "-110.00",
          "title": "Negative Discount"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }, {
      "id": "5038326",
      "course": {"id": "5004722", "code": null, "name": "Certificated Course 2 (Qual)", "description": null},
      "code": null,
      "start": "2016-01-31T08:32:43Z",
      "end": "2017-08-24T10:32:43Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 99,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "100.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "97.00",
          "discountValue": "3.00",
          "title": "Glo.Bal Discounterino 3%"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }, {
      "id": "5038331",
      "course": {"id": "5004714", "code": null, "name": "Portal Curse", "description": null},
      "code": null,
      "start": "2012-05-27T10:00:54Z",
      "end": "2021-06-30T11:00:54Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 96,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "104.76",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "101.62",
          "discountValue": "3.14",
          "title": "Glo.Bal Discounterino 3%"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "0.00",
          "discountValue": null,
          "title": "Free!"
        }, {
          "id": null,
          "expiryDate": null,
          "discountedFee": "94.29",
          "discountValue": null,
          "title": "Old Salt Discount"
        }
        ],
        "hasTax": true
      }
    }, {
      "id": "5038333",
      "course": {"id": "5004722", "code": null, "name": "Certificated Course 2 (Qual)", "description": null},
      "code": null,
      "start": "2017-10-23T08:32:43Z",
      "end": "2019-05-16T10:32:43Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "100.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "97.00",
          "discountValue": "3.00",
          "title": "Glo.Bal Discounterino 3%"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }, {
      "id": "5038337",
      "course": {"id": "5004633", "code": null, "name": "100$ Course 1", "description": null},
      "code": null,
      "start": "2017-01-10T07:12:19Z",
      "end": "2018-03-01T07:12:19Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "25.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "300.00",
          "discountValue": "-275.00",
          "title": "$300 Discount"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "22.50",
          "discountValue": null,
          "title": "Old Salt Discount"
        }
        ],
        "hasTax": false
      }
    }, {
      "id": "5038340",
      "course": {"id": "5004706", "code": null, "name": "Massive VET Course", "description": null},
      "code": null,
      "start": "2017-03-28T02:00:00Z",
      "end": "2018-04-30T04:00:00Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "250.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "330.00",
          "discountValue": "-80.00",
          "title": "$300 Discount"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }, {
      "id": "5038344",
      "course": {"id": "5004706", "code": null, "name": "Massive VET Course", "description": null},
      "code": null,
      "start": "2018-03-06T02:00:00Z",
      "end": "2019-04-08T04:00:00Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "250.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "330.00",
          "discountValue": "-80.00",
          "title": "$300 Discount"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }, {
      "id": "5038346",
      "course": {"id": "5004659", "code": null, "name": "Dog Training Course", "description": null},
      "code": null,
      "start": "2016-05-24T11:35:00Z",
      "end": "2018-05-25T12:35:00Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 995,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {"fee": "0.00", "feeOverriden": null, "appliedDiscount": null, "possibleDiscounts": [], "hasTax": false}
    }, {
      "id": "5038417",
      "course": {"id": "5004633", "code": null, "name": "100$ Course 1", "description": null},
      "code": null,
      "start": "2017-10-27T07:12:19Z",
      "end": "2018-12-01T08:12:00Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "25.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "300.00",
          "discountValue": "-275.00",
          "title": "$300 Discount"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "22.50",
          "discountValue": null,
          "title": "Old Salt Discount"
        }, {
          "id": null,
          "expiryDate": null,
          "discountedFee": "40.00",
          "discountValue": null,
          "title": "Concession Discount 2"
        }
        ],
        "hasTax": false
      }
    }, {
      "id": "5038421",
      "course": {"id": "5004706", "code": null, "name": "Massive VET Course", "description": null},
      "code": null,
      "start": "2018-04-15T22:00:00Z",
      "end": "2018-07-27T05:00:00Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "500.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "485.00",
          "discountValue": "15.00",
          "title": "Glo.Bal Discounterino 3%"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }, {
      "id": "5038423",
      "course": {"id": "5004600", "code": null, "name": "Biotics", "description": null},
      "code": null,
      "start": "2018-01-07T13:06:49Z",
      "end": "2018-03-06T14:06:49Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 27,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {"fee": "450.00", "feeOverriden": null, "appliedDiscount": null, "possibleDiscounts": [], "hasTax": true}
    }, {
      "id": "5038424",
      "course": {"id": "5004633", "code": null, "name": "100$ Course 1", "description": null},
      "code": null,
      "start": "2017-11-16T07:12:19Z",
      "end": "2018-03-20T08:12:19Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 28,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "100.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "330.00",
          "discountValue": "-230.00",
          "title": "$300 Discount"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }, {
      "id": "5038427",
      "course": {"id": "5004605", "code": null, "name": "World Saving", "description": null},
      "code": null,
      "start": "2017-03-11T11:33:34Z",
      "end": "2018-03-11T12:33:34Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 4,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "110.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "106.70",
          "discountValue": "3.30",
          "title": "Glo.Bal Discounterino 3%"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }, {
      "id": "5038450",
      "course": {"id": "5004600", "code": null, "name": "Biotics", "description": null},
      "code": null,
      "start": "2017-09-13T16:26:46Z",
      "end": "2017-11-01T16:26:46Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 19,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1730.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "1678.11",
          "discountValue": "51.89",
          "title": "Glo.Bal Discounterino 3%"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }, {
      "id": "5038465",
      "course": {"id": "5004623", "code": null, "name": "Year Course", "description": null},
      "code": null,
      "start": "2017-02-27T13:21:16Z",
      "end": "2017-06-06T15:21:16Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 27,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "385.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "373.45",
          "discountValue": "11.55",
          "title": "Glo.Bal Discounterino 3%"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }, {
      "id": "5038468",
      "course": {"id": "5004633", "code": null, "name": "100$ Course 1", "description": null},
      "code": null,
      "start": "2017-10-17T07:12:19Z",
      "end": "2018-11-21T08:12:00Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 99,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "25.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "300.00",
          "discountValue": "-275.00",
          "title": "$300 Discount"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "22.50",
          "discountValue": null,
          "title": "Old Salt Discount"
        }
        ],
        "hasTax": false
      }
    }, {
      "id": "5038475",
      "course": {"id": "5004706", "code": null, "name": "Massive VET Course", "description": null},
      "code": null,
      "start": "2018-08-10T09:51:35Z",
      "end": "2018-08-10T10:51:35Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 1,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {"fee": "0.00", "feeOverriden": null, "appliedDiscount": null, "possibleDiscounts": [], "hasTax": false}
    }, {
      "id": "5038492",
      "course": {"id": "5004600", "code": null, "name": "Biotics", "description": null},
      "code": null,
      "start": "2016-10-20T10:15:25Z",
      "end": "2018-01-20T11:15:25Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 995,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {"fee": "15.00", "feeOverriden": null, "appliedDiscount": null, "possibleDiscounts": [], "hasTax": true}
    }, {
      "id": "5038552",
      "course": {"id": "5004722", "code": null, "name": "Certificated Course 2 (Qual)", "description": null},
      "code": null,
      "start": "2017-04-01T12:00:25Z",
      "end": "2017-06-17T13:00:25Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 98,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {"fee": "0.00", "feeOverriden": null, "appliedDiscount": null, "possibleDiscounts": [], "hasTax": false}
    }, {
      "id": "5038553",
      "course": {"id": "5004722", "code": null, "name": "Certificated Course 2 (Qual)", "description": null},
      "code": null,
      "start": "2017-04-01T12:14:43Z",
      "end": "2017-06-03T13:14:43Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "285.71",
        "feeOverriden": null,
        "appliedDiscount": null,
        "possibleDiscounts": [],
        "hasTax": false
      }
    }, {
      "id": "5038554",
      "course": {"id": "5004795", "code": null, "name": "Module Course 4", "description": null},
      "code": null,
      "start": "2016-04-02T00:00:00Z",
      "end": "2017-06-03T02:00:00Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 86,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "400.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "388.00",
          "discountValue": "12.00",
          "title": "Glo.Bal Discounterino 3%"
        },
        "possibleDiscounts": [],
        "hasTax": false
      }
    }, {
      "id": "5039157",
      "course": {"id": "5004722", "code": null, "name": "Certificated Course 2 (Qual)", "description": null},
      "code": null,
      "start": "2016-04-30T09:32:43Z",
      "end": "2017-11-22T09:32:43Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 98,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "100.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "97.00",
          "discountValue": "3.00",
          "title": "Glo.Bal Discounterino 3%"
        },
        "possibleDiscounts": [],
        "hasTax": true
      }
    }
    ]) as Promise<CourseClass[]>;
  }
}
