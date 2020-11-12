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
            title: "Site",
            attribute: "site.name",
            sortable: true,
            visible: true,
            width: 200,
            type: null,
            sortFields: []
        },
        {
            title: "Seated capacity",
            attribute: "seatedCapacity",
            sortable: true,
            visible: true,
            width: 200,
            type: null,
            sortFields: []
        }
    ];
    const response = { rows, columns };
    response.entity = "Room";
    response.offset = 0;
    response.filterColumnWidth = 200;
    response.layout = "Three column";
    response.pageSize = 20;
    response.search = null;
    response.count = rows.length;
    response.filteredCount = rows.length;
    response.sort = [{
            ascending: true,
            attribute: "name",
            complexAttribute: []
        }];
    return response;
}
//# sourceMappingURL=rooms.js.map