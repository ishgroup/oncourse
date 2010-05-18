package ish.oncourse.services.format;

import java.text.Format;


public interface IFormatService {

	Format formatter(FormatName formatName);
	
	String format(Object object, FormatName formatName);
}
