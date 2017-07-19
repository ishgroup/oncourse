import faker from "faker";
import {Contact, Amount} from "../../../js/model";

const mockContact = (): Contact => {
  return {
    id: faker.random.number() as string,
    firstName: faker.name.firstName(),
    lastName: faker.name.lastName(),
    email: faker.internet.email(),
  };
};

const contacts: Contact[] =
  [
    mockContact(),
    mockContact(),
  ];

export const amount: Amount = {
  total: faker.finance.amount(),
  owing: faker.finance.amount(),
  discount: faker.finance.amount(),
  payNow: faker.finance.amount(),
};
