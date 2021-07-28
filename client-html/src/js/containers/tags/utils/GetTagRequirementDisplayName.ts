import { TagRequirementType } from "@api/model";

const GetTagRequirementDisplayName = (type: TagRequirementType) => {
  switch (type) {
    case TagRequirementType.Application:
      return "Applications";
    case TagRequirementType.Assessment:
      return "Assessments";
    case TagRequirementType.Contact:
      return "Contacts";
    case TagRequirementType.Course:
      return "Courses";
    case TagRequirementType.Document:
      return "Documents";
    case TagRequirementType.Enrolment:
      return "Enrolments";
    case TagRequirementType.Invoice:
      return "Invoices";
    case TagRequirementType.Payslip:
      return "Payslips";
    case TagRequirementType.Room:
      return "Rooms";
    case TagRequirementType.Site:
      return "Sites";
    case TagRequirementType.Student:
      return "Students";
    case TagRequirementType.Tutor:
      return "Tutors";
    case TagRequirementType.Lead:
      return "Lead";
    case TagRequirementType.WaitingList:
      return "Waiting List";
    case TagRequirementType.CourseClass:
      return "Classes";
  }
};

export default GetTagRequirementDisplayName;
