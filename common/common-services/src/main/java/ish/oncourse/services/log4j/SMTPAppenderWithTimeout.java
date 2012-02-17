package ish.oncourse.services.log4j;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.apache.log4j.net.SMTPAppender;

public class SMTPAppenderWithTimeout extends SMTPAppender {
	
	private int connectionTimeout;
	private int timeout;
	private int instanceIndex;
	private int versionIndex;

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public void setInstanceIndex(int instanceIndex) {
		this.instanceIndex = instanceIndex;
	}
	
	public int getInstanceIndex() {
		return instanceIndex;
	}
	
	public void setVersionIndex(int versionIndex) {
		this.versionIndex = versionIndex;
	}
	
	public int getVersionIndex() {
		return versionIndex;
	}
	
	@Override
	public void setSubject(String subject) {
		try {
			String[] pathComponents = getClass().getResource("/").toString().split("/");
			
			if (instanceIndex > 0 && versionIndex > 0 && 
					instanceIndex < pathComponents.length && versionIndex < pathComponents.length) {
				super.setSubject(subject + " Instance: " + pathComponents[instanceIndex] + 
						". Version: " + pathComponents[versionIndex]);
			}
			else {
				super.setSubject(subject);
			}
			
		} catch (Throwable e) {
			super.setSubject(subject);
		}
	}

	@Override
	protected Session createSession() {
		Properties props = null;
		
		try {
			props = new Properties(System.getProperties());
		} catch (SecurityException ex) {
			props = new Properties();
		}
		
		if (connectionTimeout > 0) {
			props.setProperty("mail.smtp.connectiontimeout", String.valueOf(connectionTimeout));
		}
		
		if (timeout > 0) {
			props.setProperty("mail.smtp.timeout", String.valueOf(timeout));
		}

		String prefix = "mail.smtp";
		if (getSMTPProtocol() != null) {
			props.put("mail.transport.protocol", getSMTPProtocol());
			prefix = "mail." + getSMTPProtocol();
		}
		
		if (getSMTPHost() != null) {
			props.put(prefix + ".host", getSMTPHost());
		}
		
		if (getSMTPPort() > 0) {
			props.put(prefix + ".port", String.valueOf(getSMTPPort()));
		}

		Authenticator auth = null;
		if (getSMTPPassword() != null && getSMTPUsername() != null) {
			props.put(prefix + ".auth", "true");
			auth = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(getSMTPUsername(), getSMTPPassword());
				}
			};
		}
		
		Session session = Session.getInstance(props, auth);
		if (getSMTPProtocol() != null) {
			session.setProtocolForAddress("rfc822", getSMTPProtocol());
		}
		
		if (getSMTPDebug()) {
			session.setDebug(getSMTPDebug());
		}
		
		return session;
	}
}
