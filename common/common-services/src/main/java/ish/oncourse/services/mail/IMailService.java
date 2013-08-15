package ish.oncourse.services.mail;

public interface IMailService {
	public boolean sendMail(String from, String to, String subject, String body);
	public boolean sendEmail(EmailBuilder email, boolean asynchronous);
}
