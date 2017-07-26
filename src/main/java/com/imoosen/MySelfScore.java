package com.imoosen;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldCache.Longs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
/**
 * Created by [mengsen] on 2017/7/26 0026.
 *
 * @Description: [一句话描述该类的功能]
 * @UpdateUser: [mengsen] on 2017/7/26 0026.
 */
public class MySelfScore {

    public void searchBySelfScore(){
        try{
            IndexSearcher search = new IndexSearcher(DirectoryReader.open(FileIndexUtils.getDirectory()));
            Query q = new TermQuery(new Term("content","sen"));
            MyCustomScoreQuery myQuery = new MyCustomScoreQuery(q);
            TopDocs tds = search.search(myQuery, 200);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for(ScoreDoc sd:tds.scoreDocs){
                Document d = search.doc(sd.doc);
                System.out.println(sd.doc+":("+sd.score+")" +
                        "["+d.get("filename")+"【"+d.get("path")+"】--->"+
                        d.get("size")+"-----"+sdf.format(new Date(Long.valueOf(d.get("date"))))+"]");

            }
            System.out.println("-----------Total result:"+tds.scoreDocs.length);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     *重写评分的实现方式
     * **/
    private class MyScoreProvider extends CustomScoreProvider{
        private AtomicReaderContext context;
        public MyScoreProvider(AtomicReaderContext context) {
            super(context);
            this.context = context;
        }
        /**重写评分方法，假定需求为文档size大于1000的评分/1000**/
        @Override
        public float customScore(int doc, float subQueryScore, float valSrcScore){
                    float ff = 1f;//判断加权
                     try{
                         // 从域缓存中加载索引字段信息
                         Longs longs= FieldCache.DEFAULT.getLongs(context.reader(), "size", false);
                         //doc实际上就是Lucene中得docId
                         long size = longs.get(doc);

                         if(size>1000){
                             ff = 1f/1000;
                         }
                        /*
                         * 通过得分相乘放大分数
                         * 此处可以控制与原有得分结合的方式，加减乘除都可以
                         * **/

                     }catch (Exception e){
                         e.printStackTrace();
                     }
            return subQueryScore*valSrcScore*ff;
        }
    }
    /**
     * 重写CustomScoreQuery 的getCustomScoreProvider方法
     * 引用自定义的Provider
     */
    private class MyCustomScoreQuery extends CustomScoreQuery{

        public MyCustomScoreQuery(Query subQuery) {
            super(subQuery);
        }
        @Override
        protected CustomScoreProvider getCustomScoreProvider(AtomicReaderContext context) {
            /**注册使用自定义的评分实现方式**/
            return new MyScoreProvider(context);
        }
    }
}
