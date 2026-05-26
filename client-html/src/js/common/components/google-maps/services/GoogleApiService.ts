import { GoogleMapsCoordinates } from '../../../../model/google';
import { attachScript } from '../../../utils/common';

class GoogleApiService {
  constructor() {
    attachScript(`https://maps.googleapis.com/maps/api/js?key=${atob(process.env.GOOGLE_MAPS_API_KEY)}&libraries=places`);
  }

  public async getGeocodeDetails(address: string): Promise<GoogleMapsCoordinates> {
    const temp = document.createElement("div");
    const autocompleteService = new google.maps.places.AutocompleteService();
    const placesService = new google.maps.places.PlacesService(temp);
    const places = await autocompleteService.getPlacePredictions({ input: address });

    return new Promise((resolve, reject) => places.predictions[0]
      ? placesService.getDetails(
      { placeId: places.predictions[0]?.place_id },
      (place, status) => {
        if (status !== google.maps.places.PlacesServiceStatus.OK) {
          return reject("Cannot get details");
        }

        return resolve({
          lat: place.geometry.location.lat(),
          lng: place.geometry.location.lng()
        });
      }
    )
    : reject('Googgle API error')
    );
  }
}

export default new GoogleApiService();
