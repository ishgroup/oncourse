import {CourseClass, CourseClassesParams} from "../../js/model";
import {CourseClassesApi} from "../../js/http/CourseClassesApi";
import {MockConfig} from "./mocks/MockConfig";

export class CourseClassesApiMock extends CourseClassesApi {
  public config: MockConfig;

  constructor(config: MockConfig) {
    super(null);
    this.config = config;
  }

  getCourseClasses(courseClassesParams: CourseClassesParams): Promise<CourseClass[]> {
    return this.config.createResponse(this.config.db.classes.entities.classes);
  }
}
