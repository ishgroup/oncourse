import * as models from '../../../../js/model';
import { ContactNodeService } from '../../../../js/enrol/services/ContactNodeService';
import { FieldHeading } from '../../../../js/model';

test('test ContactNodeService.getPurchaseItem', () => {
  const node: models.ContactNode = {
    contactId: '1000',
    contactLastName: 'Test',
    contactFirstName: 'Test',
    contactEmail: 'test@test.test',
    enrolments: [{
      contactId: '1000',
      classId: null,
      warnings: [],
      errors: [],
      price: {},
      selected: false,
      fieldHeadings: []
    }],
    applications: [{
      contactId: '1001',
      classId: null,
      warnings: [],
      errors: [],
      selected: false,
      fieldHeadings: []
    }],
    articles: [{
      contactId: '1002',
      productId: null,
      warnings: [],
      errors: [],
      price: 10,
      quantity: 1,
      total: 10,
      selected: false
    }],
    memberships: [{
      contactId: '1003',
      productId: null,
      warnings: [],
      errors: [],
      price: 10,
      selected: false
    }],
    vouchers: [{
      contactId: '1004',
      productId: null,
      warnings: [],
      errors: [],
      price: 100,
      total: 100,
      value: 100,
      classes: [],
      selected: false,
      isEditablePrice: false,
      quantity: 1,
    }],
    waitingLists: []
  };

  let input: any = new models.Enrolment();
  let result: any = ContactNodeService.getPurchaseItem(node, input);
  expect(result instanceof models.Enrolment).toBeTruthy();
  expect(result.contactId).toBe('1000');

  input = new models.Application();
  result = ContactNodeService.getPurchaseItem(node, input);
  expect(result instanceof models.Application).toBeTruthy();
  expect(result.contactId).toBe('1001');

  input = new models.Article();
  result = ContactNodeService.getPurchaseItem(node, input);
  expect(result instanceof models.Article).toBeTruthy();
  expect(result.contactId).toBe('1002');

  input = new models.Membership();
  result = ContactNodeService.getPurchaseItem(node, input);
  expect(result instanceof models.Membership).toBeTruthy();
  expect(result.contactId).toBe('1003');

  input = new models.Voucher();
  result = ContactNodeService.getPurchaseItem(node, input);
  expect(result instanceof models.Voucher).toBeTruthy();
  expect(result.contactId).toBe('1004');
});
