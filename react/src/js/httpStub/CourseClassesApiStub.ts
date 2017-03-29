import {DefaultHttpService, HttpService} from "../services/HttpService";
import { CourseClass } from "../model/CourseClass";
import { CourseClassesParams } from "../model/CourseClassesParams";
import { ModelError } from "../model/ModelError";
import {CourseClassesApi} from "../http/CourseClassesApi";

export class CourseClassesApiStub extends CourseClassesApi {

  getCourseClasses(courseClassesParams: CourseClassesParams): Promise<CourseClass[]> {
    return Promise.resolve([{
      "id": "5034095",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1000.00",
        "feeOverriden": "1000.00",
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "850.00",
          "discountValue": "150.00",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "900.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5034138",
        "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1000.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "850.00",
          "discountValue": "150.00",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "900.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5034160",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5034744",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1098.90",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "934.07",
          "discountValue": "164.83",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "989.01",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5034760",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1000.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "850.00",
          "discountValue": "150.00",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "900.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5034827",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "352.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "299.20",
          "discountValue": "52.80",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "316.80",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5034864",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2017,
        "month": "MAY",
        "monthValue": 5,
        "dayOfMonth": 12,
        "dayOfWeek": "FRIDAY",
        "hour": 14,
        "minute": 0,
        "second": 10,
        "nano": 0,
        "dayOfYear": 132,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2017,
        "month": "SEPTEMBER",
        "monthValue": 9,
        "dayOfMonth": 8,
        "dayOfWeek": "FRIDAY",
        "hour": 16,
        "minute": 0,
        "second": 10,
        "nano": 0,
        "dayOfYear": 251,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5034921",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "352.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "299.20",
          "discountValue": "52.80",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "316.80",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5034973",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2017,
        "month": "JULY",
        "monthValue": 7,
        "dayOfMonth": 11,
        "dayOfWeek": "TUESDAY",
        "hour": 14,
        "minute": 0,
        "second": 10,
        "nano": 0,
        "dayOfYear": 192,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2017,
        "month": "NOVEMBER",
        "monthValue": 11,
        "dayOfMonth": 7,
        "dayOfWeek": "TUESDAY",
        "hour": 16,
        "minute": 0,
        "second": 10,
        "nano": 0,
        "dayOfYear": 311,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035011",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2017,
        "month": "JUNE",
        "monthValue": 6,
        "dayOfMonth": 11,
        "dayOfWeek": "SUNDAY",
        "hour": 14,
        "minute": 0,
        "second": 10,
        "nano": 0,
        "dayOfYear": 162,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2017,
        "month": "OCTOBER",
        "monthValue": 10,
        "dayOfMonth": 8,
        "dayOfWeek": "SUNDAY",
        "hour": 16,
        "minute": 0,
        "second": 10,
        "nano": 0,
        "dayOfYear": 281,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035074",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035140",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035158",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035235",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035246",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1098.90",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "934.07",
          "discountValue": "164.83",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "989.01",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5035259",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1000.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "850.00",
          "discountValue": "150.00",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "900.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035296",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1098.90",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "934.07",
          "discountValue": "164.83",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "989.01",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5035330",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1000.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "850.00",
          "discountValue": "150.00",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "900.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035368",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1000.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "850.00",
          "discountValue": "150.00",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "900.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035401",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035407",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1000.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "850.00",
          "discountValue": "150.00",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "900.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035429",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1000.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "850.00",
          "discountValue": "150.00",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "900.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035463",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2017,
        "month": "JULY",
        "monthValue": 7,
        "dayOfMonth": 11,
        "dayOfWeek": "TUESDAY",
        "hour": 14,
        "minute": 0,
        "second": 10,
        "nano": 0,
        "dayOfYear": 192,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2017,
        "month": "NOVEMBER",
        "monthValue": 11,
        "dayOfMonth": 7,
        "dayOfWeek": "TUESDAY",
        "hour": 16,
        "minute": 0,
        "second": 10,
        "nano": 0,
        "dayOfYear": 311,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035530",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035538",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1098.90",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "934.07",
          "discountValue": "164.83",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "989.01",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5035627",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1098.90",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "934.07",
          "discountValue": "164.83",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "989.01",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5035698",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2016,
        "month": "DECEMBER",
        "monthValue": 12,
        "dayOfMonth": 28,
        "dayOfWeek": "WEDNESDAY",
        "hour": 17,
        "minute": 54,
        "second": 58,
        "nano": 0,
        "dayOfYear": 363,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2017,
        "month": "APRIL",
        "monthValue": 4,
        "dayOfMonth": 5,
        "dayOfWeek": "WEDNESDAY",
        "hour": 20,
        "minute": 54,
        "second": 58,
        "nano": 0,
        "dayOfYear": 95,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1100.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "935.00",
          "discountValue": "165.00",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "990.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5035705",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1098.90",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "934.07",
          "discountValue": "164.83",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "989.01",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5035706",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1000.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "850.00",
          "discountValue": "150.00",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "900.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035715",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2017,
        "month": "AUGUST",
        "monthValue": 8,
        "dayOfMonth": 10,
        "dayOfWeek": "THURSDAY",
        "hour": 14,
        "minute": 0,
        "second": 10,
        "nano": 0,
        "dayOfYear": 222,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2017,
        "month": "DECEMBER",
        "monthValue": 12,
        "dayOfMonth": 7,
        "dayOfWeek": "THURSDAY",
        "hour": 16,
        "minute": 0,
        "second": 10,
        "nano": 0,
        "dayOfYear": 341,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035766",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1000.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "850.00",
          "discountValue": "150.00",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "900.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035804",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5036062",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2016,
        "month": "DECEMBER",
        "monthValue": 12,
        "dayOfMonth": 28,
        "dayOfWeek": "WEDNESDAY",
        "hour": 17,
        "minute": 54,
        "second": 58,
        "nano": 0,
        "dayOfYear": 363,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2017,
        "month": "APRIL",
        "monthValue": 4,
        "dayOfMonth": 5,
        "dayOfWeek": "WEDNESDAY",
        "hour": 20,
        "minute": 54,
        "second": 58,
        "nano": 0,
        "dayOfYear": 95,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1100.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "935.00",
          "discountValue": "165.00",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "990.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5036157",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2017,
        "month": "MAY",
        "monthValue": 5,
        "dayOfMonth": 13,
        "dayOfWeek": "SATURDAY",
        "hour": 22,
        "minute": 58,
        "second": 21,
        "nano": 0,
        "dayOfYear": 133,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2017,
        "month": "JULY",
        "monthValue": 7,
        "dayOfMonth": 14,
        "dayOfWeek": "FRIDAY",
        "hour": 23,
        "minute": 58,
        "second": 21,
        "nano": 0,
        "dayOfYear": 195,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
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
          "discountedFee": "215.05",
          "discountValue": "37.95",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "227.70",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5036207",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2017,
        "month": "FEBRUARY",
        "monthValue": 2,
        "dayOfMonth": 26,
        "dayOfWeek": "SUNDAY",
        "hour": 19,
        "minute": 35,
        "second": 34,
        "nano": 0,
        "dayOfYear": 57,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2017,
        "month": "MAY",
        "monthValue": 5,
        "dayOfMonth": 20,
        "dayOfWeek": "SATURDAY",
        "hour": 22,
        "minute": 35,
        "second": 34,
        "nano": 0,
        "dayOfYear": 140,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "550.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "467.50",
          "discountValue": "82.50",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "495.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5036405",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "352.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "299.20",
          "discountValue": "52.80",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "316.80",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5036425",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "352.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "299.20",
          "discountValue": "52.80",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "316.80",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5036433",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2017,
        "month": "FEBRUARY",
        "monthValue": 2,
        "dayOfMonth": 26,
        "dayOfWeek": "SUNDAY",
        "hour": 17,
        "minute": 54,
        "second": 58,
        "nano": 0,
        "dayOfYear": 57,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2017,
        "month": "JUNE",
        "monthValue": 6,
        "dayOfMonth": 4,
        "dayOfWeek": "SUNDAY",
        "hour": 20,
        "minute": 54,
        "second": 58,
        "nano": 0,
        "dayOfYear": 155,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1100.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "935.00",
          "discountValue": "165.00",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "990.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5036460",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2017,
        "month": "FEBRUARY",
        "monthValue": 2,
        "dayOfMonth": 12,
        "dayOfWeek": "SUNDAY",
        "hour": 22,
        "minute": 58,
        "second": 21,
        "nano": 0,
        "dayOfYear": 43,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2017,
        "month": "APRIL",
        "monthValue": 4,
        "dayOfMonth": 15,
        "dayOfWeek": "SATURDAY",
        "hour": 23,
        "minute": 58,
        "second": 21,
        "nano": 0,
        "dayOfYear": 105,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
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
          "discountedFee": "215.05",
          "discountValue": "37.95",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "227.70",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5036477",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2017,
        "month": "JANUARY",
        "monthValue": 1,
        "dayOfMonth": 27,
        "dayOfWeek": "FRIDAY",
        "hour": 17,
        "minute": 54,
        "second": 58,
        "nano": 0,
        "dayOfYear": 27,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2017,
        "month": "MAY",
        "monthValue": 5,
        "dayOfMonth": 5,
        "dayOfWeek": "FRIDAY",
        "hour": 20,
        "minute": 54,
        "second": 58,
        "nano": 0,
        "dayOfYear": 125,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1100.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "935.00",
          "discountValue": "165.00",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "990.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5036589",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2017,
        "month": "MAY",
        "monthValue": 5,
        "dayOfMonth": 14,
        "dayOfWeek": "SUNDAY",
        "hour": 21,
        "minute": 9,
        "second": 31,
        "nano": 0,
        "dayOfYear": 134,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2017,
        "month": "MAY",
        "monthValue": 5,
        "dayOfMonth": 18,
        "dayOfWeek": "THURSDAY",
        "hour": 22,
        "minute": 9,
        "second": 31,
        "nano": 0,
        "dayOfYear": 138,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "1100.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "935.00",
          "discountValue": "165.00",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "990.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5036774",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "352.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "299.20",
          "discountValue": "52.80",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "316.80",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5036902",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2017,
        "month": "MAY",
        "monthValue": 5,
        "dayOfMonth": 27,
        "dayOfWeek": "SATURDAY",
        "hour": 19,
        "minute": 35,
        "second": 34,
        "nano": 0,
        "dayOfYear": 147,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2017,
        "month": "AUGUST",
        "monthValue": 8,
        "dayOfMonth": 18,
        "dayOfWeek": "FRIDAY",
        "hour": 22,
        "minute": 35,
        "second": 34,
        "nano": 0,
        "dayOfYear": 230,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "550.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "467.50",
          "discountValue": "82.50",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "495.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5036974",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2017,
        "month": "JANUARY",
        "monthValue": 1,
        "dayOfMonth": 27,
        "dayOfWeek": "FRIDAY",
        "hour": 19,
        "minute": 35,
        "second": 34,
        "nano": 0,
        "dayOfYear": 27,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2017,
        "month": "APRIL",
        "monthValue": 4,
        "dayOfMonth": 20,
        "dayOfWeek": "THURSDAY",
        "hour": 22,
        "minute": 35,
        "second": 34,
        "nano": 0,
        "dayOfYear": 110,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
      "isFinished": false,
      "isCancelled": false,
      "isAllowByApplication": false,
      "isPaymentGatewayEnabled": true,
      "price": {
        "fee": "550.00",
        "feeOverriden": null,
        "appliedDiscount": {
          "id": null,
          "expiryDate": null,
          "discountedFee": "467.50",
          "discountValue": "82.50",
          "title": "Test Discount 1"
        },
        "possibleDiscounts": [{
          "id": null,
          "expiryDate": null,
          "discountedFee": "495.00",
          "discountValue": null,
          "title": "Conc "
        }],
        "hasTax": true
      }
    }, {
      "id": "5037328",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2016,
        "month": "JULY",
        "monthValue": 7,
        "dayOfMonth": 22,
        "dayOfWeek": "FRIDAY",
        "hour": 22,
        "minute": 37,
        "second": 15,
        "nano": 0,
        "dayOfYear": 204,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2018,
        "month": "JUNE",
        "monthValue": 6,
        "dayOfMonth": 15,
        "dayOfWeek": "FRIDAY",
        "hour": 23,
        "minute": 37,
        "second": 15,
        "nano": 0,
        "dayOfYear": 166,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5038511",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2015,
        "month": "DECEMBER",
        "monthValue": 12,
        "dayOfMonth": 1,
        "dayOfWeek": "TUESDAY",
        "hour": 21,
        "minute": 13,
        "second": 11,
        "nano": 0,
        "dayOfYear": 335,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2018,
        "month": "MAY",
        "monthValue": 5,
        "dayOfMonth": 1,
        "dayOfWeek": "TUESDAY",
        "hour": 21,
        "minute": 13,
        "second": 11,
        "nano": 0,
        "dayOfYear": 121,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5038573",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2017,
        "month": "APRIL",
        "monthValue": 4,
        "dayOfMonth": 2,
        "dayOfWeek": "SUNDAY",
        "hour": 16,
        "minute": 1,
        "second": 1,
        "nano": 0,
        "dayOfYear": 92,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2017,
        "month": "APRIL",
        "monthValue": 4,
        "dayOfMonth": 16,
        "dayOfWeek": "SUNDAY",
        "hour": 17,
        "minute": 1,
        "second": 1,
        "nano": 0,
        "dayOfYear": 106,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5038575",
      "course": {
        "id": "42",
        "name": "Course Name"
      },
      "code": null,
      "start": {
        "year": 2017,
        "month": "APRIL",
        "monthValue": 4,
        "dayOfMonth": 3,
        "dayOfWeek": "MONDAY",
        "hour": 16,
        "minute": 2,
        "second": 50,
        "nano": 0,
        "dayOfYear": 93,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "end": {
        "year": 2017,
        "month": "APRIL",
        "monthValue": 4,
        "dayOfMonth": 17,
        "dayOfWeek": "MONDAY",
        "hour": 17,
        "minute": 2,
        "second": 50,
        "nano": 0,
        "dayOfYear": 107,
        "chronology": {"calendarType": "iso8601", "id": "ISO"}
      },
      "hasAvailablePlaces": true,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }]) as Promise<CourseClass[]>;
  }
}
