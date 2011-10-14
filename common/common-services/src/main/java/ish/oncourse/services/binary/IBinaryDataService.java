package ish.oncourse.services.binary;

import ish.oncourse.model.BinaryInfo;

import java.util.List;

public interface IBinaryDataService {

	BinaryInfo getBinaryInfoByReferenceNumber(Object refNum);
	
	BinaryInfo getBinaryInfoById(Object id);
	
	BinaryInfo getBinaryInfo(String searchProperty, Object value);

	BinaryInfo getRandomImage();

	List<BinaryInfo> getAttachedFiles(Long entityIdNum, String entityIdentifier, boolean isWebVisible);

}
