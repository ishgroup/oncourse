import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { change } from "redux-form";
import { State } from "../../../reducers/state";
import { getGeocodeDetails } from "./actions";

interface Props {
  dispatch: any;
  latPath: string;
  longPath: string;
  addressString: string;
  form: string;
  apiResponse?: {
    lat: number;
    lng: number;
  };
  getGeocode?: (address: string) => void;
}

class CoordinatesValueUpdater extends React.PureComponent<Props, any> {
  componentDidUpdate(prevProps) {
    const { dispatch, latPath, longPath, addressString, form, getGeocode, apiResponse } = this.props;

    if (prevProps.addressString !== addressString) {
      getGeocode(addressString);
    }

    if (prevProps.apiResponse !== apiResponse) {
      dispatch(change(form, latPath, apiResponse.lat));
      dispatch(change(form, longPath, apiResponse.lng));
    }
  }

  render() {
    return null;
  }
}

const mapStateToProps = (state: State) => ({
  apiResponse: state.googleApiResponse.responseJSON
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    getGeocode: (address: string) => dispatch(getGeocodeDetails(address))
  };
};

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(CoordinatesValueUpdater as React.ComponentClass<Props>);
