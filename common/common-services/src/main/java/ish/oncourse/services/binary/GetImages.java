package ish.oncourse.services.binary;

import ish.oncourse.function.IGet;
import ish.oncourse.model.AttachmentType;
import ish.oncourse.model.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anarut on 3/16/17.
 */
public class GetImages {

	private IGet<List<Document>> getDocuments;

	public GetImages(IGet<List<Document>> getDocuments) {
		this.getDocuments = getDocuments;
	}

	public List<Document> get() {
		List<Document> images = new ArrayList<>();
		List<Document> documents = getDocuments.get();
		for (Document document : documents) {
			if (document.getCurrentVersion() != null && AttachmentType.JPEG.getMimeType().equals(document.getCurrentVersion().getMimeType())) {
				images.add(document);
			}
		}
		return images;
	}
}
