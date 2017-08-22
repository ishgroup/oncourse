import {HttpService} from "../common/services/HttpService";
import {CourseClass, CourseClassesParams} from "../model";

export class CourseClassesApi {
  constructor(private http: HttpService) {
  }

  getCourseClasses(courseClassesParams: CourseClassesParams): Promise<CourseClass[]> {
    return this.http.POST(`/classes`, courseClassesParams);
  }
}
