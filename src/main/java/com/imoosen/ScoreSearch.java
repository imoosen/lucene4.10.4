package com.imoosen;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.junit.Test;

/**
 * Created by [mengsen] on 2017/7/26 0026.
 *
 * @Description: [一句话描述该类的功能]
 * @UpdateUser: [mengsen] on 2017/7/26 0026.
 */
public class ScoreSearch {
    /**
     * 按照评分进行排序
     */
    public void searchByScore(String queryStr,Sort sort) {
        try {
            IndexSearcher search = new IndexSearcher(DirectoryReader.open(FileIndexUtils.getDirectory()));
            QueryParser qp = new QueryParser("content", new StandardAnalyzer());
            Query q = qp.parse(queryStr);
            TopDocs tds = null;
            if (sort != null) {
                tds = search.search(q, 200, sort);
            } else {
                tds = search.search(q, 200);
            }
            for (ScoreDoc sd : tds.scoreDocs) {
                Document d = search.doc(sd.doc);
                System.out.println(sd.doc + ":(" + sd.score + ")" +
                        "[" + d.get("filename") + "【" + d.get("path") + "】---" + d.get("score") + "--->" +
                        d.get("size") + "]");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void testSearchByScore(){
        ScoreSearch ss = new ScoreSearch();
        /**sort没有set sort时，默认按照关联性进行排序**/
        Sort sort = new Sort();
        //sort.setSort(new SortField("score", Type.INT,true));//true表示倒叙，默认和false表示正序
        sort.setSort(new SortField("size", SortField.Type.LONG),new SortField("score", SortField.Type.INT));
        ss.searchByScore("java",sort);
    }

    @Test
    public void testSelfScore(){
        MySelfScore mss = new MySelfScore();
        /**
         * 如果使用标准分词器，那么java类中得java.io.IOException会被认为是一个词，不会进行拆分
         */
        mss.searchBySelfScore();
    }

}
