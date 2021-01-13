package ish.oncourse.model;

import ish.oncourse.model.auto._SystemUser;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.security.AuthenticationUtil;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;

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
			setPassword(AuthenticationUtil.generatePasswordHash(pass));
		} catch (Exception e) {
			setPassword(StringUtils.EMPTY);
		}
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
