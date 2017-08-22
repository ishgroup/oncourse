import * as Types from "../../../../js/model";

class Class1 {
  type: Types.DataType;
}

test('test Enum1.ts', () => {

  const class1: Class1 = createClass();

  expect(class1 instanceof Class1).toBe(true);
  expect(class1.type).toBe(Types.DataType.STRING);

  switch (class1.type) {
    case Types.DataType.STRING:
      expect(true).toBe(true);
      break;
    default:
      throw new Error();
  }
});


const createClass = (): Class1 => {
  const class1: Class1 = new Class1();
  class1.type = Types.DataType.STRING;
  return class1;
};
