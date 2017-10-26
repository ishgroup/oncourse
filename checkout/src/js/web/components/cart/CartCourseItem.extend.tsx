import * as React from "react";
import classnames from "classnames";

export default {
  render() {
    const {course, pending} = this.props;

    const url:string = `/course/${course.code}`;
    const deleteClassName = classnames("deleteItem", {loading: pending});
    return (
      <li>
        <a href={url}>Waiting Course: {course.name}</a>
        <span className={deleteClassName} title="Remove item">
            <a onClick={this.methods.remove}>X</a>
        </span>
      </li>
    );
  },
};

