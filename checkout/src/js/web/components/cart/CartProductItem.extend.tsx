import * as React from "react";
import {ProductCart} from "../../../services/IshState";

export default {
  render() {
    let product: ProductCart = this.props.product;

    return (
      <li>
        <a href={`/product/${product.code}`}>{product.name}</a>
        <span className={this.utils.classnames("deleteItem", {
          "loading": this.props.pending
        })} title="Remove item">
                    <a onClick={this.methods.remove}>X</a>
                </span>
        {/*
         ToDo Discussed with Artyom this part.
         Why we display product create and modified dates as date range?
         Weird stuff
         */}
        {product["created"] && product["modified"] &&
        <div className="shortListOrderClasses">
          <abbr className="dtstart" title="">{product["created"]}</abbr>
          <abbr className="dtend" title="">{product["modified"]}</abbr>
        </div>}
      </li>
    );
  }
};
