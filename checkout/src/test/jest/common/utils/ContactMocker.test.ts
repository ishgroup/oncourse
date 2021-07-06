import {Mocker} from "mocker-data-generator";
import faker from "faker";

test('test', () => {
  const ContactSchema = {
    id: {
      function: function() {
        return `${this.faker.datatype.number()}`;
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
  console.log(faker.datatype.float());
});

