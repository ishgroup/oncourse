import * as React from "react";

class DynamicText extends React.Component<any, any> {
  constructor(props) {
    super(props);

    this.state = {
      timer: null
    };
  }

  render() {
    return <span> {(this.props.value ? this.props.value : this.props.defaultValue)} {this.props.text} </span>;
  }

  componentDidMount() {
    this.tick();

    const timer = setInterval(this.tick, 30000);
    this.setState({ timer });
  }

  componentWillUnmount() {
    clearInterval(this.state.timer);
  }

  tick = () => {
    this.props.function();
  };
}

export default DynamicText;
