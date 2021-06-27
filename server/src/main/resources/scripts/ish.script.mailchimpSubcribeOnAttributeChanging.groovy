mailchimp {
    name nameOfIntegration
    action record.allowEmail ? 'subscribe' : 'unsubscribe'
    contact record
}