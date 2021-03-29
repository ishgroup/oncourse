import * as React from "react";
import {findDOMNode} from "react-dom";
import {stopPropagation} from "../../../common/utils/HtmlUtils";
import {WindowService} from "../../../services/WindowService";
import {ModalService} from "../../../services/ModalService";


export class ConfirmOrderDialog extends React.Component<Props, {}> {
  private modalService: ModalService;
  private $item;

  constructor(props) {
    super(props);
    this.$item = document.querySelector(`.classItem[data-classid='${props.id}']`);
    this.modalService = WindowService.get("modal");
  }

  componentDidMount() {
    const el: any = findDOMNode(this);
    el.style.display = 'block';
    el.style.right = '150px';
    el.style.top = '-11px';
    setTimeout(() => {
      document.addEventListener("click", this.props.close);
    },         500);
  }

  componentWillUnmount() {
    document.removeEventListener("click", this.props.close);
  }

  render() {
    const {close, isAlreadyAdded, name, checkoutPath} = this.props;

    // TODO: connect via redux
    const dateEl = this.$item.querySelector('.class-item-info-l .date a');
    const date = dateEl ? dateEl.innerText : '';

    let commonText: string = null;
    let classDescription: any[] = null;

    if (isAlreadyAdded) {
      commonText = "You've already added this class to your shortlist. Do you want to proceed to checkout?";
    } else {
      commonText = "Thanks for adding:";
      classDescription = [
        <p key="className" className="className">{name}</p>,
        <p key="classDate" className="classDate">{date}</p>,
      ];
    }

    const checkoutLink = checkoutPath + "?sourcePath=" + window.location.href;

    return (
      <div className="confirmOrderDialog dialogContainer" onClick={stopPropagation}>
        <div className="confirm-message">
          <strong className="confirm-txt">{commonText}</strong>
          {classDescription}
        </div>
        <p className="confirm-proseed">
          <a href={checkoutLink} className="button">Proceed to Checkout</a>
        </p>
        <p className="confirm-close-wrapper">
          <a className="button closeButton" onClick={close}>Continue browsing</a>
        </p>
        <div className="closeButton" onClick={close}>X</div>
      </div>
    );
  }
}

interface Props {
  checkoutPath: string;
  isAlreadyAdded: boolean;
  id: string;
  name: string;
  date?: string;
  close: () => void;
}
