### Credit card payments

Rignt now we support only windcave CC payment processing : [https://www.windcave.com/](https://www.windcave.com/)  

See the payment service interface:
```
ish.oncourse.willow.checkout.windcave.IPaymentService
```

Single entry point (checkout-api ony):
```
ish.oncourse.willow.checkout.payment.v2.ProcessPaymentModel.performGatewayOperation()
```

We support two payment moodes:
1. Auth + Complete transactions `payment.gateway.purchase-without-auth = 0`
2. Purchase transaction only  `payment.gateway.purchase-without-auth = 1`

