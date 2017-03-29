import {EnvironmentConstants} from "./EnvironmentConstants";

export class DynamicConfig {
  static getOrigin() {
    if (process.env.NODE_ENV === EnvironmentConstants.development) {
      return "reactjs.oncourse.cc";
    }

    return location.host;
  }
}
