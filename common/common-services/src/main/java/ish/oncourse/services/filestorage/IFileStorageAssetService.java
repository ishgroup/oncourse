package ish.oncourse.services.filestorage;

import ish.oncourse.model.DocumentVersion;

@Deprecated
public interface IFileStorageAssetService {
    public void put(byte[] data, DocumentVersion documentVersion);
    public byte[] get(DocumentVersion documentVersion);
    public void delete(DocumentVersion documentVersion);
    public boolean contains(DocumentVersion documentVersion);
}
