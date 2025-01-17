import { Enrolment } from "@api/model";
import { DefaultEpic } from "../../common/Default.Epic";
import { getCourseClassEnrolments, setCourseClassEnrolments } from "../../../js/containers/entities/courseClasses/actions";
import { EpicGetCourseClassEnrolments } from "../../../js/containers/entities/courseClasses/epics/EpicGetCourseClassEnrolments";

describe("Get course class enrolments epic tests", () => {
  it("EpicGetCourseClassEnrolments should returns correct values", () => DefaultEpic({
    action: () => getCourseClassEnrolments(1),
    epic: EpicGetCourseClassEnrolments,
    processData: mockedApi => {
      const response = mockedApi.db.getPlainEnrolments({
        columns: "createdOn,status,student.contact.fullName,student.contact.id",
        search: "courseClass.id is 1 and ( status is IN_TRANSACTION or status is QUEUED or status is SUCCESS )"
      });

      const enrolments = response.rows.map(({ id, values }) => ({
        id,
        createdOn: values[0],
        status: values[1] as Enrolment["status"],
        student: values[2],
        contactId: values[3],
      }));

      enrolments.sort((a, b) => (a.createdOn > b.createdOn ? 1 : -1));

      return [setCourseClassEnrolments(enrolments)];
    }
  }));
});
