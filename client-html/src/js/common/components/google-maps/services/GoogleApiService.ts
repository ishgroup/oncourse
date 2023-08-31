import { GOOGLE_MAPS_API_KEY } from "../../../../constants/Config";
import { DefaultHttpService } from "../../../services/HttpService";

class GoogleApiService {
  readonly service = new DefaultHttpService();

  public getGeocodeDetails(address: string): Promise<JSON> {
    return this.service.GET(
      `https://maps.googleapis.com/maps/api/geocode/json?address=${address}&key=${atob(GOOGLE_MAPS_API_KEY)}`
    );
  }
}

export default new GoogleApiService();
