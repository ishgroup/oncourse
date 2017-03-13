package ish.oncourse.model;

import ish.oncourse.model.auto._InstructionParameter;
import ish.oncourse.utils.QueueableObjectUtils;

public class InstructionParameter extends _InstructionParameter {
	private static final long serialVersionUID = 9033334378745667507L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
}
