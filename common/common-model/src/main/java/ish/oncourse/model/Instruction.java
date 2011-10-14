package ish.oncourse.model;

import ish.oncourse.model.auto._Instruction;

public class Instruction extends _Instruction {
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
}
