package ish.oncourse.services.filestorage;

import ish.oncourse.model.BinaryInfo;

public interface IFileStorageAssetService {
    public void put(byte[] data, BinaryInfo binaryInfo);
    public byte[] get(BinaryInfo binaryInfo);
    public void delete(BinaryInfo binaryInfo);
    public boolean contains(BinaryInfo binaryInfo);
}
