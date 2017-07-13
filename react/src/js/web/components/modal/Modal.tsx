import * as React from "react";
import Rodal from "rodal";
import "rodal/lib/rodal.css";

export class Modal extends React.Component<ModalComponentProps, ModalComponentState> {
  constructor(props) {
    super(props);

    this.state = {
      visible: false,
      animation: "zoom"
    };
  }

  show(animation) {
    this.setState({
      animation,
      visible: true
    });
  }

  hide() {
    this.setState({visible: false});
  }

  render() {

    let types = ["zoom", "fade", "flip", "door", "rotate", "slideUp", "slideDown", "slideLeft", "slideRight"];
    let buttons = types.map((value, index) => {
      return (
        <button key={index} className="btn scale" onClick={this.show.bind(this, value)}>
          {value}
        </button>
      );
    });

    const content = `<p><b>Nanana</b></p><div>Test</div>`;

    return (
      <div className="wrap">
        <div className="container" style={{paddingTop: (window.innerHeight - 440) / 2}}>
          <div className="btn-area">{buttons}</div>
        </div>
        <Rodal visible={this.state.visible}
               onClose={this.hide.bind(this)}
               animation={this.state.animation}
        >
          <div dangerouslySetInnerHTML={{__html: content}}></div>
          <div className="header">Rodal</div>
          <div className="body">A React modal with animations.</div>
          <button className="rodal-confirm-btn" onClick={this.hide.bind(this)}>ok</button>
          <button className="rodal-cancel-btn" onClick={this.hide.bind(this)}>close</button>
        </Rodal>
      </div>
    );
  }
}

interface ModalComponentProps {

}

interface ModalComponentState {
  visible: boolean;
  animation: string;
}
