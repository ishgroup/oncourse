import * as React from "react";

/**
 * MouseHover is how to check if mouse hover component or not.
 * Wraps passed component, and passed all props inside component and mouseInside property.
 */
export class MouseHover extends React.Component<MouseHoverProps, MouseHoverState> {
  constructor(props) {
    super(props);

    this.state = {
      isInside: false,
    };
  }

  onEnter = () => {
    this.setState({isInside: true});
  }

  onLeave = () => {
    this.setState({isInside: false});
  }

  /**
   * Since it possible that cursor will be already inside component,
   * we should check on move to be sure that we track this.
   */
  onMove = () => {
    this.setState({isInside: true});
  }

  render() {
    const {component, componentProps} = this.props;

    return (
      <div
        onMouseEnter={this.onEnter}
        onMouseLeave={this.onLeave}
        onMouseMove={this.onMove}
      >
        {React.createElement(
          component,
          {mouseInside: this.state.isInside, ...componentProps},
          null,
        )}
      </div>
    );
  }
}

export interface WrappedMouseHoverProps {
  mouseInside: boolean;
}

interface MouseHoverProps {
  component: any;
  componentProps?: any;
}

interface MouseHoverState {
  isInside: boolean;
}
