import {CourseClassesParams, CourseClass, Course} from "../../model";
import {CourseClassesApi} from "../../http/CourseClassesApi";

export class CourseClassService {
  api: CourseClassesApi;

  constructor(api: CourseClassesApi) {
    this.api = api;
  }

  getCourseClasses = (params: CourseClassesParams): Promise<CourseClass[]> => {
    return this.api.getCourseClasses(params);
  }
}

/**
 * Convert html properties which was got from HTMLElement with marker HTMLMarkers.ENROL_BUTTON to CourseClass
 */
export const htmlProps2CourseClass = (props: { [key: string]: any }): CourseClass => {
  const result: CourseClass = new CourseClass();
  result.course = new Course();
  result.course.id = props.courseId;
  result.course.code = props.courseCode;
  result.course.name = props.courseName;
  result.course.description = props.courseDescription;

  result.id = props.id;
  result.code = props.code;
  result.hasAvailablePlaces = props.hasAvailablePlaces;
  result.availableEnrolmentPlaces = props.availableEnrolmentPlaces;
  result.isFinished = props.isFinished;
  result.isCancelled = props.isCancelled;
  result.isAllowByApplication = props.isAllowByApplication;
  result.isPaymentGatewayEnabled = props.isPaymentGatewayEnabled;
  return result;
};

