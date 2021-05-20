import React from "react";
import Typography from "@material-ui/core/Typography";
import { MessageTemplateEntityName } from "../constants";
import { EntityName } from "../../../model/entities/common";

export const mapMessageAvailableFrom = (entity: MessageTemplateEntityName) => {
  switch (entity) {
    case "Application":
      return ["Applications"];

    case "Article":
      return ["Sales"];

    case "Contact":
      return ["Applications", "Contacts", "Classes", "Enrolments", "Invoices", "Payments In", "Payments Out", "Sales", "Waiting Lists"];

    case "CourseClass":
      return ["Classes"];

    case "CourseClassTutor":
      return ["Classes"];

    case "Enrolment":
      return ["Enrolments", "Classes"];

    case "Invoice":
      return ["Invoices"];

    case "Membership":
      return ["Sales"];

    case "PaymentIn":
      return ["Payments In"];

    case "PaymentOut":
      return ["Payments Out"];

    case "Payslip":
      return ["Tutor Pay"];

    case "ProductItem":
      return ["Sales"];

    case "Voucher":
      return ["Sales"];

    case "WaitingList":
      return ["Waiting Lists"];
  }

  return [];
};

export const mapAvailableFrom = (entity: EntityName): EntityName[] => {
  switch (entity) {
    case "Account":
      return ["Account"];

    case "AccountTransaction":
      return ["AccountTransaction", "Account", "Contact"];

    case "Application":
      return ["Application"];

    case "Article":
      return ["ProductItem"];

    case "ArticleProduct":
      return ["ArticleProduct"];

    case "Assessment":
      return ["Assessment"];

    case "Audit":
      return ["Audit"];

    case "Banking":
      return ["Banking"];

    case "Certificate":
      return ["Certificate"];

    case "ClassCost":
      return ["CourseClass"];

    case "Contact":
      return ["Contact"];

    case "CorporatePass":
      return ["CorporatePass"];

    case "Course":
      return ["Course"];

    case "CourseClass":
      return ["CourseClass"];

    case "CourseClassTutor":
      return ["CourseClass"];

    case "Discount":
      return ["Discount"];

    case "DiscountCourseClass":
      return ["CourseClass"];

    case "Enrolment":
      return ["Enrolment", "CourseClass"];

    case "Invoice":
      return ["Invoice"];

    case "InvoiceLine":
      return ["Invoice"];

    case "Membership":
      return ["ProductItem"];

    case "MembershipProduct":
      return ["MembershipProduct"];

    case "Message":
      return ["Message"];

    case "Module":
      return ["Module"];

    case "Outcome":
      return ["Outcome", "CourseClass"];

    case "PayLine":
      return ["Payslip"];

    case "PaymentIn":
      return ["PaymentIn"];

    case "PaymentInterface":
      return ["Banking"];

    case "PaymentOut":
      return ["PaymentOut"];

    case "Payslip":
      return ["Payslip"];

    case "ProductItem":
      return ["ProductItem"];

    case "Qualification":
      return ["Qualification"];

    case "Room":
      return ["Room"];

    case "Script":
      return ["Script"];

    case "Session":
      return ["CourseClass", "Course", "Room", "Site"];

    case "Site":
      return ["Site"];

    case "Student":
      return ["Contact", "CourseClass"];

    case "Survey":
      return ["Survey"];

    case "TrainingPackage":
      return ["TrainingPackage"];

    case "Tutor":
      return ["Contact"];

    case "TutorAttendance":
      return ["CourseClass"];

    case "Voucher":
      return ["ProductItem"];

    case "VoucherProduct":
      return ["VoucherProduct"];

    case "WaitingList":
      return ["WaitingList"];
  }

  return [];
};

const AvailableFromItem = React.memo<any>(({ item }) => (
  <Typography variant="body2" className="centeredFlex pb-0-5" style={{ maxHeight: "24px" }}>
    {item}
  </Typography>
  ));

const AvailableFrom = React.memo<any>(({ items }) => (
  <div>
    <div className="centeredFlex pb-1">
      <div className="heading">Available From</div>
    </div>

    {items.map((i, n) => (
      <AvailableFromItem key={n} item={i} />
      ))}
  </div>
  ));

export default AvailableFrom;
