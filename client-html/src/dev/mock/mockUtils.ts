export const generateArraysOfRecords = (count: number, keys): any => Array.from(Array(count), (_, x) => ({
  id: x.toString(),
  ...mockByKeys(x, keys)
}));

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

export const getRandomInt = (min, max) => Math.floor(Math.random() * (max - min + 1)) + min;

export const getEntityResponse = (entity = "", rows = [], columnsList = [], res = {}) => {
  const defaultColumn = {
    title: "",
    attribute: "",
    type: null,
    sortable: false,
    visible: true,
    system: null,
    width: 200,
    sortFields: []
  };

  const columns = [];

  columnsList.forEach(column => {
    columns.push({
      ...defaultColumn,
      ...column
    });
  });

  return {
    rows,
    columns,
    entity,
    offset: 0,
    filterColumnWidth: 200,
    layout: "Three column",
    pageSize: 20,
    search: null,
    count: rows.length,
    filteredCount: rows.length,
    sort: [],
    ...res
  };
};
