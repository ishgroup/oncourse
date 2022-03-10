import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

export function mockRooms() {
  this.getRooms = () => this.rooms;

  this.getRoom = id => {
    const row = this.rooms.rows.find(room => Number(room.id) === Number(id));
    return {
      createdOn: "2021-01-30T10:17:48.295Z",
      directions: "",
      documents: [],
      facilities: "",
      id: row.id,
      kioskUrl: `https://ishoncourse.oncourse.cc/room/kiosk/${row.id}`,
      modifiedOn: "2021-01-30T10:17:48.295Z",
      name: row.values[0],
      notes: [],
      rules: [],
      seatedCapacity: row.values[2],
      siteId: row.id,
      tags: this.getTags()
    };
  };

  this.createRoom = item => {
    const data = JSON.parse(item);
    const rooms = this.rooms;
    const totalRows = rooms.rows;

    data.id = totalRows.length + 1;

    rooms.rows.push({
      id: data.id,
      values: [
        data.name,
        this.getSite(data.siteId).name,
        data.seatedCapacity
      ]
    });

    this.rooms = rooms;
    this.rooms.count = data.id;
    this.rooms.filteredCount = data.id;
  };

  this.removeRoom = id => {
    this.rooms = removeItemByEntity(this.rooms, id);
  };

  this.getPlainRooms = params => {
    const columnList = params.columns.split(",");
    const ids = params.search.replace(/(id in|\(|\))/g, '').trim().split(",");

    const rows = [];

    if (columnList.length) {
      if (columnList.includes("seatedCapacity")) {
        ids.forEach(id => {
          rows.push({
            id,
            values: ["none", "0"]
          });
        });
      }
    }

    return getEntityResponse({
      entity: "Room",
      rows,
      plain: true
    });
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "name", type: "string" },
    { name: "siteName", type: "string" },
    { name: "seatedCapacity", type: "number" }
  ]).map(l => ({
    id: l.id,
    values: [l.name, this.getSite(l.id).name, l.seatedCapacity]
  }));

  return getEntityResponse({
    entity: "Room",
    rows,
    columns: [
      {
        title: "Name",
        attribute: "name",
        sortable: true,
        width: 100
      },
      {
        title: "Site",
        attribute: "site.name",
        sortable: true
      },
      {
        title: "Seated capacity",
        attribute: "seatedCapacity",
        sortable: true
      }
    ],
    res: {
      sort: [{
        ascending: true,
        attribute: "name",
        complexAttribute: []
      }]
    }
  });
}
