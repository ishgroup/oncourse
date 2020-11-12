import { generateArraysOfRecords } from "../../mockUtils";
export function mockMessage() {
    this.getMessages = () => {
        return this.messages;
    };
    this.getMessage = id => {
        const row = this.messages.rows.find(row => row.id == id);
        return {
            id: row.id,
            createdOn: row.values[0],
            modifiedOn: row.values[0],
            creatorKey: row.values[7],
            htmlMessage: null,
            message: "26/08/2011Thank you for enrolling at ish onCourse Training.Your Reference Number is .Class List:------------ENRL-05 Introduction to Quick Enrol starting at 11:00 AM 13/03/2012  located at online:   please see ishoncourse.live1.oncourse.net.au/class/ENRL-05Please re-visit ishoncourse.live1.oncourse.net.au prior to your course to  check course materials, instructions and for possible changes.  If there are insufficient numbers classes have to be cancelled. We usually cancel classes when necessary, four working days prior to their scheduled start date. You will be advised by email and/or SMS.With online courses, you will be emailed a web link for attendance 15 minutes prior to your class commencing. Please ensure you have provided the correct email address and that you will have internet access available for the duration of the class.When the session is due to commence, you can dial into the conference bridge on 02 9550 5001.We trust that you will enjoy your course(s).Please do not hesitate to contact us should you need any further information.----------------------------Email: training@ish.com.auPhone: 02 9550 5001Fax: 02 9550 4001----------------------------",
            postDescription: null,
            sentToContactFullname: "Smith, Jenny",
            sms: null,
            subject: row.values[6]
        };
    };
    this.removeMessage = id => {
        this.messages.rows = this.messages.rows.filter(a => a.id !== id);
    };
    const rows = generateArraysOfRecords(20, [
        { name: "id", type: "number" },
        { name: "createdOn", type: "Datetime" },
        { name: "createdBy", type: "string" },
        { name: "recipientsString", type: "string" },
        { name: "isSms", type: "boolean" },
        { name: "isEmail", type: "boolean" },
        { name: "isPost", type: "boolean" },
        { name: "subject", type: "string" },
        { name: "creatorKey", type: "string" }
    ]).map(l => ({
        id: l.id,
        values: [l.createdOn, "Admin", l.recipientsString, "false", "true", "false", l.subject, l.creatorKey]
    }));
    const columns = [
        {
            title: "Date time",
            attribute: "createdOn",
            sortable: true,
            visible: true,
            width: 200,
            type: "Datetime",
            sortFields: []
        },
        {
            title: "Sent by",
            attribute: "createdBy.login",
            sortable: false,
            visible: true,
            width: 200,
            type: null,
            sortFields: []
        },
        {
            title: "Recipients",
            attribute: "recipientsString",
            sortable: false,
            visible: true,
            width: 200,
            type: null,
            sortFields: []
        },
        {
            title: "SMS",
            attribute: "isSms",
            sortable: false,
            visible: true,
            width: 200,
            type: "Boolean",
            sortFields: []
        },
        {
            title: "Email",
            attribute: "isEmail",
            sortable: false,
            visible: true,
            width: 200,
            type: "Boolean",
            sortFields: []
        },
        {
            title: "Post",
            attribute: "isPost",
            sortable: false,
            visible: true,
            width: 200,
            type: "Boolean",
            sortFields: []
        },
        {
            title: "Subject",
            attribute: "emailSubject",
            sortable: true,
            visible: true,
            width: 200,
            type: null,
            sortFields: []
        },
        {
            title: "Creator key",
            attribute: "creatorKey",
            sortable: true,
            visible: true,
            width: 200,
            type: null,
            sortFields: []
        }
    ];
    const response = { rows, columns };
    response.entity = "Message";
    response.offset = 0;
    response.filterColumnWidth = 200;
    response.layout = "Three column";
    response.pageSize = 20;
    response.search = null;
    response.count = rows.length;
    response.filteredCount = rows.length;
    response.sort = [{ attribute: "createdOn", ascending: true, complexAttribute: [] }];
    return response;
}
//# sourceMappingURL=messages.js.map