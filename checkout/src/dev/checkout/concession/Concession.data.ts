import {Contact} from "../../../js/model";
import faker from "faker";

const mockContact = () : Contact => {
	return {
		id: faker.datatype.number().toString(),
		firstName: faker.name.firstName(),
		lastName: faker.name.lastName(),
		email: faker.internet.email(),
	};
};

export const contact: Contact = mockContact();

export const concessions : any[] =
	[
		{key: -1, value: "no concession"},
		{key: 0, value: "CHSP/CCSP Worker"},
	];


