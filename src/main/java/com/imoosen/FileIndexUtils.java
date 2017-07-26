package com.imoosen;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by [mengsen] on 2017/7/26 0026.
 *
 * @Description: [一句话描述该类的功能]
 * @UpdateUser: [mengsen] on 2017/7/26 0026.
 */
public class FileIndexUtils {
    private static String indexPath ="E://lucene//index";

    public static Directory getDirectory()throws Exception{
        File file = new File(indexPath);
        Directory dir  = FSDirectory.open(file);
        return dir;
    }
}
