import { Course, CourseApi, Diff, Sale } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class CourseService {
  readonly courseApi = new CourseApi(new DefaultHttpService());

  public getCourse(id: number): Promise<Course> {
    return this.courseApi.get(id);
  }

  public create(course: Course): Promise<Course> {
    return this.courseApi.create(course);
  }

  public update(id: number, course: Course): Promise<Course> {
    return this.courseApi.update(id, course);
  }

  public remove(id: number): Promise<Course> {
    return this.courseApi.remove(id);
  }

  public duplicate(ids: number[]): Promise<Course> {
    return this.courseApi.duplicateCourse(ids);
  }

  public bulkEdit(diff: Diff): Promise<Course> {
    return this.courseApi.bulkChange(diff);
  }

  public getSellables(courseIds: number[]): Promise<Sale[]> {
    return this.courseApi.getSellables(courseIds);
  }
}

export default new CourseService();
