import {HttpService} from "../common/services/HttpService";
import { CommonError } from "../model/common/CommonError";
import { CourseClass } from "../model/web/CourseClass";
import { CourseClassesParams } from "../model/web/CourseClassesParams";

export class CourseClassesApi {
  constructor(private http: HttpService) {
  }

  getCourseClasses(courseClassesParams: CourseClassesParams): Promise<CourseClass[]> {
    return this.http.POST(`/classes`, courseClassesParams)
  }
}
