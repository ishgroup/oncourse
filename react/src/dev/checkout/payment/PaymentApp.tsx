import React from "react";
import ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {CreateStore, RestoreState} from "../../../js/CreateStore";
import {MessagesRedux, ProgressRedux} from "../../../js/enrol/containers/Functions";

import 'react-select/dist/react-select.css';
import "../../../scss/_ReactSelect.scss";
import "../../../scss/index.scss";

import Payment from "../../../js/enrol/containers/payment/Payment";
import {addContact} from "../../../js/enrol/containers/contact-add/actions/Actions";
import {MockConfig} from "../../mocks/mocks/MockConfig";



const store = CreateStore();
RestoreState(store, () => render());

const config: MockConfig = MockConfig.CONFIG;

store.dispatch(addContact(config.db.getContactByIndex(0)));
store.dispatch(addContact(config.db.getContactByIndex(0)));


const render = () => ReactDOM.render(
	<Provider store={store}>
		<div id="checkout" className="col-xs-24 payments">
			<ProgressRedux/>
			<MessagesRedux/>
			<Payment/>
		</div>
	</Provider>,
	document.getElementById('root')
);