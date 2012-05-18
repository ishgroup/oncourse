package ish.oncourse.model;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;

import ish.oncourse.model.auto._SystemUser;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.util.SecurityUtil;

public class SystemUser extends _SystemUser implements Queueable {
	private static final long serialVersionUID = -9096815651354984411L;

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
	
	/**
	 * Set the password, first converting it to a hash before saving to the database.
	 */
	public void setClearPassword(String pass) {
		try {
			setPassword(SecurityUtil.hashPassword(pass));
		} catch (UnsupportedEncodingException e) {
			setPassword(StringUtils.EMPTY);
		}
	}

}
