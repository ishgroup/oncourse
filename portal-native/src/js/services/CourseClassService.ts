import { CourseClass, CourseClassApi } from '@api/model';
import { DefaultHttpService } from '../constants/HttpService';

class CourseClassService {
  private courseClassApi = new CourseClassApi(new DefaultHttpService());

  public getClass(classId: string): Promise<CourseClass> {
    return this.courseClassApi.getClass(classId);
  }
}

export default new CourseClassService();
