import * as React from "react";

interface Props {

}

export class Undefined extends React.Component<Props, any> {
  render() {
    return (
      <div>
        <p><strong>The banking system is experiencing a problem at the moment. We'll continue to try and process this
          payment and let you know if there is a problem.</strong></p>
      </div>
    );
  }
}
