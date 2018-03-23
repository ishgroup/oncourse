export const state = {
  popup: {
    content: null,
  },
  cart: {
    courses: {
      entities: {
        41181: {
          id: 41181,
          code: 77500,
          course: {
            id: 3537,
            code: "X1ARA",
            name: "Incredible Metal Mouse",
          },
          start: "2018-06-01T12:14:38.283Z",
          end: "2018-06-10T13:29:48.754Z",
          distantLearning: false,
          room: {
            name: "Sports",
            site: {
              name: "Kreigershire, Leuschke - McKenzie",
              postcode: "65053",
              suburb: "Batzstad",
              street: "63457 Bartell Locks",
            },
          },
          price: {
            fee: "107.00",
            feeOverriden: "92.00",
            hasTax: true,
            appliedDiscount: {
              id: 31069,
              discountedFee: "760.52",
              discountValue: "410.16",
              title: "Licensed Cotton Pizza",
              expiryDate: "2018-10-17T17:04:47.630Z",
            },
            possibleDiscounts: [
              {
                id: 89191,
                discountedFee: "448.83",
                discountValue: "628.63",
                title: "Generic Cotton Keyboard",
                expiryDate: "2018-10-04T13:00:39.387Z",
              },
              {
                id: 1520,
                discountedFee: "675.58",
                discountValue: "778.08",
                title: "Unbranded Rubber Gloves",
                expiryDate: "2018-05-19T04:53:49.629Z",
              },
            ],
          },
          hasAvailablePlaces: true,
          isAllowByApplication: false,
          isCancelled: false,
          isFinished: false,
          isPaymentGatewayEnabled: true,
          availableEnrolmentPlaces: 55172,
          timezone: "Australia/Sydney",
        },
      },
      result: [
        41181,
      ],
    },
    products: {
      entities: {},
      result: [],
    },
    promotions: {
      entities: {},
      result: [],
    },
    waitingCourses: {
      entities: {},
      result: [],
    },
    contact: {
      id: 30276,
      firstName: "John",
      lastName: "Doe",
      email: "test@test.com",
      parentRequired: false,
    },
  },
  courses: {
    entities: {
      41181: {
        id: 41181,
        code: 77500,
        course: {
          id: 3537,
          code: "X1ARA",
          name: "Incredible Metal Mouse",
        },
        start: "2018-06-01T12:14:38.283Z",
        end: "2018-06-10T13:29:48.754Z",
        distantLearning: false,
        room: {
          name: "Sports",
          site: {
            name: "Kreigershire, Leuschke - McKenzie",
            postcode: "65053",
            suburb: "Batzstad",
            street: "63457 Bartell Locks",
          },
        },
        price: {
          fee: "107.00",
          feeOverriden: "92.00",
          hasTax: true,
          appliedDiscount: {
            id: 31069,
            discountedFee: "760.52",
            discountValue: "410.16",
            title: "Licensed Cotton Pizza",
            expiryDate: "2018-10-17T17:04:47.630Z",
          },
          possibleDiscounts: [
            {
              id: 89191,
              discountedFee: "448.83",
              discountValue: "628.63",
              title: "Generic Cotton Keyboard",
              expiryDate: "2018-10-04T13:00:39.387Z",
            },
            {
              id: 1520,
              discountedFee: "675.58",
              discountValue: "778.08",
              title: "Unbranded Rubber Gloves",
              expiryDate: "2018-05-19T04:53:49.629Z",
            },
          ],
        },
        hasAvailablePlaces: true,
        isAllowByApplication: false,
        isCancelled: false,
        isFinished: false,
        isPaymentGatewayEnabled: true,
        availableEnrolmentPlaces: 55172,
        timezone: "Australia/Sydney",
      },
    },
    result: [
      41181,
    ],
  },
  products: {
    entities: {},
    result: [],
  },
  waitingCourses: {
    entities: {},
    result: [],
  },
  checkout: {
    fetching: false,
    newContact: false,
    fields: {
      current: null,
      unfilled: [],
    },
    phase: 4,
    page: 4,
    error: null,
    amount: {
      total: "431.30",
      subTotal: "417.34",
      owing: "219.71",
      discount: "625.41",
      payNow: "37.39",
      minPayNow: 0,
      voucherPayments: [
        {
          redeemVoucherId: "1-100",
          amount: 555,
        },
        {
          redeemVoucherId: "2-100",
          amount: 666,
        },
      ],
      isEditable: true,
      payNowVisibility: true,
    },
    summary: {
      entities: {
        contactNodes: {
          20503: {
            contactId: 20503,
            enrolments: [
              "20503-41181",
            ],
            applications: [],
            memberships: [],
            articles: [],
            vouchers: [],
            waitingLists: [],
          },
          30276: {
            contactId: 30276,
            enrolments: [
              "30276-41181",
            ],
            applications: [],
            memberships: [],
            articles: [],
            vouchers: [],
            waitingLists: [],
          },
          65768: {
            contactId: 65768,
            enrolments: [
              "65768-41181",
            ],
            applications: [],
            memberships: [],
            articles: [],
            vouchers: [],
            waitingLists: [],
          },
        },
        enrolments: {
          "20503-41181": {
            contactId: 20503,
            classId: 41181,
            errors: [],
            warnings: [],
            price: {
              fee: "107.00",
              feeOverriden: "92.00",
              hasTax: true,
              appliedDiscount: {
                id: 31069,
                discountedFee: "760.52",
                discountValue: "410.16",
                title: "Licensed Cotton Pizza",
                expiryDate: "2018-10-17T17:04:47.630Z",
              },
              possibleDiscounts: [
                {
                  id: 89191,
                  discountedFee: "448.83",
                  discountValue: "628.63",
                  title: "Generic Cotton Keyboard",
                  expiryDate: "2018-10-04T13:00:39.387Z",
                },
                {
                  id: 1520,
                  discountedFee: "675.58",
                  discountValue: "778.08",
                  title: "Unbranded Rubber Gloves",
                  expiryDate: "2018-05-19T04:53:49.629Z",
                },
              ],
            },
            selected: true,
            fieldHeadings: [
              {
                fields: [
                  {
                    key: "street",
                    name: "Street",
                    id: 29031,
                    description: "Use the haptic SSL protocol, then you can bypass the wireless bus!",
                    mandatory: true,
                    dataType: "STRING",
                    enumType: null,
                    value: null,
                    defaultValue: "Kirova",
                    enumItems: [],
                    ordering: 0,
                  },
                  {
                    key: "customField.contact.passportType",
                    name: "Passport type",
                    id: 49757,
                    description: "You can't transmit the array without overriding the virtual FTP bus!",
                    mandatory: true,
                    dataType: "CHOICE",
                    enumType: "Passport type",
                    value: null,
                    defaultValue: "MPP",
                    enumItems: [
                      {
                        key: "1",
                        value: "MP",
                      },
                      {
                        key: "2",
                        value: "MK",
                      },
                    ],
                    ordering: 0,
                  },
                ],
                name: "heading 2",
              },
            ],
          },
          "65768-41181": {
            contactId: 65768,
            classId: 41181,
            errors: [],
            warnings: [],
            price: {
              fee: "107.00",
              feeOverriden: "92.00",
              hasTax: true,
              appliedDiscount: {
                id: 31069,
                discountedFee: "760.52",
                discountValue: "410.16",
                title: "Licensed Cotton Pizza",
                expiryDate: "2018-10-17T17:04:47.630Z",
              },
              possibleDiscounts: [
                {
                  id: 89191,
                  discountedFee: "448.83",
                  discountValue: "628.63",
                  title: "Generic Cotton Keyboard",
                  expiryDate: "2018-10-04T13:00:39.387Z",
                },
                {
                  id: 1520,
                  discountedFee: "675.58",
                  discountValue: "778.08",
                  title: "Unbranded Rubber Gloves",
                  expiryDate: "2018-05-19T04:53:49.629Z",
                },
              ],
            },
            selected: true,
            fieldHeadings: [
              {
                fields: [
                  {
                    key: "street",
                    name: "Street",
                    id: 29031,
                    description: "Use the haptic SSL protocol, then you can bypass the wireless bus!",
                    mandatory: true,
                    dataType: "STRING",
                    enumType: null,
                    value: null,
                    defaultValue: "Kirova",
                    enumItems: [],
                    ordering: 0,
                  },
                  {
                    key: "customField.contact.passportType",
                    name: "Passport type",
                    id: 49757,
                    description: "You can't transmit the array without overriding the virtual FTP bus!",
                    mandatory: true,
                    dataType: "CHOICE",
                    enumType: "Passport type",
                    value: null,
                    defaultValue: "MPP",
                    enumItems: [
                      {
                        key: "1",
                        value: "MP",
                      },
                      {
                        key: "2",
                        value: "MK",
                      },
                    ],
                    ordering: 0,
                  },
                ],
                name: "heading 2",
              },
            ],
          },
          "30276-41181": {
            contactId: 30276,
            classId: 41181,
            errors: [],
            warnings: [],
            price: {
              fee: "107.00",
              feeOverriden: "92.00",
              hasTax: true,
              appliedDiscount: {
                id: 31069,
                discountedFee: "760.52",
                discountValue: "410.16",
                title: "Licensed Cotton Pizza",
                expiryDate: "2018-10-17T17:04:47.630Z",
              },
              possibleDiscounts: [
                {
                  id: 89191,
                  discountedFee: "448.83",
                  discountValue: "628.63",
                  title: "Generic Cotton Keyboard",
                  expiryDate: "2018-10-04T13:00:39.387Z",
                },
                {
                  id: 1520,
                  discountedFee: "675.58",
                  discountValue: "778.08",
                  title: "Unbranded Rubber Gloves",
                  expiryDate: "2018-05-19T04:53:49.629Z",
                },
              ],
            },
            selected: true,
            fieldHeadings: [
              {
                fields: [
                  {
                    key: "street",
                    name: "Street",
                    id: 29031,
                    description: "Use the haptic SSL protocol, then you can bypass the wireless bus!",
                    mandatory: true,
                    dataType: "STRING",
                    enumType: null,
                    value: null,
                    defaultValue: "Kirova",
                    enumItems: [],
                    ordering: 0,
                  },
                  {
                    key: "customField.contact.passportType",
                    name: "Passport type",
                    id: 49757,
                    description: "You can't transmit the array without overriding the virtual FTP bus!",
                    mandatory: true,
                    dataType: "CHOICE",
                    enumType: "Passport type",
                    value: null,
                    defaultValue: "MPP",
                    enumItems: [
                      {
                        key: "1",
                        value: "MP",
                      },
                      {
                        key: "2",
                        value: "MK",
                      },
                    ],
                    ordering: 0,
                  },
                ],
                name: "heading 2",
              },
            ],
          },
        },
        applications: {},
        memberships: {},
        articles: {},
        vouchers: {},
        waitingLists: {},
      },
      result: [
        30276,
        65768,
        20503,
      ],
      fetching: false,
    },
    payment: {
      corporatePass: {},
      currentTab: 0,
      resetOnInit: false,
      result: [],
      fetching: false,
    },
    payerId: 20503,
    contacts: {
      entities: {
        contact: {
          20503: {
            id: 20503,
            firstName: "Pasquale",
            lastName: "Maggio",
            email: "Alejandrin.OReilly@hotmail.com",
            uniqueIdentifier: "nrkus2gefp",
            company: false,
            parent: null,
          },
          30276: {
            id: 30276,
            firstName: "John",
            lastName: "Doe",
            email: "test@test.com",
            parentRequired: false,
            parent: null,
          },
          65768: {
            id: 65768,
            firstName: "Lorem",
            lastName: "Ipsum",
            email: "lorem@ipsum.com",
            parentRequired: false,
            parent: null,
          },
        },
      },
      result: [
        30276,
        65768,
        20503,
      ],
    },
    concession: {
      contactId: null,
      types: [
        {
          id: "-1",
          name: "No concession",
        },
        {
          id: "2",
          name: "Man",
          hasExpireDate: true,
          hasNumber: true,
        },
        {
          id: "1",
          name: "Student",
        },
      ],
      concessions: [
        {
          contactId: 30276,
          name: "Student",
        },
      ],
      memberships: [
        {
          contactId: 30276,
          name: "Studend membership",
        },
      ],
    },
    redeemVouchers: [
      {
        name: "1 voucher",
        id: "1-100",
        payer: {
          id: 20503,
          firstName: "Pasquale",
          lastName: "Maggio",
          email: "Alejandrin.OReilly@hotmail.com",
          uniqueIdentifier: "nrkus2gefp",
          company: false,
        },
        enabled: true,
      },
    ],
    contactAddProcess: {
      contact: {},
      type: null,
      forChild: null,
    },
    isCartModified: false,
  },
  config: {
    checkoutPath: "/enrol/",
    paymentSuccessURL: "/courses",
    termsAndConditions: null,
    featureEnrolmentDisclosure: null,
  },
  preferences: {
    corporatePassEnabled: true,
    creditCardEnabled: true,
    successLink: "/courses",
    refundPolicyUrl: "/courses",
    minAge: 17,
  },
};
