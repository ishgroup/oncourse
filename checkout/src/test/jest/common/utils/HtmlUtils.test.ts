import {Mocker} from "mocker-data-generator";

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
  }

  const mocker:Mocker = new Mocker();
  mocker.schema('contacts', ContactSchema, 2);
  mocker.build();
  console.log(mocker.DB);

  // mocker().schema('users', ContactMocker, 2).build(function(data) {
  //   console.log(util.inspect(data, { depth: 10 }))});
});


