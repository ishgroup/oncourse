import * as React from "react";

interface Props {
  children: any
  className: string
  option: any
  value: any
  onFocus: (option, event) => void
  onSelect: (option, event) => void
  isFocused: boolean

  renderOption: (option: any) => JSX.Element
}

export class RenderOption extends React.Component<Props, any> {
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
    const {children, className, renderOption} = this.props;
    return (<div className={className}
                 onMouseDown={this.handleMouseDown}
                 onMouseEnter={this.handleMouseEnter}
                 onMouseMove={this.handleMouseMove}>
      {renderOption(children)}
    </div>)
  }
}

export class RenderValue extends React.Component<Props, any> {
  render() {
    const {value, renderOption} = this.props;
    return (<div className="Select-value">
				<span className="Select-value-label">
          {renderOption(value.value)}
				</span>
    </div>);
  }
}


export class SuburbOption extends React.Component<any, any> {
  render() {
    return (<RenderOption {...this.props} renderOption={(option) => {
      return option.suburb
    }}/>)
  }
}

export class SuburbValue extends React.Component<any, any> {
  render() {
    return (<RenderValue {...this.props} renderOption={(option) => {
      return option.suburb
    }}/>);
  }
}
