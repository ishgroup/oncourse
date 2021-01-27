/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Sorting, DataResponse } from "@api/model";
import { promiseResolve } from "../../MockAdapter";

export function listApiMock() {
  /**
   * List items
   * */
  this.api.onAny("/v1/list").reply(config => {
    const {
      offset, pageSize, search
    } = config.method === "get" ? config.params : JSON.parse(config.data);

    const {
      entity
    } = config.params;

    if (entity === "Script") {
      return promiseResolve(config, this.db.getScripts());
    }
    if (entity === "ExportTemplate") {
      return promiseResolve(config, this.db.getExportTemplates());
    }
    if (entity === "EmailTemplate") {
      return promiseResolve(config, this.db.getEmailTemplates());
    }
    if (entity === "Import") {
      return promiseResolve(config, this.db.getImportTemplates());
    }
    if (entity === "Report") {
      return promiseResolve(config, this.db.getReports());
    }
    if (entity === "ReportOverlay") {
      return promiseResolve(config, this.db.getReportOverlays());
    }
    if (entity === "Audit") {
      return promiseResolve(config, this.db.getAudits());
    }
    if (entity === "WaitingList") {
      return promiseResolve(config, this.db.getWaitingLists());
    }
    if (entity === "Site") {
      return promiseResolve(config, this.db.getSites());
    } if (entity === "Room") {
      return promiseResolve(config, this.db.getRooms());
    } if (entity === "Account") {
      return promiseResolve(config, this.db.getAccountList());
    } if (entity === "CourseClass") {
      return promiseResolve(config, this.db.getCourseClasses());
    } if (entity === "Contact") {
      return promiseResolve(config, this.db.getContacts());
    } if (entity === "Traineeship") {
      return promiseResolve(config, this.db.getTraineeships());
    } if (entity === "TraineeshipCourse") {
      return promiseResolve(config, this.db.getTraineeshipCourses());
    } if (entity === "Course") {
      return promiseResolve(config, this.db.getCourses());
    } if (entity === "Qualification") {
      return promiseResolve(config, this.db.getQualifications());
    } if (entity === "Assessment") {
      return promiseResolve(config, this.db.getAssessments());
    } if (entity === "Outcome") {
      return promiseResolve(config, this.db.getOutcomes());
    } if (entity === "Module") {
      return promiseResolve(config, this.db.getModules());
    } if (entity === "Enrolment") {
      return promiseResolve(config, this.db.getEnrolments());
    } if (entity === "ArticleProduct") {
      return promiseResolve(config, this.db.getArticleProducts());
    } if (entity === "Invoice") {
      return promiseResolve(config, this.db.getInvoices());
    } if (entity === "PaymentIn") {
      return promiseResolve(config, this.db.getPaymentsIn());
    } if (entity === "PaymentOut") {
      return promiseResolve(config, this.db.getPaymentsOut());
    } if (entity === "CorporatePass") {
      return promiseResolve(config, this.db.getCorporatePasses());
    } if (entity === "AccountTransaction") {
      return promiseResolve(config, this.db.getAccountTransactions());
    } if (entity === "Payslip") {
      return promiseResolve(config, this.db.getPayslips());
    } if (entity === "Banking") {
      return promiseResolve(config, this.db.getBankings());
    } if (entity === "Discount") {
      return promiseResolve(config, this.db.getDiscounts());
    } if (entity === "Document") {
      return promiseResolve(config, this.db.getDocuments());
    } if (entity === "Application") {
      return promiseResolve(config, this.db.getApplications());
    } if (entity === "Certificate") {
      return promiseResolve(config, this.db.getCertificates());
    } if (entity === "Survey") {
      return promiseResolve(config, this.db.getSurveys());
    } if (entity === "Message") {
      return promiseResolve(config, this.db.getMessages());
    } if (entity === "VoucherProduct") {
      return promiseResolve(config, this.db.getVoucherProducts());
    } if (entity === "MembershipProduct") {
      return promiseResolve(config, this.db.getMembershipProducts());
    } if (entity === "ProductItem") {
      return promiseResolve(config, this.db.getProductItems());
    }

    const list = this.db.getList(entity);
    const response = {} as DataResponse;

    // Current implementation only for audit list
    response.columns = [
      {
        title: "Id",
        attribute: "id",
        sortable: true,
        visible: false,
        width: 100
      },
      {
        title: "Message",
        attribute: "message",
        sortable: true,
        visible: true,
        width: 150
      },
      {
        title: "Action",
        attribute: "action",
        sortable: true,
        visible: true,
        width: 100
      },
      {
        title: "System User",
        attribute: "systemUser",
        sortable: false,
        visible: true,
        width: 250
      },
      {
        title: "Date",
        attribute: "date",
        sortable: false,
        visible: true,
        type: "Datetime",
        width: 250
      }
    ];

    response.rows = [];

    list.forEach(l => {
      response.rows.push({
        id: l.id,
        values: [l.id, l.message, l.action, l.systemUser, l.date]
      });
    });

    response.entity = entity;
    response.offset = offset;
    response.pageSize = pageSize;
    response.search = search;
    response.sort = response.columns.map(col => ({
      attribute: col.attribute,
      ascending: true
    })) as Sorting[];

    response.rows = response.rows.splice(offset, pageSize);

    return promiseResolve(config, response);
  });

  // todo: save columns data in DB
  this.api.onPut("v1/list/column").reply(config => promiseResolve(config, ""));

  this.api.onGet("/v1/list/plain").reply(config => {
    const { entity, search } = config.params;

    switch (entity) {
      case "ReportOverlay": {
        return promiseResolve(config, this.db.getPlainReportOverlays());
      }

      case "FundingSource": {
        return promiseResolve(config, this.db.getAvetmissExportPlainList());
      }

      case "CustomFieldType": {
        return promiseResolve(config, this.db.getCustomFields(search));
      }

      case "Contact": {
        return promiseResolve(config, this.db.getContactsPlainList());
      }

      case "Course": {
        return promiseResolve(config, this.db.getCourses());
      }

      case "Site": {
        return promiseResolve(config, this.db.getSites());
      }

      case "Assessment": {
        return promiseResolve(config, this.db.getPlainAssessmentList());
      }

      case "Outcome": {
        return promiseResolve(config, this.db.getPlainOutcomes());
      }

      case "Module": {
        return promiseResolve(config, this.db.getModules());
      }

      case "Enrolment": {
        return promiseResolve(config, this.db.getPlainEnrolments(config.params));
      }

      case "ArticleProduct": {
        return promiseResolve(config, this.db.getPlainArticleProductList());
      }

      case "InvoiceLine": {
        return promiseResolve(config, this.db.getPlainInvoiceLines());
      }

      case "Account": {
        return promiseResolve(config, this.db.getPlainAccounts());
      }

      case "Tax": {
        return promiseResolve(config, this.db.getPlainTaxes());
      }

      case "ContactRelationType": {
        return promiseResolve(config, this.db.getPlainContactRelationTypes());
      }

      case "Certificate": {
        return promiseResolve(config, this.db.getPlainCertificates(config.params));
      }

      case "Qualification": {
        return promiseResolve(config, this.db.getPlainQualifications());
      }

      case "PriorLearning": {
        return promiseResolve(config, this.db.getPlainPriorLearnings());
      }

      case "MembershipProduct": {
        return promiseResolve(config, this.db.getMembershipProductPlainList());
      }

      case "VoucherProduct": {
        return promiseResolve(config, this.db.getVoucherProductPlainList());
      }

      case "CourseClass": {
        return promiseResolve(config, this.db.getPlainCourseClassList());
      }

      case "ProductItem": {
        return promiseResolve(config, this.db.getPlainProductItemList());
      }

      case "Script": {
        return promiseResolve(config, this.db.getPlainScripts());
      }

      case "ExportTemplate": {
        return promiseResolve(config, this.db.getPlainExportTemplates());
      }

      case "EmailTemplate": {
        return promiseResolve(config, this.db.getPlainEmailTemplates());
      }

      case "Import": {
        return promiseResolve(config, this.db.getPlainImportTemplates());
      }

      case "Report": {
        return promiseResolve(config, this.db.getPlainReports());
      }

      default: {
        return promiseResolve(config, {});
      }
    }
  });

  this.api.onGet("/v1/list/export/pdf/template").reply(config => {
    const { entityName } = config.params;

    return promiseResolve(config, this.db.getPdfTemplate(entityName));
  });

  this.api.onGet("/v1/list/export/csv/template").reply(config => {
    const { entityName } = config.params;

    return promiseResolve(config, this.db.getCsvTemplate(entityName));
  });

  this.api.onGet("/v1/list/export/xml/template").reply(config => {
    const { entityName } = config.params;
    return promiseResolve(config, this.db.getXmlTemplate(entityName));
  });

  this.api.onGet("/v1/list/export/template").reply(config => {
    const { entityName } = config.params;
    return promiseResolve(config, this.db.listExportTemplate(entityName));
  });

  this.api.onGet("/v1/list/option/email/template").reply(config => {
    const { entityName } = config.params;

    return promiseResolve(config, this.db.listEmailTemplate(entityName));
  });

  this.api.onGet("/v1/list/entity/note").reply(config => {
    const { entityName } = config.params;
    return promiseResolve(config, this.db.getNotesByEntityName(entityName));
  });
}
