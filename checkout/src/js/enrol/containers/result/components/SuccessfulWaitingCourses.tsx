import * as React from "react";

interface Props {
  successLink?: string;
  contacts?: any;
}

export class SuccessfulWaitingCourses extends React.Component<Props, any> {
  render() {
    const {successLink, contacts} = this.props;

    return (
      <div>
        <h2>Thank you for your request.</h2>

        {contacts && contacts.map((c, index) => (
          <div key={index}>
            The student <b>{c.name}</b> was successfully added to waiting list for course <br/>
            {c.courses.map((w, index) => (
              <p key={index}>{w.name}</p>
            ))}
          </div>
        ))}

        <p><a className="link-continue" href={successLink}>Close</a></p>
      </div>
    );
  }
}
