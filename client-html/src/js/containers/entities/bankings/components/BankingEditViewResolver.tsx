import { openInternalLink } from "ish-ui";
import * as React from "react";
import { EditViewProps } from "../../../../model/common/ListView";
import { PaymentInType } from "../consts";
import BankingCreateView from "./BankingCreateView";
import BankingEditView from "./BankingEditView";

class BankingEditViewResolver extends React.PureComponent<EditViewProps, any> {
  openNestedView = item => {
    const url = `/${item.paymentTypeName === PaymentInType ? "paymentIn" : "paymentOut"}/` + item.paymentId;
    openInternalLink(url);
  };

  render() {
    const { isNew } = this.props;
    return isNew ? (
      <BankingCreateView {...this.props} openNestedView={this.openNestedView} />
    ) : (
      <BankingEditView {...this.props} openNestedView={this.openNestedView} />
    );
  }
}

export default BankingEditViewResolver;
