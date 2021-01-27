const mockByKeys = (index, keys) => {
  const obj = {};
  keys.forEach(item => {
    let value: any = `${item.name} ${index + 1}`;

    if (item.type === "Datetime") value = new Date().toISOString();
    else if (item.type === "number") value = parseInt(`${index + 1}`, 10);

    obj[item.name] = value;
  });

  return obj;
};

export const generateArraysOfRecords = (count: number, keys): any => Array.from(Array(count), (_, x) => ({
  id: x.toString(),
  ...mockByKeys(x, keys)
}));

export const getRandomInt = (min, max) => Math.floor(Math.random() * (max - min + 1)) + min;

export const getEntityResponse = ({
 entity = "", rows = [], columns = [], res = {}, plain = false
}) => {
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

  const columnsList = [];

  columns.forEach(column => {
    columnsList.push({
      ...defaultColumn,
      ...column
    });
  });

  let response = {
    rows,
    columns: columnsList,
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

  if (plain) {
    response = { ...response, filterColumnWidth: null, layout: null };
  }

  return response;
};

export const removeItemByEntity = (entity, id) => {
  entity.rows = [...entity.rows.filter(m => Number(m.id) !== Number(id))];
  entity.count = entity.rows.length;
  entity.filteredCount = entity.rows.length;
  return entity;
};

export const getParamsId = config => {
  const params = config.url.split("/");
  return params[params.length - 1];
};
