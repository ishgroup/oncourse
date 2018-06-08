import {EnvironmentConstants} from "./EnvironmentConstants";

export class DynamicConfig {
  static getOrigin() {
    return location.host;
  }
}
