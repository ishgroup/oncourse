import * as models from "../../../../js/model";
import {ContactNodeService} from "../../../../js/enrol/services/ContactNodeService";


test("test ContactNodeService.getPurchaseItem", () => {
  const node: models.ContactNode = {
    enrolments: [{contactId: "1000"}],
    applications: [{contactId: "1001"}],
    articles: [{contactId: "1002"}],
    memberships: [{contactId: "1003"}],
    vouchers: [{contactId: "1004"}],
  };

  let input: any = new models.Enrolment();
  let result: any = ContactNodeService.getPurchaseItem(node, input);
  expect(result instanceof models.Enrolment).toBeTruthy();
  expect(result.contactId).toBe("1000");

  input = new models.Application();
  result = ContactNodeService.getPurchaseItem(node, input);
  expect(result instanceof models.Application).toBeTruthy();
  expect(result.contactId).toBe("1001");


  input = new models.Article();
  result = ContactNodeService.getPurchaseItem(node, input);
  expect(result instanceof models.Article).toBeTruthy();
  expect(result.contactId).toBe("1002");

  input = new models.Membership();
  result = ContactNodeService.getPurchaseItem(node, input);
  expect(result instanceof models.Membership).toBeTruthy();
  expect(result.contactId).toBe("1003");

  input = new models.Voucher();
  result = ContactNodeService.getPurchaseItem(node, input);
  expect(result instanceof models.Voucher).toBeTruthy();
  expect(result.contactId).toBe("1004");
});