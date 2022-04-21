import { getNestedCourseClassItem } from "../../../../js/containers/entities/courseClasses/utils";

const count = 1;
const id = 1;

describe("CourseClass getNestedCourseClassItem utils tests", () => {
  test("Match getNestedCourseClassItem test", () => {
    const result = getNestedCourseClassItem("Current", count, id);
    expect(result).toMatchObject({
      name: "Current",
      count,
      link: `/class?search=course.id is ${id}&filter=@Current_classes`,
      // eslint-disable-next-line max-len
      timetableLink: `/timetable/search?search=courseClass.course.id=${id} and courseClass.startDateTime < tomorrow and courseClass.endDateTime >= today and courseClass.isCancelled is false`
    });
  });
});
