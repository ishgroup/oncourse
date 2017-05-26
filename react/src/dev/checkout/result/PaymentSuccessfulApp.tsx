import React from "react";
import ReactDOM from "react-dom";
import {Provider} from "react-redux";
import {CreateStore, RestoreState} from "../../../js/CreateStore";
import {MessagesRedux, ProgressRedux} from "../../../js/enrol/containers/Functions";
import PaymentSuccessful from "../../../js/enrol/containers/result/PaymentSuccessful";

const store = CreateStore();
RestoreState(store, () => render());

const successfulRefId: string = "W1367014";

const render = () => ReactDOM.render(
	<Provider store={store}>
		<div id="checkout" className="col-xs-24 payments">
			<ProgressRedux />
			<MessagesRedux />
			<PaymentSuccessful refId={successfulRefId} />
		</div>
	</Provider>,
	document.getElementById('root')
);