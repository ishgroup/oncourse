import { Holiday } from "@api/model";
import { HttpService } from "../../../common/services/HttpService";

export class PreferencesApi {
  constructor(private http: HttpService) {}

  getPreferences(query): Promise<any> {
    return this.http.GET(`/preferences`, { params: { cayenneExp: query } });
  }

  savePreferences(fields): Promise<any> {
    return this.http.POST(`/preferences`, { fields });
  }

  getHolidays(): Promise<Holiday[]> {
    return this.http.GET(`/holidays`);
  }

  saveHolidays(items: Holiday[]): Promise<any> {
    return this.http.POST(`/holidays`, { items });
  }

  deleteHolidays(id: number): Promise<any> {
    return this.http.DELETE(`/holidays/${id}`);
  }

  getAccounts(types): Promise<any> {
    return this.http.GET(`/accounts`, { params: { types } });
  }
}
