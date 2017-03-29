
export class Product {

  /**
   * Internal Unique identifier of product
   */
  id?: string;

  /**
   * Code of product
   */
  code?: string;

  /**
   * Name of product
   */
  name?: string;

  /**
   * Description of product
   */
  description?: string;

  /**
   * Is payment gateway enabled
   */
  isPaymentGatewayEnabled?: boolean;

  /**
   * Is on sale and is Web visible
   */
  canBuy?: boolean;
}

