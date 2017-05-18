import * as L from "lodash"

test('test', () => {
  const props = {
    headings: [
      {
        name: 'heading1',
        fields: [
          "field1",
          "field1"
        ]
      },
      {
        name: 'heading2',
        fields: [
          "field1",
          "field2"
        ]
      },
    ]
  };
  console.log(L.flatMap(props.headings, (h)=> {return h.fields} ));
});