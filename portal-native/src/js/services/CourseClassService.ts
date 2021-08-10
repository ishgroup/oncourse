import { ClassAttendanceItem, CourseClass, CourseClassApi } from '@api/model';
import { DefaultHttpService } from '../constants/HttpService';

class CourseClassService {
  private courseClassApi = new CourseClassApi(new DefaultHttpService());

  public getClass(classId: string): Promise<CourseClass> {
    return this.courseClassApi.getClass(classId);
  }

  public markAttendance(item: ClassAttendanceItem): Promise<any> {
    return this.courseClassApi.markAttendance(item);
  }
}

export default new CourseClassService();
