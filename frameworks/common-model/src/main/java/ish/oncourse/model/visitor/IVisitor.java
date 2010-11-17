package ish.oncourse.model.visitor;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;

public interface IVisitor<T> {
	T visitWebNode(WebNode node);
	T visitWebContent(WebContent block);
	T visitWebNodeType(WebNodeType type);
}
