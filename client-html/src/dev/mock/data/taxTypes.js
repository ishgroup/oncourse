import { generateArraysOfRecords } from "../mockUtils";
export function mockTaxTypes() {
    this.getPlainTaxes = () => {
        const rows = generateArraysOfRecords(20, [
            { name: "id", type: "number" },
            { name: "code", type: "number" },
            { name: "editable", type: "boolean" },
            { name: "systemType", type: "boolean" }
        ]).map(l => ({
            id: l.id,
            values: [l.code, true, false, true]
        }));
        const columns = [];
        const response = { rows, columns };
        response.entity = "Tax";
        response.offset = 0;
        response.filterColumnWidth = null;
        response.layout = null;
        response.pageSize = 10;
        response.search = null;
        response.count = null;
        response.sort = [];
        return response;
    };
    this.saveTaxType = items => {
        this.taxTypes = items;
    };
    this.removeTaxType = id => {
        this.taxTypes = this.taxTypes.filter(it => it.id !== id);
    };
    return [
        {
            id: 886543,
            code: "56r76387",
            editable: false,
            systemType: true,
            gst: true,
            rate: 0.5,
            payableAccountId: 1,
            receivableAccountId: 3,
            description: ""
        },
        {
            id: 5684452,
            code: "2345245 ",
            editable: true,
            systemType: false,
            gst: false,
            rate: 0.1,
            payableAccountId: 2,
            receivableAccountId: 4,
            description: "Australian GST"
        },
        {
            id: 32435,
            code: "7456241",
            editable: true,
            systemType: true,
            gst: true,
            rate: 0.4,
            payableAccountId: 1,
            receivableAccountId: 3,
            description: "GST exempt"
        }
    ];
}
//# sourceMappingURL=taxTypes.js.map