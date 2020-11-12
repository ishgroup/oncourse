import { DefaultHttpService } from "../../../common/services/HttpService";
import { SearchRequest, Session, TimetableApi } from "@api/model";

class TimetableService {
  readonly timetableApi = new TimetableApi(new DefaultHttpService());

  public findTimetableSessions(request: SearchRequest): Promise<Session[]> {
    return this.timetableApi.find(request);
  }

  public findTimetableSessionsForCourseClasses(ids: string): Promise<Session[]> {
    return this.timetableApi.getForClasses(ids);
  }

  public getTimetableSessionsByIds(ids: number[]): Promise<Session[]> {
    return this.timetableApi.get(ids.toString());
  }

  public getSessionTags(id: number[]): Promise<{ [key: string]: string }[]> {
    return this.timetableApi.getSessionsTags(id.toString());
  }

  public getTimetableSessionsDays(month: number, year: number, search: string): Promise<number[]> {
    // Temporary fix for Firefox due invalid (negative) date parsing
    // TODO Fix dates parsing in Firefox
    return this.timetableApi.getDates(
      String(month).replace(/[^0-9]/g, "") as any,
      String(year).replace(/[^0-9]/g, "") as any,
      search
    );
  }
}

export default new TimetableService();
