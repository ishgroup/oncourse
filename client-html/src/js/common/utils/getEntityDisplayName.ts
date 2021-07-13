import { Category } from "@api/model";

export const getEntityDisplayName = (entity: string): Category => {
  switch (entity) {
    case "Audit": {
      return "Audit Logging";
    }
    case "Account": {
      return "Accounts";
    }
    case "Banking": {
      return "Banking Deposits";
    }
    case "CorporatePass": {
      return "Corporate Pass";
    }
    case "Invoice": {
      return "Invoices";
    }
    case "Module": {
      return "Units Of Competency";
    }
    case "PaymentIn": {
      return "Payments In";
    }
    case "PaymentOut": {
      return "Payments Out";
    }
    case "Payslip": {
      return "Tutor pay";
    }
    case "Qualification": {
      return "Qualifications";
    }
    case "Room": {
      return "Rooms";
    }
    case "Site": {
      return "Sites";
    }
    case "AccountTransaction": {
      return "Transactions";
    }
    case "CourseClass": {
      return "Classes";
    }
    case "Contact": {
      return "Contacts";
    }
    case "Enrolment": {
      return "Enrolments";
    }
    case "Lead": {
      return "Leads";
    }
    case "WaitingList": {
      return "Waiting lists";
    }
    case "ArticleProduct": {
      return "Products";
    }
    case "Application": {
      return "Applications";
    }
    case "Course": {
      return "Courses";
    }
    case "VoucherProduct": {
      return "Voucher Types";
    }
    case "MembershipProduct": {
      return "Memberships";
    }
    case "Certificate": {
      return "Certificates";
    }
    case "Discount": {
      return "Discounts";
    }
    case "Survey": {
      return "Student Feedback";
    }
    case "ProductItem": {
      return "Sales";
    }
    case "Document": {
      return "Documents";
    }
    case "Outcome": {
      return "Outcomes";
    }
    case "Assessment": {
      return "Assessment tasks";
    }
    case "AssessmentSubmission": {
      return "Assessment submissions";
    }
    case "Message": {
      return "Messages";
    }
    case "Traineeship": {
      return "Traineeships";
    }
    case "PriorLearning": {
      return "Prior learnings";
    }
    default: {
      return entity as any;
    }
  }
};

export const getSingleEntityDisplayName = (entity: string): string => {
  switch (entity) {
    case "CorporatePass": {
      return "Corporate Pass";
    }
    case "Module": {
      return "Unit Of Competency";
    }
    case "PaymentIn": {
      return "Payment In";
    }
    case "PaymentOut": {
      return "Payment Out";
    }
    case "Payslip": {
      return "Tutor pay";
    }
    case "AccountTransaction": {
      return "Transaction";
    }
    case "CourseClass": {
      return "Class";
    }
    case "Lead": {
      return "Lead";
    }
    case "WaitingList": {
      return "Waiting list";
    }
    case "ArticleProduct": {
      return "Product";
    }
    case "VoucherProduct": {
      return "Voucher";
    }
    case "MembershipProduct": {
      return "Membership";
    }
    case "Survey": {
      return "Student Feedback";
    }
    case "ProductItem": {
      return "Sale";
    }
    case "Assessment": {
      return "Assessment task";
    }
    case "AssessmentSubmission": {
      return "Assessment submission";
    }
    default: {
      return entity;
    }
  }
};
