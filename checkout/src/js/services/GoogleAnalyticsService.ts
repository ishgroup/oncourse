interface PageEvent {
  title: string
}

interface Event {
  eventCategory: string,
  eventAction: string,
  eventLabel?: string,
  eventValue?: number,
}


export const initGAEvent = data => {
  console.log(data);

  if (window['ga'] && data) {
    if (data.type === 'page') {
      sendGAPageEvent(data)
    }

    if (data.type === 'event') {
      sendGAEvent(data)
    }
  }
};

const sendGAEvent = (data: Event) => {
  window['ga']('send', {
    hitType: 'event',
    eventCategory: data.eventCategory || '',
    eventAction: data.eventAction || '',
    eventLabel: data.eventLabel || '',
    eventValue: data.eventValue || 0,
  });
};

const sendGAPageEvent = (data: PageEvent) => {
  window['ga']('send', {
    hitType: 'pageview',
    page: data.title || '',
  });
};

export class GABuilder {
  static addItemToCart = (type, id) => {
    return {
      hitType: 'event',
      eventCategory: 'cart',
      eventAction: 'add',
      eventLabel: type,
      eventValue: id,
    }
  }

  static removeItemFromCart = (type, id) => {
    return {
      hitType: 'event',
      eventCategory: 'cart',
      eventAction: 'remove',
      eventLabel: type,
      eventValue: id,
    }
  }
}
