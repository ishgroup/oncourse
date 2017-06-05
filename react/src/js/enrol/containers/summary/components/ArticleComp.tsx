import * as React from "react";
import {Contact} from "../../../../model/web/Contact";
import {ProductClass} from "../../../../model/web/ProductClass";
import ProductListComp from "./ProductListComp";
import {Article} from "../../../../model/checkout/Article";


export interface Props {
  contact: Contact;
  article: Article;
  productClass: ProductClass;
  onChange?: (item, contact) => void;
  onPriceValueChange?: () => void;
}

class ArticleComp extends React.Component<Props, any> {
  public render(): JSX.Element {
    const {article, productClass, contact, onChange, onPriceValueChange} = this.props;

    return (
      <div>
        <ProductListComp type={Article} contact={contact} productItem={article} productClass={productClass} onChange={onChange} onPriceValueChange={onPriceValueChange} />
      </div>
    );
  }
}

export default ArticleComp;
