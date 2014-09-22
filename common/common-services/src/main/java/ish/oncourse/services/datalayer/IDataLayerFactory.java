package ish.oncourse.services.datalayer;

import java.util.List;

import static ish.oncourse.services.datalayer.DataLayerFactory.Cart;

public interface IDataLayerFactory {
	public Cart build(List values);
}
