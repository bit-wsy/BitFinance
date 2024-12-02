package com.bit.srb.oss.service;

import java.io.InputStream;

public interface FileService {

    String upload(InputStream inputStream, String module, String fileName);

    void removeFile(String url);
}
