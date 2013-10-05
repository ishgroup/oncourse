package ish.oncourse.services.datalayer;

import java.util.List;

public interface IDataLayerFactory {
	public DataLayerFactory.Cart build(List values);
}
