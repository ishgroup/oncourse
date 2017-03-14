package ish.oncourse.utils;

import ish.oncourse.model.Queueable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageFormat<E extends Queueable> {
	private static final Logger logger = LogManager.getLogger();
	private static final String MESSAGE_template = "%s: willowId: %d, angelId: %d, collegeId: %d, %s";

	private E entity;

	private String messageTemplate;
	private Object[] parameters;

	public String format() {
		try {
			String message = String.format(messageTemplate, parameters);
			return String.format(MESSAGE_template, entity.getClass().getSimpleName(), entity.getId(), entity.getAngelId(),
					(entity.getCollege() != null ? entity.getCollege().getId(): null), message);
		} catch (Exception e) {
			logger.error("entity: {}, messageTemplate: {}, parameters: {}: Unexpected exception!", entity, messageTemplate, parameters);
			return messageTemplate;
		}
	}

	public static <E extends Queueable> MessageFormat<E> valueOf(E entity, String messageTemplate, Object... parameter) {
		MessageFormat<E> messageFormat = new MessageFormat<>();
		messageFormat.entity = entity;
		messageFormat.messageTemplate = messageTemplate;
		messageFormat.parameters = parameter;
		return messageFormat;
	}
}
