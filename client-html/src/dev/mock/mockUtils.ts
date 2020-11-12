export const generateArraysOfRecords = (count: number, keys): any => {
  return Array.from(Array(count), (_, x) => ({
    id: x.toString(),
    ...mockByKeys(x, keys)
  }));
};

const mockByKeys = (index, keys) => {
  const obj = {};
  keys.map(item => {
    let value: any = `${item.name} ${index + 1}`;

    if (item.type === "Datetime") value = new Date().toISOString();
    else if (item.type === "number") value = parseInt(`${index + 1}`, 10);

    obj[item.name] = value;
  });

  return obj;
};

export const getRandomInt = (min, max) => {
  return Math.floor(Math.random() * (max - min + 1)) + min;
};
