import { generateArraysOfRecords } from "../../mockUtils";
export function mockMembershipProducts() {
    this.getMembershipProducts = () => {
        return this.membershipProducts;
    };
    this.getMembershipProduct = id => {
        const row = this.membershipProducts.rows.find(row => row.id == id);
        return {
            id: row.id,
            name: row.values[0],
            totalFee: row.values[1],
            feeExTax: row.values[1],
            code: row.values[2],
            description: "This is an example of a membership. You can have mutiple membership, have discounted memberhsip",
            expiryType: "1st January",
            expiryDays: null,
            taxId: 1,
            incomeAccountId: 1,
            status: "Can be purchased in office & online",
            corporatePasses: [],
            membershipDiscounts: [],
            createdOn: "2016-11-30T11:58:39.000Z",
            modifiedOn: "2018-03-23T13:53:18.000Z"
        };
    };
    this.createMembershipProduct = item => {
        const data = JSON.parse(item);
        const membershipProducts = this.membershipProducts;
        const totalRows = membershipProducts.rows;
        data.id = totalRows.length + 1;
        membershipProducts.rows.push({
            id: data.id,
            values: [data.name, data.code, data.totalFee]
        });
        this.membershipProducts = membershipProducts;
    };
    this.removeMembershipProduct = id => {
        this.membershipProducts.rows = this.membershipProducts.rows.filter(a => a.id !== id);
    };
    this.getMembershipProductPlainList = () => {
        const rows = generateArraysOfRecords(20, [
            { name: "id", type: "number" },
            { name: "code", type: "string" },
            { name: "name", type: "string" },
            { name: "price", type: "number" }
        ]).map(l => ({
            id: l.id,
            values: [l.code, l.name, l.price]
        }));
        const columns = [];
        const response = { rows, columns };
        response.entity = "MembershipProduct";
        response.offset = 0;
        response.filterColumnWidth = null;
        response.layout = null;
        response.pageSize = rows.length;
        response.search = null;
        response.count = null;
        response.sort = [];
        return response;
    };
    const rows = generateArraysOfRecords(20, [
        { name: "id", type: "number" },
        { name: "name", type: "string" },
        { name: "totalFee", type: "number" },
        { name: "code", type: "string" }
    ]).map(l => ({
        id: l.id,
        values: [l.name, l.totalFee, l.code]
    }));
    const columns = [
        {
            title: "Name",
            attribute: "name",
            sortable: true,
            visible: true,
            width: 200,
            type: null,
            system: null,
            sortFields: []
        },
        {
            title: "Price",
            attribute: "price_with_tax",
            sortable: true,
            visible: true,
            width: 200,
            type: "Money",
            system: null,
            sortFields: []
        },
        {
            title: "SKU",
            attribute: "sku",
            sortable: true,
            visible: true,
            width: 200,
            type: null,
            system: null,
            sortFields: []
        }
    ];
    const response = { rows, columns };
    response.entity = "MembershipProduct";
    response.offset = 0;
    response.filterColumnWidth = 200;
    response.layout = "Three column";
    response.pageSize = 20;
    response.search = "(isOnSale == true)";
    response.count = rows.length;
    response.filteredCount = rows.length;
    response.sort = [{ attribute: "name", ascending: true, complexAttribute: [] }];
    return response;
}
//# sourceMappingURL=membershipProducts.js.map