import {render}  from 'react-dom';
import {Provider} from 'react-redux';
import {createStore, combineReducers, applyMiddleware} from 'redux';
import {reducer} from 'redux-form';
import thunk from 'redux-thunk';

import enrolReducer from 'app/reducers/enrol';
import Enrol from 'app/containers/Enrol';
import 'css/modules/enrol.css';

let store = createStore(
    combineReducers({
        form: reducer,
        enrol: enrolReducer
    }), applyMiddleware(thunk)
);


render(
    <Provider store={store}>
        <Enrol/>
    </Provider>,
    document.getElementById('main')
);
