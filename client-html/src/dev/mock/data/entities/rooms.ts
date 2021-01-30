import { generateArraysOfRecords } from "../../mockUtils";

export function mockRooms() {
  this.getRoom = id => {
    const row = this.rooms.rows.find(row => row.id == id);
    return {
      createdOn: new Date().toISOString(),
      directions: null,
      documents: [],
      facilities: null,
      id: row.id,
      kioskUrl: `https://ishoncourse.oncourse.cc/room/kiosk/${row.id}`,
      modifiedOn: new Date().toISOString(),
      name: row.values[0],
      notes: [],
      rules: [],
      seatedCapacity: row.values[2],
      siteId: row.id,
      tags: []
    };
  };

  this.getRooms = () => {
    return this.rooms;
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
