import * as L from "lodash"
import {Mocker} from "mocker-data-generator";
import * as util from "util";

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
  }

  const mocker:Mocker = new Mocker();
  mocker.schema('contacts', ContactSchema, 2);
  mocker.build();
  console.log(mocker.DB);

  // mocker().schema('users', ContactMocker, 2).build(function(data) {
  //   console.log(util.inspect(data, { depth: 10 }))});
});


