import {DefaultHttpService, HttpService} from "../services/HttpService";
import { CourseClass } from "../model/CourseClass";
import { CourseClassesParams } from "../model/CourseClassesParams";
import { ModelError } from "../model/ModelError";
import {CourseClassesApi} from "../http/CourseClassesApi";

export class CourseClassesApiStub extends CourseClassesApi {

  getCourseClasses(courseClassesParams: CourseClassesParams): Promise<CourseClass[]> {
    return Promise.resolve([{
      "id": "5034095",
      "course": {"id": "5004585", "code": null, "name": "Mega Course 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 96,
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
      "id": "5034138",
      "course": {"id": "5004585", "code": null, "name": "Mega Course 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 147,
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
      "course": {"id": "5004587", "code": null, "name": "VET COurse 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 992,
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
      "course": {"id": "5004590", "code": null, "name": "Archeology", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 99,
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
      "course": {"id": "5004585", "code": null, "name": "Mega Course 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 199,
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
      "course": {"id": "5004586", "code": null, "name": "Test Course", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004587", "code": null, "name": "VET COurse 1", "description": null},
      "code": null,
      "start": "2017-05-12T14:00:10Z",
      "end": "2017-09-08T16:00:10Z",
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5034921",
      "course": {"id": "5004586", "code": null, "name": "Test Course", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004587", "code": null, "name": "VET COurse 1", "description": null},
      "code": null,
      "start": "2017-07-11T14:00:10Z",
      "end": "2017-11-07T16:00:10Z",
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035011",
      "course": {"id": "5004587", "code": null, "name": "VET COurse 1", "description": null},
      "code": null,
      "start": "2017-06-11T14:00:10Z",
      "end": "2017-10-08T16:00:10Z",
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035074",
      "course": {"id": "5004587", "code": null, "name": "VET COurse 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035140",
      "course": {"id": "5004587", "code": null, "name": "VET COurse 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035158",
      "course": {"id": "5004587", "code": null, "name": "VET COurse 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035235",
      "course": {"id": "5004587", "code": null, "name": "VET COurse 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035246",
      "course": {"id": "5004590", "code": null, "name": "Archeology", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004585", "code": null, "name": "Mega Course 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 200,
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
      "course": {"id": "5004590", "code": null, "name": "Archeology", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004585", "code": null, "name": "Mega Course 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 200,
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
      "course": {"id": "5004585", "code": null, "name": "Mega Course 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 200,
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
      "course": {"id": "5004587", "code": null, "name": "VET COurse 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035407",
      "course": {"id": "5004585", "code": null, "name": "Mega Course 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 200,
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
      "course": {"id": "5004585", "code": null, "name": "Mega Course 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 200,
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
      "course": {"id": "5004587", "code": null, "name": "VET COurse 1", "description": null},
      "code": null,
      "start": "2017-07-11T14:00:10Z",
      "end": "2017-11-07T16:00:10Z",
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035530",
      "course": {"id": "5004587", "code": null, "name": "VET COurse 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035538",
      "course": {"id": "5004590", "code": null, "name": "Archeology", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004590", "code": null, "name": "Archeology", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004588", "code": null, "name": "Payment Course 1", "description": null},
      "code": null,
      "start": "2016-12-28T17:54:58Z",
      "end": "2017-04-05T20:54:58Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 99,
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
      "course": {"id": "5004590", "code": null, "name": "Archeology", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004585", "code": null, "name": "Mega Course 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 200,
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
      "course": {"id": "5004587", "code": null, "name": "VET COurse 1", "description": null},
      "code": null,
      "start": "2017-08-10T14:00:10Z",
      "end": "2017-12-07T16:00:10Z",
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5035766",
      "course": {"id": "5004585", "code": null, "name": "Mega Course 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 200,
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
      "course": {"id": "5004587", "code": null, "name": "VET COurse 1", "description": null},
      "code": null,
      "start": null,
      "end": null,
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5036062",
      "course": {"id": "5004588", "code": null, "name": "Payment Course 1", "description": null},
      "code": null,
      "start": "2016-12-28T17:54:58Z",
      "end": "2017-04-05T20:54:58Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004586", "code": null, "name": "Test Course", "description": null},
      "code": null,
      "start": "2017-05-13T22:58:21Z",
      "end": "2017-07-14T23:58:21Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004586", "code": null, "name": "Test Course", "description": null},
      "code": null,
      "start": "2017-02-26T19:35:34Z",
      "end": "2017-05-20T22:35:34Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004586", "code": null, "name": "Test Course", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004586", "code": null, "name": "Test Course", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004588", "code": null, "name": "Payment Course 1", "description": null},
      "code": null,
      "start": "2017-02-26T17:54:58Z",
      "end": "2017-06-04T20:54:58Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004586", "code": null, "name": "Test Course", "description": null},
      "code": null,
      "start": "2017-02-12T22:58:21Z",
      "end": "2017-04-15T23:58:21Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004588", "code": null, "name": "Payment Course 1", "description": null},
      "code": null,
      "start": "2017-01-27T17:54:58Z",
      "end": "2017-05-05T20:54:58Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004588", "code": null, "name": "Payment Course 1", "description": null},
      "code": null,
      "start": "2017-05-14T21:09:31Z",
      "end": "2017-05-18T22:09:31Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004586", "code": null, "name": "Test Course", "description": null},
      "code": null,
      "start": null,
      "end": null,
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004586", "code": null, "name": "Test Course", "description": null},
      "code": null,
      "start": "2017-05-27T19:35:34Z",
      "end": "2017-08-18T22:35:34Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004586", "code": null, "name": "Test Course", "description": null},
      "code": null,
      "start": "2017-01-27T19:35:34Z",
      "end": "2017-04-20T22:35:34Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 100,
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
      "course": {"id": "5004760", "code": null, "name": "VET3", "description": null},
      "code": null,
      "start": "2016-07-22T22:37:15Z",
      "end": "2018-06-15T23:37:15Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 998,
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
      "course": {"id": "5004782", "code": null, "name": "UoC 1", "description": null},
      "code": null,
      "start": "2015-12-01T21:13:11Z",
      "end": "2018-05-01T21:13:11Z",
      "hasAvailablePlaces": true,
      "availableEnrolmentPlaces": 998,
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
      "course": {"id": "5004783", "code": null, "name": "UOC 2", "description": null},
      "code": null,
      "start": "2017-04-02T16:01:01Z",
      "end": "2017-04-16T17:01:01Z",
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }, {
      "id": "5038575",
      "course": {"id": "5004782", "code": null, "name": "UoC 1", "description": null},
      "code": null,
      "start": "2017-04-03T16:02:50Z",
      "end": "2017-04-17T17:02:50Z",
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
          "title": "Conc "
        }],
        "hasTax": false
      }
    }]) as Promise<CourseClass[]>;
  }
}
