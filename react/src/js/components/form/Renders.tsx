import * as React from "react";


export class SuburbOption extends React.Component<any, any> {

  handleMouseDown = (event) => {
    event.preventDefault();
    event.stopPropagation();
    this.props.onSelect(this.props.option, event);
  };

  handleMouseEnter = (event) => {
    this.props.onFocus(this.props.option, event);
  };

  handleMouseMove = (event) => {
    if (this.props.isFocused) return;
    this.props.onFocus(this.props.option, event);
  };

  render() {
    const {children, className, option} = this.props;
    return (<div className={className}
                 onMouseDown={this.handleMouseDown}
                 onMouseEnter={this.handleMouseEnter}
                 onMouseMove={this.handleMouseMove}>
      {option.suburb}
      {children.suburb}
    </div>)
  }
}

export class SuburbValue extends React.Component<any, any> {
  render() {
    const {value, children} = this.props;
    return (<div className="Select-value">
				<span className="Select-value-label">
          {value.value.suburb}
				</span>
    </div>);
  }
}