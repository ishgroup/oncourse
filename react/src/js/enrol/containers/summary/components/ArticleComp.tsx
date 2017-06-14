import * as React from "react";
import {Contact} from "../../../../model/web/Contact";
import {ProductClass} from "../../../../model/web/ProductClass";
import ProductListComp from "./ProductListComp";
import {Article} from "../../../../model/checkout/Article";
import {Product} from "../../../../model/web/Product";


export interface Props {
  contact: Contact;
  article: Article;
  product: Product;
  onChange?: (item, contact) => void;
}

class ArticleComp extends React.Component<Props, any> {
  public render(): JSX.Element {
    const {article, product, contact, onChange} = this.props;

    return (
      <div>
        <ProductListComp type={Article} contact={contact} productItem={article} product={product} onChange={onChange} />
      </div>
    );
  }
}

export default ArticleComp;
