import faker from "faker";
import {Contact, Amount} from "../../../js/model";

export const amount: Amount = {
  total: faker.datatype.float(),
  owing: faker.datatype.float(),
  discount: faker.datatype.float(),
  payNow: faker.datatype.float(),
};
