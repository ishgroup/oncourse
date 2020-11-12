import { generateArraysOfRecords } from "../../mockUtils";
import { format } from "date-fns";
export function mockInvoices() {
    this.getInvoices = () => {
        return this.invoices;
    };
    this.getInvoice = id => {
        const row = this.invoices.rows.find(row => row.id == id);
        return {
            id: row.id,
            amountOwing: row.values[4],
            billToAddress: "",
            contactId: 323,
            contactName: row.values[3],
            createdByUser: "admin",
            createdOn: new Date().toISOString(),
            customerReference: null,
            dateDue: format(new Date(row.values[2]), "yyyy-MM-dd"),
            invoiceDate: format(new Date(row.values[2]), "yyyy-MM-dd"),
            invoiceLines: [
                {
                    id: 461,
                    title: "Student, New Email Filtering and Security",
                    quantity: 1.0,
                    unit: null,
                    incomeAccountId: 15,
                    incomeAccountName: "Student enrolments 41000",
                    cosAccountId: null,
                    cosAccountName: null,
                    priceEachExTax: 120.0,
                    discountEachExTax: 0.0,
                    taxEach: 12.0,
                    taxId: 1,
                    taxName: "Australian GST",
                    description: "SPAM-07 Email Filtering and Security",
                    courseClassId: null,
                    courseName: null,
                    courseCode: null,
                    classCode: null,
                    enrolmentId: null,
                    enrolledStudent: null,
                    courseId: null
                }
            ],
            invoiceNumber: row.values[0],
            modifiedOn: new Date().toISOString(),
            notes: [],
            overdue: row.values[6],
            paymentPlans: [
                {
                    id: 461,
                    date: "2011-08-01",
                    type: "Invoice office",
                    successful: true,
                    amount: 132.0,
                    entityName: "Invoice"
                },
                {
                    id: 517,
                    date: "2018-04-17",
                    type: "Payment due",
                    successful: true,
                    amount: 132.0,
                    entityName: "InvoiceDueDate"
                },
                {
                    id: 381,
                    date: "2011-08-01",
                    type: "payment in (Credit card)",
                    successful: false,
                    amount: 132.0,
                    entityName: "PaymentIn"
                },
                {
                    id: 583,
                    date: "2011-08-18",
                    type: "payment in (Credit card)",
                    successful: false,
                    amount: 132.0,
                    entityName: "PaymentIn"
                },
                {
                    id: 1875,
                    date: "2012-12-20",
                    type: "payment in (Credit card)",
                    successful: false,
                    amount: 132.0,
                    entityName: "PaymentIn"
                },
                {
                    id: 1878,
                    date: "2012-12-20",
                    type: "payment in (Credit card)",
                    successful: false,
                    amount: 132.0,
                    entityName: "PaymentIn"
                },
                {
                    id: 1906,
                    date: "2012-12-20",
                    type: "payment in (Credit card)",
                    successful: false,
                    amount: 132.0,
                    entityName: "PaymentIn"
                }
            ],
            publicNotes: null,
            sendEmail: false,
            shippingAddress: null,
            source: row.values[1],
            total: row.values[5]
        };
    };
    this.createInvoice = item => {
        const data = JSON.parse(item);
        const invoices = this.invoices;
        const totalRows = invoices.rows;
        data.id = totalRows.length + 1;
        invoices.rows.push({
            id: data.id,
            values: [
                data.invoiceNumber,
                data.source,
                data.dateDue,
                data.contactName,
                data.amountOwing,
                data.total,
                data.overdue
            ]
        });
        this.invoices = invoices;
    };
    this.removeInvoice = id => {
        this.invoices = this.invoices.rows.filter(m => m.id !== id);
    };
    const rows = generateArraysOfRecords(20, [
        { name: "id", type: "number" },
        { name: "invoiceNumber", type: "number" },
        { name: "source", type: "string" },
        { name: "dateDue", type: "Datetime" },
        { name: "contactName", type: "string" },
        { name: "amountOwing", type: "number" },
        { name: "total", type: "number" },
        { name: "overdue", type: "number" }
    ]).map(l => ({
        id: l.id,
        values: [l.invoiceNumber, l.source, l.dateDue, l.contactName, 132, 132, 132]
    }));
    const columns = [
        {
            title: "Invoice number",
            attribute: "invoiceNumber",
            type: null,
            sortable: true,
            visible: true,
            width: 200,
            sortFields: []
        },
        {
            title: "Source",
            attribute: "source",
            type: null,
            sortable: true,
            visible: true,
            width: 200,
            sortFields: []
        },
        {
            title: "Date due",
            attribute: "dateDue",
            type: "Datetime",
            sortable: true,
            visible: true,
            width: 200,
            sortFields: []
        },
        {
            title: "Name",
            attribute: "contact.full_name",
            type: null,
            sortable: true,
            visible: true,
            width: 200,
            sortFields: ["contact.lastName", "contact.firstName", "contact.middleName"]
        },
        {
            title: "Owing",
            attribute: "amountOwing",
            type: "Money",
            sortable: true,
            visible: true,
            width: 200,
            sortFields: []
        },
        {
            title: "Total",
            attribute: "totalIncTax",
            type: "Money",
            sortable: false,
            visible: true,
            width: 200,
            sortFields: []
        },
        {
            title: "Overdue",
            attribute: "overdue",
            type: "Money",
            sortable: true,
            visible: true,
            width: 200,
            sortFields: []
        }
    ];
    const response = { rows, columns };
    response.entity = "Invoice";
    response.offset = 0;
    response.filterColumnWidth = 200;
    response.layout = "Three column";
    response.pageSize = 20;
    response.search = null;
    response.count = rows.length;
    // response.filteredCount = rows.length;
    response.sort = [
        {
            attribute: "invoiceNumber",
            ascending: true,
            complexAttribute: []
        }
    ];
    return response;
}
//# sourceMappingURL=invoices.js.map