import * as L from 'lodash';
import { inspect } from 'util';
import * as SummaryActions from '../../../../../../js/enrol/containers/summary/actions/Actions';
import { ContactNode } from '../../../../../../js/model';
import { ContactNodeToState, State } from '../../../../../../js/enrol/containers/summary/reducers/State';
import { Reducer } from '../../../../../../js/enrol/containers/summary/reducers/Reducer';

const state: State = ContactNodeToState([{
  contactId: '00001',
  contactLastName: 'Test',
  contactFirstName: 'Test',
  contactEmail: 'test@test.test',
  enrolments: [
    {
      contactId: '00001',
      classId: '00001',
      selected: false,
      price: {},
      warnings: [],
      errors: [],
      fieldHeadings: []
    }, {
      contactId: '00001',
      classId: '00002',
      selected: false,
      price: {},
      warnings: [],
      errors: [],
      fieldHeadings: []
    },
  ],
  applications: [],
  vouchers: [],
  articles: [],
  memberships: [],
  waitingLists: []
}, {
  contactId: '00002',
  contactLastName: 'Test',
  contactFirstName: 'Test',
  contactEmail: 'test@test.test',
  enrolments: [
    {
      contactId: '00002',
      classId: '00001',
      selected: false,
      price: {},
      warnings: [],
      errors: [],
      fieldHeadings: []
    }, {
      contactId: '00002',
      classId: '00002',
      selected: false,
      price: {},
      warnings: [],
      errors: [],
      fieldHeadings: []
    }, {
      contactId: '00002',
      classId: '00003',
      selected: false,
      price: {},
      warnings: [],
      errors: [],
      fieldHeadings: []
    },
  ],
  applications: [
    {
      contactId: '00002',
      classId: '00004',
      selected: false,
      warnings: [],
      errors: [],
      fieldHeadings: []
    },
  ],
  vouchers: [],
  articles: [],
  memberships: [],
  waitingLists: []
}]);

test('test add contact ', () => {
  const upi: ContactNode = {
    contactId: '00003',
    contactLastName: 'Test',
    contactFirstName: 'Test',
    contactEmail: 'test@test.test',
    applications: [],
    vouchers: [],
    articles: [],
    waitingLists: [],
    enrolments: [
      {
        contactId: '00003',
        classId: '00001',
        selected: false,
        fieldHeadings: [],
        price: {},
        warnings: [],
        errors: []
      },
    ],
    memberships: [
      {
        contactId: '00003',
        productId: '00011',
        selected: false,
        price: 100,
        warnings: [],
        errors: []
      },
    ],
  };

  const ns: State = Reducer(state, { type: SummaryActions.ADD_CONTACT_NODE_TO_STATE, payload: ContactNodeToState([upi]) });
  console.log(inspect(ns, true, 10, true));

  expect(ns.result.length).toBe(3);
  expect(ns.entities.enrolments['00003-00001'].contactId).toBe('00003');
  expect(ns.entities.memberships['00003-00011'].contactId).toBe('00003');
  expect(ns.entities.contactNodes['00003'].contactId).toBe('00003');
});

test('test add enrolment ', () => {
  const upi: ContactNode = {
    contactId: '00002',
    contactLastName: 'Test',
    contactFirstName: 'Test',
    contactEmail: 'test@test.test',
    memberships: [],
    applications: [],
    vouchers: [],
    articles: [],
    waitingLists: [],
    enrolments: [
      {
        contactId: '00002',
        classId: '00004',
        selected: false,
        fieldHeadings: [],
        price: {},
        warnings: [],
        errors: []
      },
    ],
  };

  const ns: State = Reducer(state, { type: SummaryActions.UPDATE_ITEM, payload: ContactNodeToState([upi]) });

  expect(ns.result.length).toBe(2);
  expect(ns.entities.enrolments['00002-00004'].contactId).toBe('00002');
  expect(ns.entities.contactNodes['00002'].enrolments[3]).toBe('00002-00004');
});

test('test update enrolment', () => {
  const upi: ContactNode = {
    contactId: '00002',
    contactLastName: 'Test',
    contactFirstName: 'Test',
    contactEmail: 'test@test.test',
    memberships: [],
    applications: [],
    vouchers: [],
    articles: [],
    waitingLists: [],
    enrolments: [
      {
        contactId: '00002',
        classId: '00003',
        selected: true,
        fieldHeadings: [],
        price: {},
        warnings: [],
        errors: []
      },
    ],
  };
  const ns: State = Reducer(state, { type: SummaryActions.UPDATE_ITEM, payload: ContactNodeToState([upi]) });
  expect(ns.result.length).toBe(2);
  expect(ns.entities.enrolments['00002-00003'].selected).toBe(true);
});

test('test update application', () => {
  const upi: ContactNode = {
    contactId: '00002',
    contactLastName: 'Test',
    contactFirstName: 'Test',
    contactEmail: 'test@test.test',
    applications: [
      {
        contactId: '00002',
        classId: '00005',
        selected: true,
        warnings: [],
        errors: [],
        fieldHeadings: [],
      },
    ],
    vouchers: [],
    articles: [],
    memberships: [],
    enrolments: [],
    waitingLists: []
  };

  const payload: State = ContactNodeToState([upi]);

  const ns = L.cloneDeep(state);

  ns.entities.enrolments = { ...ns.entities.enrolments, ...payload.entities.enrolments };
  ns.entities.applications = { ...ns.entities.applications, ...payload.entities.applications };

  console.log(inspect(ns, true, 10, true));
});
