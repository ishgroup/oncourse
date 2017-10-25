import {HttpService} from "../common/services/HttpService";
import {CommonError} from "../model/common/CommonError";
import {Course} from "../model/web/Course";
import {CourseClass} from "../model/web/CourseClass";
import {CourseClassesParams} from "../model/web/CourseClassesParams";
import {CoursesParams} from "../model/web/CoursesParams";

export class CourseClassesApi {
  constructor(private http: HttpService) {
  }

  getCourseClasses(courseClassesParams: CourseClassesParams): Promise<CourseClass[]> {
    return this.http.POST(`/classes`, courseClassesParams);
  }
  getCourses(coursesParams: CoursesParams): Promise<Course[]> {
    return this.http.POST(`/courses`, coursesParams);
  }
}
