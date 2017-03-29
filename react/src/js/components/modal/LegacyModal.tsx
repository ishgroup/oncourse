import * as React from 'react';
import Rodal from 'rodal';
import "rodal/lib/rodal.css";
import {ModalService, ModalOptions} from "../../services/ModalService";
import {WindowService} from "../../services/WindowService";

/**
 * Modal used from old javascript application.
 */
export class LegacyModal extends React.Component<ModalComponentProps, ModalComponentState> {
  constructor(props) {
    super(props);

    const open = (options: ModalOptions) => {
      const state = Object.assign({visible: true}, options);

      this.setState(state);

      return () => {
        this.hide();
      }
    };

    WindowService.set("modal", new ModalService(open));

    this.state = {
      content: '',
      visible: false,
      animation: 'zoom'
    }
  }

  hide = () => {
    this.setState({
      visible: false
    });

    if (this.state.onClose) {
      this.state.onClose();
    }
  };

  render() {
    return (
        <Rodal visible={this.state.visible}
               onClose={this.hide}
               width={this.state.width}
               height={this.state.height}
               showCloseButton={this.state.showCloseButton}
               duration={this.state.duration}
               animation={this.state.animation}
        >
          <div dangerouslySetInnerHTML={{__html: this.state.content}}></div>
        </Rodal>
    )
  }
}

interface ModalComponentProps {

}

interface ModalComponentState extends ModalOptions{
  visible: boolean;
}
