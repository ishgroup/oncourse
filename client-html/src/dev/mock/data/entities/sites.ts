import { Site } from "@api/model";
import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

export function mockSites() {
  this.getSites = () => this.sites;

  this.getSite = (id: any): Site => {
    const row = this.sites.rows.find(site => Number(site.id) === Number(id));
    return {
      country: this.countries.find(c => Number(c.id) === Number(`20${row.id}`)),
      createdOn: "2021-02-01T06:09:45.466Z",
      documents: [],
      drivingDirections: "",
      id: row.id,
      isAdministrationCentre: true,
      isShownOnWeb: row.values[3],
      isVirtual: false,
      kioskUrl: `https://ishoncourse.oncourse.cc/site/kiosk/${row.id}`,
      latitude: -33.8863809,
      longitude: 151.2107548,
      modifiedOn: "2021-02-01T06:09:45.466Z",
      name: row.values[0],
      postcode: row.values[2],
      publicTransportDirections: "",
      rooms: [
        {
          createdOn: "2021-02-01T06:09:45.466Z",
          directions: null,
          documents: [],
          facilities: null,
          id: row.id,
          kioskUrl: `https://ishoncourse.oncourse.cc/room/kiosk/${row.id}`,
          modifiedOn: "2021-02-01T06:09:45.466Z",
          name: `room ${row.id}`,
          rules: [],
          seatedCapacity: Number(`${row.id}`),
          siteId: row.id,
          tags: []
        }
      ],
      rules: [],
      specialInstructions: "",
      state: "",
      street: `street ${row.id}`,
      suburb: row.values[1],
      tags: [
        this.getTag(1)
      ],
      timezone: this.timezones[row.id],
    };
  };

  this.createSite = item => {
    const data = JSON.parse(item);
    const sites = this.sites;
    const totalRows = sites.rows;

    data.id = totalRows.length + 1;

    sites.rows.push({
      id: data.id,
      values: [
        data.name,
        data.suburb,
        data.postcode,
        data.isShownOnWeb
      ]
    });

    this.sites = sites;
  };

  this.removeSite = id => {
    this.sites = removeItemByEntity(this.sites, id);
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

  return getEntityResponse({
    entity: "Site",
    rows,
    columns: [
      {
        title: "Name",
        attribute: "name",
        sortable: true,
        width: 100
      },
      {
        title: "Country",
        attribute: "country.name",
        sortable: true,
        visible: false,
        width: 100
      },
      {
        title: "State",
        attribute: "state",
        sortable: true,
        visible: false,
        width: 100
      },
      {
        title: "Suburb",
        attribute: "suburb",
        sortable: true,
        width: 100
      },
      {
        title: "Street",
        attribute: "street",
        sortable: true,
        visible: false,
        width: 100
      },
      {
        title: "Postcode",
        attribute: "postcode",
        sortable: true,
        width: 100
      },
      {
        title: "Local timezone",
        attribute: "localTimezone",
        sortable: true,
        visible: false
      },
      {
        title: "Administration centre",
        attribute: "isAdministrationCentre",
        sortable: true,
        type: "Boolean"
      },
      {
        title: "Shown on web",
        attribute: "isShownOnWeb",
        sortable: true,
        visible: false,
        type: "Boolean"
      },
      {
        title: "Is virtual",
        attribute: "isVirtual",
        sortable: true,
        visible: false,
        type: "Boolean"
      }
    ]
  });
}
