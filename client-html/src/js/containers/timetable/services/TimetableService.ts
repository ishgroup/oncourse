/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { SearchRequest, Session, TimetableApi } from "@api/model";
import { DefaultHttpService } from "../../../common/services/HttpService";

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

  public getTimetableSessionsDays(month: number, year: number, search: SearchRequest): Promise<number[]> {
    return this.timetableApi.getDates(month, year, search);
  }
}

export default new TimetableService();
