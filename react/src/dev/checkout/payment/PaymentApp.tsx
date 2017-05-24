import React from "react";
import ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {CreateStore, RestoreState} from "../../../js/CreateStore";
import {MessagesRedux, ProgressRedux} from "../../../js/enrol/containers/Functions";

import 'react-select/dist/react-select.css';
import "../../../scss/_ReactSelect.scss";
import "../../../scss/index.scss";

import {amount, contactPropses} from "./PaymentApp.data";
import Payment from "../../../js/enrol/containers/payment/Payment";

const store = CreateStore();
RestoreState(store, () => render());

const render = () => ReactDOM.render(
	<Provider store={store}>
		<div id="checkout" className="col-xs-24 payments">
			<ProgressRedux/>
			<MessagesRedux/>
			<Payment contacts={[
				contactPropses[0],
				contactPropses[1]
			]} amount={amount} />
		</div>
	</Provider>,
	document.getElementById('root')
);