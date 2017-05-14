test('test', () => {
  const props = {
    values: {
      firstName: "Andrei",
      lastName: "Koira",
      email: "pervoliner@gmail.com"
    }
  };

  const result = Object.assign({}, props.values, {id: "123456"});
  console.log(result);
});