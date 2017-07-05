import {Contact} from "../../../js/model/web/Contact";

import faker from "faker";
import {Amount} from "../../../js/model/checkout/Amount";

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
