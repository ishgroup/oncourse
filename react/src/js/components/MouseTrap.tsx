import * as React from "react";

export class MouseTrap extends React.Component<MouseTrapProps, MouseTrapState> {
  constructor() {
    super();

    this.state = {
      inside: false
    }
  }

  onEnter = () => {
    this.setState({inside: true})
  };

  onLeave = () => {
    this.setState({inside: false})
  };

  /**
   * Since it possible that cursor will be already inside component,
   * we should check on move to be sure that we track this.
   */
  onMove = () => {
    this.setState({inside: true})
  };

  render() {
    const {component, componentProps} = this.props;

    return (
      <div onMouseEnter={this.onEnter}
           onMouseLeave={this.onLeave}
           onMouseMove={this.onMove}
      >
        {React.createElement(
          component,
          {mouseInside: this.state.inside, ...componentProps},
          null
        )}
      </div>
    )
  }
}

export interface MouseTrapChildProps {
  mouseInside: boolean;
}

interface MouseTrapProps {
  component: any;
  componentProps?: any;
}

interface MouseTrapState {
  inside: boolean;
}
