import * as React from 'react';
import {PromotionCart, PromotionCartState} from "../../services/IshState";
import {KeyboardEvent} from "react";

export default class Promotions extends React.Component<PromotionsProps, PromotionsState> {

  constructor() {
    super();

    this.state = {
      value: '',
      error: null,
      pending: false
    };
  }

  onChange = (e) => {
    this.setState({
      value: e.target.value
    });
  };

  add = (e) => {
    e.preventDefault();
    this.setState({pending: true});
    this.props.addPromotion(this.state.value);
    this.setState({pending: false});
  };

  addEnter = (e: KeyboardEvent<any>) => {
    if (e.key === "13") {
      this.add(e);
    }
  };

  remove = (e, promotion: PromotionCart) => {
    e.preventDefault();

    this.setState({pending: true});
    this.props.removePromotion(promotion);
    this.setState({pending: false});
  };

  render() {
    const {error, value, pending} = this.state;
    const {promotions} = this.props;
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
          <button id="addDiscountButton" onClick={this.add} disabled={!value || pending}>Add</button>
          {!!error && <div className="validation">{error}</div>}
        </form>

        {!!count && <div>
          <p className="discount_options">You have entered the following codes.</p><br/>
          {promotions.result.map((promotionId) => {
            const promotion = promotions.entities[promotionId];

            return (
              <div key={promotionId}>
                <h5 className="popup-name">{promotion.code}</h5>
                <div className="clear"/>
                <p>{promotion.name}</p> <a onClick={(e) => this.remove(e, promotion)}>X</a>
                <div className="divideline"/>
              </div>
            );
          })}
          <p>The discounted prices will be now shown next to each course as you browse this site.</p>
        </div>}

        <p className="note">
          <strong className="alert">Please note:</strong>
          Our discounts are usually only available until a certain date, so you may not
          be able to use an old discount code.
        </p>
      </div>
    );
  }
}

export interface PromotionsProps {
  promotions: PromotionCartState;
  addPromotion: (code: string) => void;
  removePromotion: (promotion: PromotionCart) => void;
}

interface PromotionsState {
  value: string;
  error: any;
  pending: boolean;
}
