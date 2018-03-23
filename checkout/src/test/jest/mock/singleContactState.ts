export const state = {
  preferences: {},
  checkout: {
    phase: 3,
    contacts: {
      entities: {
        contact: {
          35059: {
            email: "test@test.com",
            firstName: "Child",
            id: 35059,
            lastName: "Child",
            parentRequired: true,
          },
        },
      },
      result: [35059],
    },
    payerId: null,
    summary: {
      entities: {
        contactNodes: {
          35059: {
            contactId: 35059,
            enrolments: ["35059-12354"],
            applications: [],
            memberships: [],
            articles: [],
            vouchers: [],
            waitingLists: [],
          },
        },
        enrolments: {
          "35059-12354": {
            contactId: 35059,
            classId: 12354,
            errors: [],
            warnings: [],
            price: {
              fee: "108.00",
              feeOverriden: "948.00",
              hasTax: true,
              appliedDiscount: {
                id: 97372,
                discountedFee: "883.75",
                discountValue: "615.96",
                title: "Intelligent Steel Fish",
                expiryDate: "2018-07-19T17:31:29.613Z",
              },
              possibleDiscounts: [
                {
                  id: 11792,
                  discountedFee: "291.99",
                  discountValue: "376.55",
                  title: "Unbranded Rubber Shirt",
                  expiryDate: "2018-10-17T09:45:11.804Z",
                },
                {
                  id: 46580,
                  discountedFee: "126.63",
                  discountValue: "426.26",
                  title: "Handcrafted Frozen Chips",
                  expiryDate: "2018-11-11T19:52:34.373Z",
                },
              ],
            },
            selected: true,
          },
        },
        applications: {},
        memberships: {},
        articles: {},
        vouchers: {},
        waitingLists: {},
      },
      result: [35059],
      fetching: false,
    },
  },

  cart: {
    courses: {
      entities: {
        12354: {
          id: 12354,
          code: 30859,
          course: {
            id: 53942,
            code: "RNF1H",
            name: "Practical Soft Towels",
          },
          start: "2018-06-18T23:54:41.938Z",
          end: "2019-03-06T02:54:24.910Z",
          distantLearning: false,
          room: {
            name: "Baby",
            site: {
              name: "West Sadyeberg, Waters - Kessler",
              postcode: "58062",
              suburb: "Shemarland",
              street: "76231 Effertz Falls",
            },
          },
          price: {
            fee: "108.00",
            feeOverriden: "948.00",
            hasTax: true,
            appliedDiscount: {
              id: 97372,
              discountedFee: "883.75",
              discountValue: "615.96",
              title: "Intelligent Steel Fish",
              expiryDate: "2018-07-19T17:31:29.613Z",
            },
            possibleDiscounts: [
              {
                id: 11792,
                discountedFee: "291.99",
                discountValue: "376.55",
                title: "Unbranded Rubber Shirt",
                expiryDate: "2018-10-17T09:45:11.804Z",
              },
              {
                id: 46580,
                discountedFee: "126.63",
                discountValue: "426.26",
                title: "Handcrafted Frozen Chips",
                expiryDate: "2018-11-11T19:52:34.373Z",
              },
            ],
          },
          hasAvailablePlaces: true,
          isAllowByApplication: false,
          isCancelled: false,
          isFinished: false,
          isPaymentGatewayEnabled: true,
          availableEnrolmentPlaces: 7909,
          timezone: "Australia/Sydney",
        },
      },
      result: [
        12354,
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
      id: 35059,
      firstName: "Child",
      lastName: "Child",
      email: "test@test.com",
      parentRequired: true,
    },
  },
};
