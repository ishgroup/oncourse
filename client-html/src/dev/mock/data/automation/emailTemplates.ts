import { Sorting } from "@api/model";
import { generateArraysOfRecords } from "../../mockUtils";

export function mockEmailTemplates() {
  this.getEmailTemplates = () => this.emailTemplates;

  this.getEmailTemplate = id => {
    const row = this.emailTemplates.rows.find(row => row.id == id);
    return {
      id: row.id,
      type: "Email",
      name: row.values[0],
      keyCode: row.values[1],
      enabled: row.values[2],
      entity: "Enrolment",
      subject: "CCE Student Survey",
      plainBody: "Help us to serve you better. Tell us what you think."
        + "\n\nHello ${enrolment.student.contact.firstName}"
        + "\n\nWe are fully committed to finding new ways to improve our performance. In keeping with this commitment, we'd love to receive feedback on your experience with ${enrolment.courseClass.course.name} in which you were recently enrolled."
        + "\n\nComplete our survey. It only takes a minute.\nPlease complete our short survey which should take less than 1 minute of your time. Click here to complete the survey now. "
        + "\n\nRate us on Google.\nWe'd also appreciate it if you gave us a rating online with Google: https://plus.google.com/+CentreforContinuingEducationNewtown/about?review=1",
      body:
        "${render(\"CCE-Header\")}    "
        + "\n              <!-- Title --> "
        + "\n              <table width=\"660\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"title\">"
        + "\n                <tbody>"
        + "\n                  <tr>"
        + "\n                    <td width=\"30\" height=\"36\"></td>"
        + "\n                    <td width=\"630\"></td>"
        + "\n                  </tr>"
        + "\n                  <tr>"
        + "\n                    <td></td>"
        + "\n                    <td class=\"title\" style=\"color:#333333; font:'Helvetica Neue', Helvetica, Arial, 'Lucida Grande', sans-serif; font-size:20px; line-height:24px;\"><strong style=\"color:#e64626;\">Help us to serve you better.</strong> Tell us what you think.</td>"
        + "\n                  </tr>"
        + "\n                  <tr>"
        + "\n                    <td width=\"30\" height=\"40\"></td>"
        + "\n                    <td></td>"
        + "\n                  </tr>"
        + "\n                </tbody>"
        + "\n              </table>"
        + "\n              <!-- End Title -->         "
        + "\n${render(\"CCE-Body-Start\")}"
        + "\n<h1> Hello ${enrolment.student.contact.firstName} </h1>"
        + "\n<p> We are fully committed to finding new ways to improve our performance. In keeping with this commitment, we'd love to receive feedback on your experience with <strong>${enrolment.courseClass.course.name}</strong> in which you were recently enrolled.</p>"
        + "\n<h2> <strong>Complete our survey.</strong> It only takes a minute.</h2>"
        + "\n<p> Please complete our short survey which should take less than 1 minute of your time. <a href=\"${enrolment.student.getPortalLink(enrolment)}\" target=\"_blank\">Click here</a> to complete the survey now.</p>"
        + "\n<h2><strong>Rate us on Google.</strong></h2>"
        + "\n<p>We'd also appreciate it if you <a href=\"https://plus.google.com/+CentreforContinuingEducationNewtown/about?review=1\" target=\"_blank\"> gave us a rating online with Google</a></p>"
        + "\n${render(\"CCE-Body-End\")}"
        + "\n${render(\"CCE-Footer\")}",
      variables: [],
      options: [],
      createdOn: "2019-04-12T07:59:34.000Z",
      modifiedOn: "2019-04-12T07:59:34.000Z",
      description: "Email template"
    };
  };

  this.getPlainEmailTemplates = () => {
    const rows = generateArraysOfRecords(20, [
      { name: "id", type: "number" },
      { name: "name", type: "string" },
      { name: "keyCode", type: "string" },
      { name: "enabled", type: "string" }
    ]).map(l => ({
      id: l.id,
      values: [l.name, l.keyCode, l.enabled]
    }));

    const columns = [];

    const response = { rows, columns } as any;
    response.entity = "EmailTemplate";
    response.count = null;
    response.filterColumnWidth = null;
    response.filteredCount = null;
    response.layout = null;
    response.sort = [];
    return response;
  };

  this.createEmailTemplate = item => {
    const data = JSON.parse(item);
    const emailTemplates = this.emailTemplates;
    const totalRows = emailTemplates.rows;

    data.id = totalRows.length + 1;

    emailTemplates.rows.push({
      id: data.id,
      values: [data.name, data.keyCode, data.enabled]
    });

    this.emailTemplates = emailTemplates;
  };

  this.removeEmailTemplate = id => {
    this.emailTemplates.rows = this.emailTemplates.rows.filter(a => a.id !== id);
  };
  
  this.getEmailTemplatesByEntityName = entityName => {
    switch (entityName) {
      case "Account": 
        return [];
      default: 
        return [];
    }
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "name", type: "string" },
    { name: "keyCode", type: "string" },
    { name: "enabled", type: "string" }
  ]).map(l => ({
    id: l.id,
    values: [l.name, l.keyCode, l.enabled]
  }));

  const columns = [
    {
      title: "Name",
      attribute: "name",
      sortable: true,
      visible: true,
      width: 200
    },
    {
      title: "Key code",
      attribute: "keyCode",
      sortable: true,
      visible: true,
      width: 200
    },
    {
      title: "Enabled",
      attribute: "enabled",
      sortable: false,
      visible: true,
      width: 200
    }
  ];

  const response = { rows, columns } as any;

  response.entity = "EmailTemplate";
  response.offset = 0;
  response.pageSize = 10;
  response.search = "";
  response.count = rows.length;
  response.layout = null;
  response.filteredCount = null;
  response.filterColumnWidth = null;
  response.sort = response.columns.map(col => ({
    attribute: col.attribute,
    ascending: true
  })) as Sorting[];

  return response;
}
