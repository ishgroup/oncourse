package ish.oncourse.services.datalayer;

import static ish.oncourse.services.datalayer.DataLayerFactory.*;

public abstract class AbstractProductConverter<V> {
	private ProductFactory productFactory;
	private V value;
	private Product product = new Product();

	protected abstract ProductId getProductId();

	protected abstract ProductCategory getCategory();

	protected abstract Price getPrice();

	public void convert() {
		product.id = getProductId();
		product.category = getCategory();
		product.quantity = 1;
		product.price = getPrice();
	}

	public Product getProduct() {
		return product;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public ProductFactory getProductFactory() {
		return productFactory;
	}

	public void setProductFactory(ProductFactory productFactory) {
		this.productFactory = productFactory;
	}
}
