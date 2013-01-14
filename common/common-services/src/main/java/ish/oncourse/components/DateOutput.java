package ish.oncourse.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The component could be used to convert date to string and show it on ther page.
 * Example:
 * <span t:type="ui/DateOutput" pattern="literal:yyyy/MM/dd"/> - will show something like this
 * 2012/01/30
 * document about <source>pattern</source> can be found by the link
 * http://docs.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html or in
 * java documentation java.text.SimpleDateFormat your JVM.
 */
public class DateOutput {

	@Parameter
	private Date date;

	@Parameter
	private DateFormat dateFormat;

	@Parameter
	private String pattern;


	@SetupRender
	public void setupRender()
	{
		if (date == null)
			date = new Date();
		if (dateFormat == null)
		{
			if (pattern != null)
				dateFormat = new SimpleDateFormat(pattern);
			else
				dateFormat = new SimpleDateFormat();
		}
	}

	public String getValue()
	{
		return dateFormat.format(date);
	}
}
