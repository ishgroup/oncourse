import * as React from "react";
import {Contact, Article, Product} from "../../../../model";
import classnames from "classnames";
import {ItemWrapper} from "./ItemWrapper";


export interface Props {
  contact: Contact;
  article: Article;
  product: Product;
  onQuantityValueChange?: (val: number) => any;
  onChange?: (item, contact) => void;
}
export interface State {
  quantity: number;
}
class ArticleComp extends React.Component<Props, State> {

  constructor(props) {
    super(props);

    this.state = {
      quantity: props.article.quantity || 1,
    };
  }
  
  componentWillReceiveProps(props) {
    this.setState({
      quantity: props.article.quantity,
    });
  }

  private updateQuantity = val => {
    const reg = (/^[0-9]+/);

    if (val === '' || (val > 0 && reg.test(val))) {
      this.setState({
        quantity: Number(val),
      });
    }
    return false;
  }

  private handleQuantityBlur() {
    const {onQuantityValueChange} = this.props;
    onQuantityValueChange(this.state.quantity || 1);  
  }

  public render(): JSX.Element {
    const {article, product, contact, onChange} = this.props;
    const divClass = classnames("row", "enrolmentItem", {disabled: !article.selected});
    const warning = article.warnings && article.warnings.length ? this.props.article.warnings[0] : null;
    const error = article.warnings && article.errors.length ? this.props.article.errors[0] : null;
    const name = `article-${contact.id}-${article.productId}`;
    const quantity = this.state.quantity;

    return (
      <div className={divClass}>
        <ItemWrapper title={product.name} name={name} error={error} warning={warning} selected={article.selected}
                     item={article} contact={contact}
                     onChange={onChange}>
          <div/>
        </ItemWrapper>
        {article.selected &&
        <div className="col-xs-8 col-md-7 alignright priceValue">
					<div className="row">
            <span className="col-xs-5 col-md-5 fee-full quantity text-right">Quantity:</span>
						<span className="col-xs-5 col-md-5">
              <input
                  type="text"
                  className="text-right"
                  name="quantityValue"
                  value={quantity}
                  onChange={e => {this.updateQuantity(e.target.value);}}
                  onBlur={e => {this.handleQuantityBlur();}}
              />
            </span>
            
            <span className="col-xs-14 col-md-14 fee-full fullPrice text-right">${article.total.toFixed(2)}</span>
          </div>
        </div>
        }
      </div>
    );
  }
}

export default ArticleComp;
