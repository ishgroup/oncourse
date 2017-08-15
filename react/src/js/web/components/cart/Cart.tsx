import * as React from "react";
import CartClassItem from "./CartClassItem";
import CartProductItem from "./CartProductItem";
import classnames from "classnames";
import {plural} from "../../../common/utils/HtmlUtils";
import {CourseClassCart, CourseClassCartState, ProductCart, ProductCartState} from "../../../services/IshState";

class Cart extends React.Component<Props, State> {

  constructor() {
    super();

    this.state = {
      showShortList: false,
    };
  }

  toggleShortList = () => {
    this.setState({
      showShortList: !this.state.showShortList,
    });
  }

  componentWillReceiveProps(nextProps) {
    if (!nextProps.classes.length) {
      this.setState({
        showShortList: false,
      });
    }
  }

  render() {
    const {removeClass, removeProduct, classes, products, checkoutPath} = this.props;
    const countClasses = classes.result.length;
    const count = countClasses + products.result.length;

    return (
      <div className="short-list" id="shortlist">
        <div className="shortlistInfo" id="info">
          <span>{count}</span>
          <p>{plural(count, ["item", "items"])}</p>
        </div>
        {!!count && [<h3 key="title" className="title-block">
          <span>My Courses Order</span>
        </h3>,
          <div key="body" className="shortlistChoices dialogContainer">
            <ul className="shortListOrder shortlistChoices" style={{
              display: this.state.showShortList ? "block" : "none",
            }}>
              {classes.result.map(courseClassId => {
                return <CartClassItem key={courseClassId} item={classes.entities[courseClassId]} remove={removeClass}/>;
              })}
              {products.result.map(productId => {
                return <CartProductItem key={productId} item={products.entities[productId]} remove={removeProduct}/>;
              })}
              <li className="shortListOrderEnrol">
                <a className="shortlistLinkEnrol" href={checkoutPath}>{countClasses ? "Enrol" : "Purchase"}</a>
              </li>
            </ul>
            <div className="closeButton" onClick={this.toggleShortList}>X</div>
          </div>,
          <div key="footer" className="shortlistAction">
            <ul className="shortlistControls">
              <li className={classnames("active", {
                shortlistActionHide: this.state.showShortList,
                shortlistActionShow: !this.state.showShortList,
              })} onClick={this.toggleShortList}>
                <a>{(this.state.showShortList ? "Hide" : "Show") + " Shortlist"}</a>
              </li>
              <li className="shortlistActionEnrol">
                <a href={checkoutPath}>{countClasses ? "Enrol" : "Purchase"}</a>
              </li>
            </ul>
          </div>,
        ]}
      </div>
    );
  }
}

export interface Props {
  readonly checkoutPath?: string;
  readonly classes?: CourseClassCartState;
  readonly products?: ProductCartState;
  readonly removeClass?: (courseClass: CourseClassCart) => void;
  readonly removeProduct?: (product: ProductCart) => void;
}

interface State {
  readonly showShortList: boolean;
}

export default Cart;
