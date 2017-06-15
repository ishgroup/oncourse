import * as Items from "../../../../js/model/checkout";
import {ContactNode} from "../../../../js/model/checkout/ContactNode";
import {ContactNodeService} from "../../../../js/enrol/services/ContactNodeService";


test("test ContactNodeService.getPurchaseItem", () => {
  const node: ContactNode = {
    enrolments: [{contactId: "1000"}],
    applications: [{contactId: "1001"}],
    articles: [{contactId: "1002"}],
    memberships: [{contactId: "1003"}],
    vouchers: [{contactId: "1004"}],
  };

  let input: any = new Items.Enrolment();
  let result: any = ContactNodeService.getPurchaseItem(node, input.constructor.name);
  expect(result instanceof Items.Enrolment).toBeTruthy();
  expect(result.contactId).toBe("1000");

  input = new Items.Application();
  result = ContactNodeService.getPurchaseItem(node, input.constructor.name);
  expect(result instanceof Items.Application).toBeTruthy();
  expect(result.contactId).toBe("1001");


  input = new Items.Article();
  result = ContactNodeService.getPurchaseItem(node, input.constructor.name);
  expect(result instanceof Items.Article).toBeTruthy();
  expect(result.contactId).toBe("1002");

  input = new Items.Membership();
  result = ContactNodeService.getPurchaseItem(node, input.constructor.name);
  expect(result instanceof Items.Membership).toBeTruthy();
  expect(result.contactId).toBe("1003");

  input = new Items.Voucher();
  result = ContactNodeService.getPurchaseItem(node, input.constructor.name);
  expect(result instanceof Items.Voucher).toBeTruthy();
  expect(result.contactId).toBe("1004");
});