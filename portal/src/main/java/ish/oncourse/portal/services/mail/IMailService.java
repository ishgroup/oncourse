package ish.oncourse.portal.services.mail;

public interface IMailService {
	boolean sendEmail(EmailBuilder email, boolean asynchronous);
}
