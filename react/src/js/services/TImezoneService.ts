import moment from "moment";

export class TimezoneService {
  static getTimezone() {
    const offset = moment().utcOffset() / 60;

    if (offset > 0) {
      return `+${offset}`;
    } else {
      return `${offset}`;
    }
  }
}
