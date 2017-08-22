import {Mocker} from "mocker-data-generator";
import faker from "faker";

const mockCourseClass = (): any => {
  return {
    id: faker.random.number() as string,
    code: faker.random.number() as string,
    course: {
      code: faker.random.alphaNumeric(5).toUpperCase(),
      name: faker.commerce.productName()
    },
    start: faker.date.future(),
    end: faker.date.future(),
    distantLearning: false,
    room: {
      name: faker.commerce.department(),
      site: {
        name: `${faker.address.city()}, ${faker.company.companyName()}`,
        postcode: faker.address.zipCode(),
        suburb: faker.address.city(),
        street: faker.address.streetAddress()
      }
    }

  }
};

test('test', () => {
  const ContactSchema = {
    id: {
      function: function() {
        return `${this.faker.random.number()}`;
      }
    },
    firstName: {
      faker: 'name.firstName'
    },
    lastName: {
      faker: 'name.lastName'
    },
    email: {
      faker: 'internet.email'
    }
  };

  const mocker:Mocker = new Mocker();
  mocker.schema('contacts', ContactSchema, 2);
  mocker.build();


  //console.log(mocker.DB);
  console.log(faker.finance.amount());
});

