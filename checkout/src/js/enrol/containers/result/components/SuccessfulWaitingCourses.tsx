import * as React from "react";

interface Props {
  successLink?: string;
}

export class SuccessfulWaitingCourses extends React.Component<Props, any> {
  render() {
    const {successLink} = this.props;

    return (
      <div>
        <h2>Thank you for your request.</h2>
        <p>The student was successfully added to waiting list for course</p>

        <p><strong>Please press continue to view further important information</strong></p>
        <p><a href={successLink}>Continue</a></p>

      </div>
    );
  }
}
