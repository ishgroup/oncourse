import * as React from "react";
import CartClassItem from "./CartClassItem";
import CartProductItem from "./CartProductItem";
import CartCourseItem from "./CartCourseItem";
import classnames from "classnames";
import {plural} from "../../../common/utils/HtmlUtils";
import {
  CourseClassCart, CourseClassCartState, ProductCart, ProductCartState, WaitingCourseCart, WaitingCourseClassState,
} from "../../../services/IshState";

class Cart extends React.Component<Props, State> {

  constructor(props) {
    super(props);

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
    const {removeClass, removeProduct, classes, products, waitingCourses, checkoutPath, removeWaitingCourse} = this.props;
    const countClasses = classes.result.length;
    const count = countClasses + products.result.length + waitingCourses.result.length;

    const checkoutLink = checkoutPath + "?sourcePath=" + window.location.href;

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
              {waitingCourses.result.map(courseId => {
                return <CartCourseItem key={courseId} item={waitingCourses.entities[courseId]} remove={removeWaitingCourse}/>;
              })}
              <li className="shortListOrderEnrol">
                <a className="shortlistLinkEnrol" href={checkoutLink}>{countClasses ? "Enrol" : "Purchase"}</a>
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
                <a href={checkoutLink}>{countClasses ? "Enrol" : "Purchase"}</a>
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
  readonly waitingCourses?: WaitingCourseClassState;
  readonly removeClass?: (courseClass: CourseClassCart) => void;
  readonly removeProduct?: (product: ProductCart) => void;
  readonly removeWaitingCourse?: (wCourse: WaitingCourseCart) => void;
}

interface State {
  readonly showShortList: boolean;
}

export default Cart;
