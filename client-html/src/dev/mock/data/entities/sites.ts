import { generateArraysOfRecords } from "../../mockUtils";

export function mockSites() {
  this.getSite = id => {
    const row = this.sites.rows.find(row => row.id == id);
    return {
      country: this.countries.find(c => c.id == `20${row.id}`),
      createdOn: new Date().toISOString(),
      documents: [],
      drivingDirections: null,
      id: row.id,
      isAdministrationCentre: true,
      isShownOnWeb: row.values[3],
      isVirtual: false,
      kioskUrl: `https://ishoncourse.oncourse.cc/site/kiosk/${row.id}`,
      latitude: -33.8863809,
      longitude: 151.2107548,
      modifiedOn: new Date().toISOString(),
      name: row.values[0],
      notes: [],
      postcode: row.values[2],
      publicTransportDirections: null,
      rooms: [
        {
          createdOn: new Date().toISOString(),
          directions: null,
          documents: [],
          facilities: null,
          id: row.id,
          kioskUrl: `https://ishoncourse.oncourse.cc/room/kiosk/${row.id}`,
          modifiedOn: new Date().toISOString(),
          name: `room ${row.id}`,
          notes: [],
          rules: [],
          seatedCapacity: `${row.id}`,
          siteId: row.id,
          tags: []
        }
      ],
      rules: [],
      specialInstructions: null,
      state: "",
      street: `street ${row.id}`,
      suburb: row.values[1],
      tags: [
        this.getTag(1)
      ],
      timezone: this.timezones[row.id],
    }
  };

  this.getSites = () => {
    return this.sites;
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "name", type: "string" },
    { name: "suburb", type: "string" },
    { name: "postcode", type: "string" },
    { name: "isShownOnWeb", type: "boolean" }
  ]).map(l => ({
    id: l.id,
    values: [l.name, l.suburb, l.postcode, true]
  }));

  const columns = [
    {
      title: "Name",
      attribute: "name",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    },
    {
      title: "Country",
      attribute: "country.name",
      sortable: true,
      visible: false,
      width: 100,
      type: null,
      sortFields: []
    },
    {
      title: "State",
      attribute: "state",
      sortable: true,
      visible: false,
      width: 100,
      type: null,
      sortFields: []
    },
    {
      title: "Suburb",
      attribute: "suburb",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    },
    {
      title: "Street",
      attribute: "street",
      sortable: true,
      visible: false,
      width: 100,
      type: null,
      sortFields: []
    },
    {
      title: "Postcode",
      attribute: "postcode",
      sortable: true,
      visible: true,
      width: 100,
      type: null,
      sortFields: []
    },
    {
      title: "Local timezone",
      attribute: "localTimezone",
      sortable: true,
      visible: false,
      width: 200,
      type: null,
      sortFields: []
    },
    {
      title: "Administration centre",
      attribute: "isAdministrationCentre",
      sortable: true,
      visible: true,
      width: 200,
      type: "Boolean",
      sortFields: []
    },
    {
      title: "Shown on web",
      attribute: "isShownOnWeb",
      sortable: true,
      visible: false,
      width: 200,
      type: "Boolean",
      sortFields: []
    },
    {
      title: "Is virtual",
      attribute: "isVirtual",
      sortable: true,
      visible: false,
      width: 200,
      type: "Boolean",
      sortFields: []
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "Site";
  response.offset = 0;
  response.filterColumnWidth = 200;
  response.layout = "Three column";
  response.pageSize = 20;
  response.search = null;
  response.count = rows.length;
  response.sort = [];

  return response;
}
