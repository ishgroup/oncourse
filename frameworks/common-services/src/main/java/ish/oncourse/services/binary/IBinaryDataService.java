package ish.oncourse.services.binary;

import ish.oncourse.model.BinaryData;
import ish.oncourse.model.BinaryInfo;

public interface IBinaryDataService {

	BinaryInfo getBinaryInfo(String searchProperty, Object value);
	
	BinaryData getBinaryData(BinaryInfo binaryInfo);

}
