import { generateArraysOfRecords, getEntityResponse, removeItemByEntity } from "../../mockUtils";

export function mockMessage() {
  this.getMessages = () => this.messages;

  this.getMessage = id => {
    const row = this.messages.rows.find(row => row.id == id);
    return {
      id: row.id,
      createdOn: row.values[0],
      modifiedOn: row.values[0],
      creatorKey: row.values[7],
      htmlMessage: null,
      message:
        "26/08/2011Thank you for enrolling at ish onCourse Training.Your Reference Number is .Class List:------------ENRL-05 Introduction to Quick Enrol starting at 11:00 AM 13/03/2012  located at online:   please see ishoncourse.live1.oncourse.net.au/class/ENRL-05Please re-visit ishoncourse.live1.oncourse.net.au prior to your course to  check course materials, instructions and for possible changes.  If there are insufficient numbers classes have to be cancelled. We usually cancel classes when necessary, four working days prior to their scheduled start date. You will be advised by email and/or SMS.With online courses, you will be emailed a web link for attendance 15 minutes prior to your class commencing. Please ensure you have provided the correct email address and that you will have internet access available for the duration of the class.When the session is due to commence, you can dial into the conference bridge on 02 9550 5001.We trust that you will enjoy your course(s).Please do not hesitate to contact us should you need any further information.----------------------------Email: training@ish.com.auPhone: 02 9550 5001Fax: 02 9550 4001----------------------------",
      postDescription: null,
      sentToContactFullname: "Smith, Jenny",
      sms: null,
      subject: row.values[6]
    };
  };

  this.removeMessage = id => {
    this.messages = removeItemByEntity(this.messages, id);
  };

  this.getRecipientsByEntityName = entity => ({
    "students": {
      "sendSize": 1,
      "suppressToSendSize": 0,
      "withoutDestinationSize": 0
    },
    "activeStudents": {
      "sendSize": 0,
      "suppressToSendSize": 0,
      "withoutDestinationSize": 0
    },
    "withdrawnStudents": {
      "sendSize": 0,
      "suppressToSendSize": 0,
      "withoutDestinationSize": 0
    },
    "tutors": {
      "sendSize": 0,
      "suppressToSendSize": 0,
      "withoutDestinationSize": 0
    },
    "other": {
      "sendSize": 0,
      "suppressToSendSize": 0,
      "withoutDestinationSize": 0
    }
  });

  this.getMessageByType = messageType => {
    if (messageType === "Sms") {
      return "testing 123";
    }
    return '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">\n'
      + '<html xmlns="http://www.w3.org/1999/xhtml">\n'
      + '<head>\n'
      + '    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />\n'
      + '    <meta name="viewport" content="width=device-width, initial-scale=1" />\n'
      + '    <title>test</title>\n'
      + '    <style type="text/css">\n'
      + '        .ReadMsgBody {\n'
      + '            width: 100%;\n'
      + '        }\n'
      + '\n'
      + '        .ExternalClass {\n'
      + '            width: 100%;\n'
      + '        }\n'
      + '\n'
      + '        * {\n'
      + '            font-family: Helvetica, Arial, sans-serif;\n'
      + '        }\n'
      + '\n'
      + '        body {\n'
      + '            -webkit-font-smoothing: antialiased;\n'
      + '            -webkit-text-size-adjust: none;\n'
      + '            width: 100% !important;\n'
      + '            margin: 0 !important;\n'
      + '            height: 100%;\n'
      + '            color: #676767;\n'
      + '        }\n'
      + '\n'
      + '        img {\n'
      + '            max-width: 600px;\n'
      + '            outline: none;\n'
      + '            text-decoration: none;\n'
      + '            -ms-interpolation-mode: bicubic;\n'
      + '        }\n'
      + '\n'
      + '        a img {\n'
      + '            border: none;\n'
      + '        }\n'
      + '\n'
      + '        a {\n'
      + '            color: #676767;\n'
      + '            text-decoration: none !important;\n'
      + '        }\n'
      + '\n'
      + '        a span {\n'
      + '            color: #676767;\n'
      + '            text-decoration: none !important;\n'
      + '        }\n'
      + '\n'
      + '        table {\n'
      + '            border-collapse: collapse !important;\n'
      + '        }\n'
      + '\n'
      + '        .table-border-separate {\n'
      + '            border-collapse: separate !important;\n'
      + '        }\n'
      + '\n'
      + '        .container-for-gmail-android {\n'
      + '            /*min-width: 600px;*/\n'
      + '        }\n'
      + '\n'
      + '        table td {\n'
      + '            font-family: Helvetica, Arial, sans-serif;\n'
      + '            font-size: 14px;\n'
      + '            color: #777777;\n'
      + '            line-height: 21px;\n'
      + '            border-collapse: collapse;\n'
      + '        }\n'
      + '\n'
      + '        table.table-striped > tbody > tr:nth-of-type(odd) {\n'
      + '            background-color: rgba(0,0,0,.05);\n'
      + '        }\n'
      + '\n'
      + '        table.table-striped > thead > th {\n'
      + '            vertical-align: bottom;\n'
      + '            border-bottom: 2px solid #dee2e6;\n'
      + '        }\n'
      + '\n'
      + '        table.table-striped > tbody > tr > td {\n'
      + '            vertical-align: top;\n'
      + '        }\n'
      + '\n'
      + '        .bg-color {\n'
      + '            background-color: #f7f7f7;\n'
      + '        }\n'
      + '\n'
      + '        .text-center {\n'
      + '            text-align: center;\n'
      + '        }\n'
      + '\n'
      + '        .text-right {\n'
      + '            text-align: right;\n'
      + '        }\n'
      + '\n'
      + '        .pull-left {\n'
      + '            text-align: left;\n'
      + '        }\n'
      + '\n'
      + '        .header-sm {\n'
      + '            font-weight: 700;\n'
      + '            color: #4d4d4d;\n'
      + '            padding: 5px 0;\n'
      + '            font-size: 18px;\n'
      + '            line-height: 1.3;\n'
      + '        }\n'
      + '\n'
      + '        .content-padding {\n'
      + '            padding: 20px 0 30px;\n'
      + '        }\n'
      + '\n'
      + '        .mobile-header-padding-right {\n'
      + '            width: 290px;\n'
      + '            text-align: right;\n'
      + '            padding-left: 10px;\n'
      + '        }\n'
      + '\n'
      + '        .mobile-header-padding-left {\n'
      + '            width: 290px;\n'
      + '            text-align: left;\n'
      + '            padding-right: 10px;\n'
      + '        }\n'
      + '\n'
      + '        .mini-block-container {\n'
      + '            padding: 30px 0;\n'
      + '        }\n'
      + '\n'
      + '        .mini-block {\n'
      + '            background-color: #ffffff;\n'
      + '            border: 1px solid #cccccc;\n'
      + '            border-radius: 5px;\n'
      + '            padding: 30px 25px;\n'
      + '        }\n'
      + '\n'
      + '        .top-header {\n'
      + '            background-color: #ffffff;\n'
      + '            border-top: 1px solid #e5e5e5;\n'
      + '            border-bottom: 1px solid #e5e5e5;\n'
      + '            vertical-align: middle;\n'
      + '        }\n'
      + '\n'
      + '        .top-header .mobile-header-padding-left img {\n'
      + '            max-width: 300px;\n'
      + '            padding: 8px 0;\n'
      + '            width: 100%;\n'
      + '        }\n'
      + '        .top-header .mobile-header-padding-right img {\n'
      + '            max-width: 300px;\n'
      + '            padding: 8px 0;\n'
      + '            width: 100%;\n'
      + '        }\n'
      + '\n'
      + '        .footer-inner {\n'
      + '            padding: 25px 0 25px;\n'
      + '        }\n'
      + '\n'
      + '        .head-detail-left {\n'
      + '            text-align: left !important;\n'
      + '        }\n'
      + '\n'
      + '        .head-detail-right {\n'
      + '            text-align: right !important;\n'
      + '        }\n'
      + '\n'
      + '    </style>\n'
      + '\n'
      + '    <link href="https://fonts.googleapis.com/css2?family=Oxygen:wght@400;700&display=swap" rel="stylesheet" type="text/css" media="screen" />\n'
      + '\n'
      + '    <style type="text/css" media="screen">\n'
      + '        @media screen {\n'
      + '            * {\n'
      + '                font-family: \'Oxygen\', \'Helvetica Neue\', \'Arial\', \'sans-serif\' !important;\n'
      + '            }\n'
      + '        }\n'
      + '    </style>\n'
      + '\n'
      + '    <style type="text/css" media="only screen and (max-width: 480px)">\n'
      + '        @media only screen and (max-width: 480px) {\n'
      + '\n'
      + '            table[class*="container-for-gmail-android"] {\n'
      + '                min-width: 290px !important;\n'
      + '                width: 100% !important;\n'
      + '            }\n'
      + '\n'
      + '            table[class="w320"] {\n'
      + '                width: 320px !important;\n'
      + '            }\n'
      + '\n'
      + '            td[class*="mobile-header-padding-left"] {\n'
      + '                width: 160px !important;\n'
      + '                padding-left: 8px !important;\n'
      + '            }\n'
      + '\n'
      + '            td[class*="mobile-header-padding-right"] {\n'
      + '                width: 160px !important;\n'
      + '                padding-right: 8px !important;\n'
      + '            }\n'
      + '\n'
      + '            td[class="content-padding"] {\n'
      + '                padding: 5px 0 30px !important;\n'
      + '            }\n'
      + '\n'
      + '            td[class="mini-block-container"] {\n'
      + '                padding: 8px !important;\n'
      + '            }\n'
      + '\n'
      + '            td[class="mini-block"] {\n'
      + '                padding: 20px !important;\n'
      + '            }\n'
      + '\n'
      + '            .head-detail-left, .head-detail-right {\n'
      + '                width: 100% !important;\n'
      + '                text-align: left !important;\n'
      + '            }\n'
      + '\n'
      + '            .head-detail-right {\n'
      + '                margin-top: 15px;\n'
      + '            }\n'
      + '        }\n'
      + '    </style>\n'
      + '</head>\n'
      + '\n'
      + '<body bgcolor="#f7f7f7">\n'
      + '    <table class="container-for-gmail-android" width="100%" cellspacing="0" cellpadding="0" align="center">\n'
      + '        <tbody>\n'
      + '            <tr>\n'
      + '                <td class="top-header" width="100%" valign="top" height="80" align="center">\n'
      + '                    <center>\n'
      + '                        <table class="w320" width="100%" cellspacing="0" cellpadding="0">\n'
      + '                            <tbody>\n'
      + '                                <tr>\n'
      + '                                    <td class="header-sm mobile-header-padding-left">\n'
      + '                                        \n'
      + '                                        <img src="https://www.ish.com.au/assets/img/icons/logo.png" alt="logo"/>\n'
      + '                                        \n'
      + '                                    </td>\n'
      + '                                    <td class="mobile-header-padding-right">\n'
      + '                                        <!-- right header here -->\n'
      + '                                    </td>\n'
      + '                                </tr>\n'
      + '                            </tbody>\n'
      + '                        </table>\n'
      + '                    </center>\n'
      + '                </td>\n'
      + '            </tr>\n'
      + '        </tbody>\n'
      + '    </table>\n'
      + '\n'
      + '\n'
      + '<tr>\n'
      + '    <td align="center" valign="top" width="100%" class="bg-color content-padding">\n'
      + '        <center>\n'
      + '            <table cellpadding="0" cellspacing="0" width="600" class="w320">\n'
      + '                <tr>\n'
      + '                    <td class="mini-block-container">\n'
      + '                        <table cellpadding="0" cellspacing="0" width="100%" class="table-border-separate">\n'
      + '                            <tr>\n'
      + '                                <td class="mini-block pull-left" valign="top">\n'
      + '                                    test 123\n'
      + '                                </td>\n'
      + '                            </tr>\n'
      + '                        </table>\n'
      + '                    </td>\n'
      + '                </tr>\n'
      + '            </table>\n'
      + '        </center>\n'
      + '    </td>\n'
      + '</tr>\n'
      + '<center>\n'
      + '    <table class="w320" width="100%" cellspacing="0" cellpadding="0">\n'
      + '        <tbody>\n'
      + '        <tr>\n'
      + '            <td class="footer-inner">\n'
      + '\n'
      + '            </td>\n'
      + '        </tr>\n'
      + '        </tbody>\n'
      + '    </table>\n'
      + '</center>\n'
      + '\n'
      + '</body>\n'
      + '</html>\n'
      + '\n';
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

  return getEntityResponse({
    entity: "Message",
    rows,
    columns: [
      {
        title: "Date time",
        attribute: "createdOn",
        sortable: true,
        type: "Datetime"
      },
      {
        title: "Sent by",
        attribute: "createdBy.login"
      },
      {
        title: "Recipients",
        attribute: "recipientsString"
      },
      {
        title: "SMS",
        attribute: "isSms",
        type: "Boolean"
      },
      {
        title: "Email",
        attribute: "isEmail",
        type: "Boolean"
      },
      {
        title: "Post",
        attribute: "isPost",
        type: "Boolean"
      },
      {
        title: "Subject",
        attribute: "emailSubject",
        sortable: true
      },
      {
        title: "Creator key",
        attribute: "creatorKey",
        sortable: true
      }
    ],
    res: {
      sort: [{ attribute: "createdOn", ascending: true, complexAttribute: [] }]
    }
  });
}
