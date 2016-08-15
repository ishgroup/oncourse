/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.certificate;

import ish.oncourse.portal.certificate.Model;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * User: akoiro
 * Date: 11/08/2016
 */
public class Title {
	@Parameter(required = true)
	@Property
	private Model model;

	@Inject
	private Block statement;

	@Inject
	private Block skillset;

	@Inject
	private Block qualification;

	@Inject
	private Block revoked;

	@Property
	private Block current;

	@SetupRender
	public void setupRender() {
		switch (GetBlockId.valueOf(model).getBlockId()) {
			case statement:
				current = statement;
				break;
			case skillset:
				current = skillset;
				break;
			case qualification:
				current = qualification;
				break;
			case revoked:
				current = revoked;
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	enum BlockId {
		statement,
		skillset,
		qualification,
		revoked
	}

	static class GetBlockId {
		private Model model;

		BlockId getBlockId() {
			if (model.getRevoked() != null) {
				return BlockId.revoked;
			} else if (model.getQualification() != null && model.getQualification().isQualification()) {
				switch (model.getQualification().getType()) {
					case QUALIFICATION_TYPE:
					case COURSE_TYPE:
					case HIGHER_TYPE:
						return BlockId.qualification;
					case SKILLSET_TYPE:
					case SKILLSET_LOCAL_TYPE:
						return BlockId.skillset;
					default:
						throw new IllegalArgumentException();
				}
			} else {
				return BlockId.statement;
			}
		}

		static GetBlockId valueOf(Model model) {
			GetBlockId getBlockId = new GetBlockId();
			getBlockId.model = model;
			return getBlockId;
		}
	}
}


