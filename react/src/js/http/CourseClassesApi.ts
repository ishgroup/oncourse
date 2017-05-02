import {HttpService} from "../common/services/HttpService";
import { CourseClass } from "../model/CourseClass";
import { CourseClassesParams } from "../model/CourseClassesParams";
import { ModelError } from "../model/ModelError";

export class CourseClassesApi {
  constructor(private http: HttpService) {
  }

  getCourseClasses(courseClassesParams: CourseClassesParams): Promise<CourseClass[]> {
    return this.http.POST(`/classes`, courseClassesParams)
  }
}
