/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Faculty, FacultyApi } from "@api/model";
import { DefaultHttpService } from "../../../../common/services/HttpService";

class FacultyService {
  readonly facultyApi = new FacultyApi(new DefaultHttpService());

  public create(faculty: Faculty): Promise<number> {
    return this.facultyApi.create(faculty);
  }

  public get(id: number): Promise<Faculty> {
    return this.facultyApi.get(id);
  }

  public remove(id: number): Promise<any> {
    return this.facultyApi.remove(id);
  }

  public update(id: number, course: Faculty): Promise<any> {
    return this.facultyApi.update(id, course);
  }
}

export default new FacultyService();