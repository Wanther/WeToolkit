package cn.wanther.toolkit.dao;

import cn.wanther.toolkit.exception.StorageException;

public interface LocalDataDao {
    int CATE_SCOPE_APP = 0;
    int CATE_SCOPE_USER = 1;

    void save(int category, String key, String content) throws StorageException;
    void save(String key, String content) throws StorageException;

    String get(int category, String key) throws StorageException;
    String get(String key) throws StorageException;

    void delete(int category) throws StorageException;
    void delete(int category, String key) throws StorageException;
}
