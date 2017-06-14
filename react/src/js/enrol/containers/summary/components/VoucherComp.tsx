import * as React from "react";
import {Contact} from "../../../../model/web/Contact";
import {Voucher} from "../../../../model/checkout/Voucher";
import {ProductClass} from "../../../../model/web/ProductClass";
import ProductListComp from "./ProductListComp";
import {Product} from "../../../../model/web/Product";


export interface Props {
	contact: Contact;
	voucher: Voucher;
	product: Product;
	onChange?: () => void;
	onPriceValueChange?: (item: any) => void;
}

class VoucherComp extends React.Component<Props, any> {
	public render(): JSX.Element {
		const {voucher, product, contact, onChange, onPriceValueChange} = this.props;

		return (
			<div>
				<ProductListComp type={Voucher} contact={contact} productItem={voucher} product={product} onChange={onChange} onPriceValueChange={(item) => onPriceValueChange(item)} />
			</div>
		);
	}
}

export default VoucherComp;
