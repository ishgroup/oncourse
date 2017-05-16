import * as React from "react";
import $ from "jquery";
import {findDOMNode} from "react-dom";
import {stopPropagation} from "../../../common/utils/HtmlUtils";
import {WindowService} from "../../../services/WindowService";
import {ModalService} from "../../../services/ModalService";


export class ConfirmOrderDialog extends React.Component<Props, {}> {
  private modalService: ModalService;
  private $item: JQuery;

  constructor(props) {
    super();
    this.$item = $(`.classItem[data-classid=${props.id}]`);
    this.modalService = WindowService.get("modal");
  }

  componentWillMount = () => {
    $(document).on("click", this.props.close);
  };

  componentDidMount = () => {
    $(findDOMNode(this))
      .show()
      .offset({top: this.$item.offset().top} as JQueryCoordinates)
      .css("right", "150px")
      .fadeIn("fast");
  };

  componentWillUnmount = () => {
    $(document).off("click", this.props.close);
  };

  render() {
    const {close, isAlreadyAdded, name, checkoutPath} = this.props;

    // TODO: connect via redux
    const date = this.$item.find(".class-item-info-l > .date a:first").text();

    let commonText, classDescription = [];

    if (isAlreadyAdded) {
      commonText = "You've already added this class to your shortlist. Do you want to proceed to checkout?";
    } else {
      commonText = "Thanks for adding:";
      classDescription = [
        <p key="className" className="className">{name}</p>,
        <p key="classDate" className="classDate">{date}</p>
      ];
    }

    return (
      <div className="confirmOrderDialog dialogContainer" onClick={stopPropagation}>
        <div className="confirm-message">
          <strong className="confirm-txt">{commonText}</strong>
          {classDescription}
        </div>
        <p className="confirm-proseed">
          <a href={checkoutPath} className="button">Proceed to Checkout</a>
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
  checkoutPath: string
  isAlreadyAdded: boolean
  id: string
  name: string
  date?: string
  close: () => void
}
