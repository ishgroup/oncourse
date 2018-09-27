import * as React from "react";
import classnames from "classnames";
import {formatDate} from "../../../common/utils/FormatUtils";
import {Formats} from "../../../constants/Formats";
import {TimezoneService} from "../../../services/TImezoneService";

export default {
  render() {
    const {courseClass, pending} = this.props;

    const url:string = `/class/${courseClass.course.code}-${courseClass.code}`;
    const deleteClassName = classnames("deleteItem", {loading: pending});
    return (
      <li>
        <a href={url}>{courseClass.course.name}</a>
        <span className={deleteClassName} title="Remove item">
            <a onClick={this.methods.remove}>X</a>
        </span>
        {courseClass.start && courseClass.end &&
        <div className="shortListOrderClasses">
          <abbr className="dtstart" title="">
            {formatDate(courseClass.start, Formats.DATE_TIME_FORMAT, courseClass.timezone)}
          </abbr>
          {" - "}
          <abbr className="dtend" title="">
            {formatDate(courseClass.end, Formats.DATE_TIME_FORMAT, courseClass.timezone)}
          </abbr>
          {` (UTC ${TimezoneService.getTimezone()}) `}
        </div>}
      </li>
    );
  },
};

