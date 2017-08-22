import {Store, Unsubscribe} from "redux";
import {Logger, LogMessage, Level} from "./Logger";
import {IshState} from "./IshState";

type StoreListener = (state: IshState) => void;

class StoreSubscriber {
    constructor(public listener: StoreListener,
                public unsubscribe: Unsubscribe) {
    }
}

export class StoreListenerService {
    private subscribers: StoreSubscriber[] = [];

    constructor(private store: Store<IshState>) {
    }

    addListener(listener: StoreListener) {
        const unsubscribe = this.store.subscribe(() => listener(this.store.getState()));
        this.subscribers = [...this.subscribers, new StoreSubscriber(listener, unsubscribe)];
    }

    removeListener(listener: StoreListener) {

        const subscriber = this.subscribers.find(subscriber => subscriber.listener === listener);

        if (subscriber) {
            subscriber.unsubscribe();
        } else {
            Logger.log(new LogMessage(Level.ERROR, "Trying remove listener, that not registered"));
        }
    }
}
