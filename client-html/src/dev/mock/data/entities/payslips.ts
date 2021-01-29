import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

export function mockPayslips() {
  this.getPayslips = () => this.payslips;

  this.getPayslip = id => {
    const row = this.payslips.rows.find(row => row.id == id);
    return {
      id: row.id,
      tutorFullName: row.values[0],
      createdOn: row.values[2],
      modifiedOn: row.values[2],
      status: row.values[3],
      paylines: [
        {
          id: 1,
          dateFor: "2013-04-29",
          description: "Reimbursement of Prep Printing Costs",
          quantity: 1,
          value: 50
        },
        {
          id: 2,
          dateFor: "2013-04-29",
          description: "Reimbursement of Internet Costs",
          quantity: 1,
          value: 50
        },
        {
          id: 3,
          dateFor: "2015-08-19",
          description: "Cert IV in Project Management",
          quantity: 1,
          value: 30
        }
      ],
      privateNotes: null,
      publicNotes: null,
      tutorId: 1,
      tags: []
    };
  };

  this.createPayslip = item => {
    const data = JSON.parse(item);
    const payslips = this.payslips;
    const totalRows = payslips.rows;

    data.id = totalRows.length + 1;

    payslips.rows.push({
      id: data.id,
      values: [data.tutorFullName, data.refNumber, data.createdOn, data.status]
    });

    this.corporatePasses = payslips;
  };

  this.removePayslip = id => {
    this.payslips = removeItemByEntity(this.payslips, id);
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "tutorFullName", type: "string" },
    { name: "refNumber", type: "string" },
    { name: "createdOn", type: "Datetime" },
    { name: "status", type: "string" }
  ]).map(l => ({
    id: l.id,
    values: [l.tutorFullName, l.refNumber, l.createdOn, l.status]
  }));

  return getEntityResponse({
    entity: "Payslip",
    rows,
    columns: [
      {
        title: "Name",
        attribute: "contact.fullName",
        sortable: true,
        sortFields: ["contact.lastName", "contact.firstName", "contact.middleName"]
      },
      {
        title: "Payroll reference number",
        attribute: "contact.tutor.payrollRef"
      },
      {
        title: "Created",
        attribute: "createdOn",
        sortable: true,
        type: "Datetime"
      },
      {
        title: "Status",
        attribute: "status",
        sortable: true
      }
    ],
    res: {
      sort: [{ attribute: "createdOn", ascending: true, complexAttribute: [] }]
    }
  });
}
