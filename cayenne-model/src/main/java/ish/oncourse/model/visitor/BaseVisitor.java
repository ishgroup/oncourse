package ish.oncourse.model.visitor;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;

public class BaseVisitor<T> implements IVisitor<T> {

	public T visitWebNode(WebNode node) {
		throw new UnsupportedOperationException();
	}

	public T visitWebContent(WebContent block) {
		throw new UnsupportedOperationException();
	}

	public T visitWebNodeType(WebNodeType type) {
		throw new UnsupportedOperationException();
	}
}
