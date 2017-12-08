package ish.oncourse.services.filestorage;

import ish.oncourse.model.DocumentVersion;

@Deprecated
public interface IFileStorageAssetService {
    void put(byte[] data, DocumentVersion documentVersion);
    byte[] get(DocumentVersion documentVersion);
    void delete(DocumentVersion documentVersion);
    boolean contains(DocumentVersion documentVersion);
}
