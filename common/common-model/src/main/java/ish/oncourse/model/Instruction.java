package ish.oncourse.model;

import ish.oncourse.model.auto._Instruction;
import ish.oncourse.utils.QueueableObjectUtils;

public class Instruction extends _Instruction {
	private static final long serialVersionUID = -5727769742150943774L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
}
