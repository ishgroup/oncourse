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
