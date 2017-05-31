import * as React from "react";
import {Contact} from "../../../../model/web/Contact";
import {Voucher} from "../../../../model/checkout/Voucher";
import {ProductClass} from "../../../../model/web/ProductClass";
import ProductListComp from "./ProductListComp";


export interface Props {
	contact: Contact;
	voucher: Voucher;
	productClass: ProductClass;
	onChange?: () => void;
	onPriceValueChange?: () => void;
}

class VoucherComp extends React.Component<Props, any> {
	public render(): JSX.Element {
		const {voucher, productClass, contact, onChange, onPriceValueChange} = this.props;

		return (
			<div>
				<ProductListComp type={Voucher} contact={contact} productItem={voucher} productClass={productClass} onChange={onChange} onPriceValueChange={onPriceValueChange} />
			</div>
		);
	}
}

export default VoucherComp;
