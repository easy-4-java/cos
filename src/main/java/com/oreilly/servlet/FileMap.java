package com.oreilly.servlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * A map-like container for uploaded files that handles duplicate keys gracefully.
 *
 * <p>When multiple files are uploaded with the same form field name, the
 * standard behavior would keep only the last value. This class avoids
 * collisions by appending an incremental suffix ({@code _0}, {@code _1}, &hellip;)
 * to duplicate keys so that every uploaded file is retained.</p>
 *
 * @author L.cm
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see MultipartRequest
 */
class FileMap {
	
    private int index = 0;
    private LinkedHashMap<String, UploadedFile> map = new LinkedHashMap<String, UploadedFile>();

    /**
     * put
     * @param key 键
     * @param value 值
     * @return boolean
     */
    public UploadedFile put(String key, UploadedFile value) {
        if (map.containsKey(key)) {
            key = key + "_" + (index++);
        }
        return map.put(key, value);
    }

    /**
     * get List by key
     * @param key 键
     * @return List
     */
    public UploadedFile get(String key) {
        return map.get(key);
    }

    /**
     * keys 此处需要还原为真实的大小,一个key一个
     * @return key 集合
     */
    public Enumeration<String> keys() {
    	return Collections.enumeration(map.keySet());
    }
    
    /**
     * jfinal 3.2 以及后续版本使用该方法来取出上传文件的文件名
     * 避免构造 Enumeration 对象
     */
    public Set<String> getFileNameSet() {
    	return map.keySet();
    }
	
	/**
	 * 用于在发生异常后删除所有已上传文件
	 */
	public void deleteAllFiles() {
		for (UploadedFile uf : map.values()) {
			try {
				java.io.File file = uf.getFile();
				if (file != null) {
					file.delete();
				}
			}
			catch (Exception e) {
				// ignore
			}
		}
	}
}
