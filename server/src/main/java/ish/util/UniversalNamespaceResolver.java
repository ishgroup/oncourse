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

package ish.util;

import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;


/**
 * saxon does not handle the xml namespaces by default. this is a class which adds support for simple namespaces
 */
public class UniversalNamespaceResolver implements NamespaceContext {
	// the delegate
	private Document sourceDocument;

	/**
	 * This constructor stores the source document to search the namespaces in
	 * it.
	 *
	 * @param document source document
	 */
	public UniversalNamespaceResolver(Document document) {
		sourceDocument = document;
	}

	/**
	 * The lookup for the namespace uris is delegated to the stored document.
	 *
	 * @param prefix to search for
	 * @return uri
	 */
	public String getNamespaceURI(String prefix) {
		if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
			return sourceDocument.lookupNamespaceURI(null);
		} else {
			return sourceDocument.lookupNamespaceURI(prefix);
		}
	}

	/**
	 * This method is not needed in this context, but can be implemented in a
	 * similar way.
	 */
	public String getPrefix(String namespaceURI) {
		return sourceDocument.lookupPrefix(namespaceURI);
	}

	public Iterator getPrefixes(String namespaceURI) {
		return null;
	}
}
