/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.export;

import org.apache.cayenne.exp.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds parameter from the client that are need to do a XSLT transformation on the server
 *
 */
public class ExportParameter implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * expression for fetching the database records on the server that are listed in the transformed xml file
	 */
	private Expression expression = null;

	/**
	 * id's of selected objects
	 */
	private List<Long> ids = new ArrayList<>();

	/**
	 * class name of objects from the list view
	 */
	private String entity = null;

	/**
	 * id of the used XSL file
	 */
	private String xslKeyCode = null;

	public Expression getExpression() {
		return this.expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public String getEntity() {
		return this.entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public List<Long> getIds() {
		return this.ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public String getXslKeyCode() {
		return this.xslKeyCode;
	}

	public void setXslKeyCode(String xslKeyCode) {
		this.xslKeyCode = xslKeyCode;
	}

}
