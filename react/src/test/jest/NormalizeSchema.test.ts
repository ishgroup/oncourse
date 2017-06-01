import {mockContact} from "../../dev/mocks/mocks/MockFunctions";
import {Contact} from "../../js/model/web/Contact";
import {normalize} from "normalizr";
import {ContactSchema, ContactsSchema} from "../../js/NormalizeSchema";
import {inspect} from "util";

test('test ContactSchema', () => {
  const contact: Contact = mockContact();

  console.log(inspect(normalize(contact, ContactSchema), true, 10, true));
});

test('test ContactsSchema', () => {
  const contacts: Contact[] = [mockContact(), mockContact()];

  console.log(inspect(normalize(contacts, ContactsSchema), true, 10, true));
});