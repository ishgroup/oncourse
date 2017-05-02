import * as React from "react";
import {PromotionCart, PromotionCartState} from "../../../services/IshState";
import {KeyboardEvent} from "react";

export default class Promotions extends React.Component<PromotionsProps, PromotionsState> {

  constructor() {
    super();

    this.state = {
      value: "",
      error: null
    };
  }

  onChange = (e) => {
    this.setState({
      value: e.target.value
    });
  };

  add = (e) => {
    e.preventDefault();
    this.props.addPromotion(this.state.value);
  };

  addEnter = (e: KeyboardEvent<any>) => {
    if (e.key === "13") {
      this.add(e);
    }
  };

  remove = (e, promotion: PromotionCart) => {
    e.preventDefault();

    this.props.removePromotion(promotion);
  };

  render() {
    const {error, value} = this.state;
    const {promotions, removePromotion} = this.props;
    const count = promotions.result.length;

    return (
      <div>
        <p>
          Sometimes we are able to offer discounts on a selection of our classes.
          If you have a discount code, then please enter it into the box below
        </p>
        <form id="addDiscountForm">
          <label htmlFor="promo">Discount code:</label>
          <input id="promo" value={value} onKeyPress={this.addEnter} onChange={this.onChange}/>
          <button id="addDiscountButton" onClick={this.add} disabled={!value}>Add</button>
          {!!error && getError(error)}
        </form>

        {!!count && getDiscountOptions(promotions, removePromotion)}
        <Note/>
      </div>
    );
  }
}

function getError(error: string) {
  return (
    <div className="validation">{error}</div>
  );
}

function Note() {
  return (
    <p className="note">
      <strong className="alert">Please note:</strong>
      Our discounts are usually only available until a certain date, so you may not
      be able to use an old discount code.
    </p>
  );
}

function getDiscountOptions(promotions: PromotionCartState, removePromotion: (promotion: PromotionCart) => void) {
  return (
    <div>
      <p className="discount_options">You have entered the following codes.</p>
      <br/>
      {promotions.result.map((promotionId) => {
        const promotion = promotions.entities[promotionId];

        return (
          <div key={promotionId}>
            <h5 className="popup-name">{promotion.code}</h5>
            <div className="clear"/>
            <p>{promotion.name}</p> <a onClick={() => removePromotion(promotion)}>X</a>
            <div className="divideline"/>
          </div>
        );
      })}
      <p>The discounted prices will be now shown next to each course as you browse this site.</p>
    </div>
  );
}

export interface PromotionsProps {
  promotions: PromotionCartState;
  addPromotion: (code: string) => void;
  removePromotion: (promotion: PromotionCart) => void;
}

interface PromotionsState {
  value: string;
  error: any;
}
