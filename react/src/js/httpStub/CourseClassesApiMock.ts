import { CourseClass } from "../model/web/CourseClass";
import { CourseClassesParams } from "../model/web/CourseClassesParams";
import {CourseClassesApi} from "../http/CourseClassesApi";
import {MockConfig} from "./mocks/MockConfig";

export class CourseClassesApiMock extends CourseClassesApi {
  public config: MockConfig = MockConfig.CONFIG;

  getCourseClasses(courseClassesParams: CourseClassesParams): Promise<CourseClass[]> {
    return this.config.createResponse(this.config.db.classes.entities.classes);
  }
}
