import * as React from "react";
import {Contact, Article, Product} from "../../../../model";
import classnames from "classnames";
import {ItemWrapper} from "./ItemWrapper";


export interface Props {
  contact: Contact;
  article: Article;
  product: Product;
  onChange?: (item, contact) => void;
}

class ArticleComp extends React.Component<Props, any> {

  constructor(props) {
    super(props)

    this.props.article.quantity = 1;
  }

  private updateQuantity = val => {
    const reg = (/^[0-9]+/);

    if (val > 0 && reg.test(val)) {
      this.props.article.quantity = Number(val);
    }
    return false;
  }

  private onChangeQuantity = val => {
    this.props.article.quantity = Number(val);
  }

  public render(): JSX.Element {
    const {article, product, contact, onChange} = this.props;
    const divClass = classnames("row", "enrolmentItem", {disabled: !article.selected});
    const warning = article.warnings && article.warnings.length ? this.props.article.warnings[0] : null;
    const error = article.warnings && article.errors.length ? this.props.article.errors[0] : null;
    const name = `article-${contact.id}-${article.productId}`;

    return (
      <div className={divClass}>
        <ItemWrapper title={product.name} name={name} error={error} warning={warning} selected={article.selected}
                     item={article} contact={contact}
                     onChange={onChange}>
          <div/>
        </ItemWrapper>
        {article.selected &&
        <div className="col-xs-8 col-md-7 alignright priceValue">
          <span className="col-xs-24 col-md-24 fee-full quantity text-right">Quantity:</span>
          <input
              type="text"
              className="text-left"
              name="quantityValue"
              value={article.quantity}
              // onChange={e => {this.updateQuantity(e.target.value);}}
              onBlur={e => {this.onChangeQuantity(e.currentTarget.value);}}
          />

          <div className="row">
            <span className="col-xs-24 col-md-24 fee-full fullPrice text-right">${article.price}</span>
          </div>
        </div>
        }
      </div>
    );
  }
}

export default ArticleComp;
