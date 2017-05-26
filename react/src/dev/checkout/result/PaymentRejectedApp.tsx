import React from "react";
import ReactDOM from "react-dom";
import {Provider} from "react-redux";
import {CreateStore, RestoreState} from "../../../js/CreateStore";
import {MessagesRedux, ProgressRedux} from "../../../js/enrol/containers/Functions";
import PaymentRejected from "../../../js/enrol/containers/result/PaymentRejected";

const store = CreateStore();
RestoreState(store, () => render());

const render = () => ReactDOM.render(
	<Provider store={store}>
		<div id="checkout" className="col-xs-24 payments">
			<ProgressRedux />
			<MessagesRedux />
			<PaymentRejected/>
		</div>
	</Provider>,
	document.getElementById('root')
);