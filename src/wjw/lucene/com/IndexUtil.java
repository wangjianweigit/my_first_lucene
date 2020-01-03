package wjw.lucene.com;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 索引工具类
 */
public class IndexUtil {
    static String[] ids = new String[]{"1", "2", "3"};
    static String[] name = new String[]{"zhangsan", "lisi", "wanger"};
    static String[] age = new String[]{"18", "20", "36"};
    static String[] content = new String[]{"my name is zhangsan", "my name is lisi", "my name is wanger"};

    /**
     * 建立索引
     */
    @Test
    public static void createIndex() {
        IndexWriter indexWriter = null;
        try {
            // 1、创建Directory
            Directory directory = FSDirectory.open(new File("D:/test/lucene/index"));

            // 2、创建IndexWriter
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
            indexWriter = new IndexWriter(directory, config);
            for (int i = 0; i < ids.length; i++) {
                // 3、创建Document对象
                Document document = new Document();

                // 4、为Document添加Field
                /**
                 * @param  Field.Index.ANALYZED     进行分词
                 * @param  Field.Index.NOT_ANALYZED     不分词
                 * @param  Field.Index.NOT_ANALYZED_NOT_NORMS 进行分词但是不存储norms信息,这个norms包括创建索引的时间和权值等信息
                 */
                document.add(new Field("id", ids[i], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
                // 对Author存储，但是不分词也不存储norms信息,这个norms中包括了创建索引的时间和权值等信息
                document.add(new Field("name", name[i], Field.Store.YES, Field.Index.ANALYZED));
                // 对Title存储，分词
                document.add(new Field("age", age[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
                // 对Content不存储，但是分词
                /**
                 * 注：添加内容或文件是默认是不存储的，这个查询时可以证明这个问题
                 *
                 * new Field(name, reader)
                 *
                 * 那么问题来了，如果想存文件内容怎么办呢？
                 *
                 * 那就把文件读出来，比如读出字符串，然后不就能按字符串的方式存储啦
                 */
                document.add(new Field("content", content[i], Field.Store.YES, Field.Index.ANALYZED));

                // 5、通过IndexWriter添加文档到索引中
                indexWriter.addDocument(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (indexWriter != null) {
                    indexWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 搜索
     */
    @Test
    public static void searchFile() {
        IndexReader indexReader = null;
        try {
            // 1、创建Directory
            Directory directory = FSDirectory.open(new File("D:/test/lucene/index"));
            // 2、创建IndexReader
            indexReader = IndexReader.open(directory);
            // 3、根据IndexReader创建IndexSearch
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            // 4、创建搜索的Query
            // 使用默认的标准分词器
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);

            // 在content中搜索Lucene
            // 创建parser来确定要搜索文件的内容，第二个参数为搜索的域
            QueryParser queryParser = new QueryParser(Version.LUCENE_35, "content", analyzer);
            // 创建Query表示搜索域为content包含Lucene的文档
            Query query = queryParser.parse("lisi");

            // 5、根据searcher搜索并且返回TopDocs
            TopDocs topDocs = indexSearcher.search(query, 10);
            // 6、根据TopDocs获取ScoreDoc对象
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                // 7、根据searcher和ScoreDoc对象获取具体的Document对象
                Document document = indexSearcher.doc(scoreDoc.doc);
                // 8、根据Document对象获取需要的值
                System.out.println("id : " + document.get("id"));
                System.out.println("name : " + document.get("name"));
                System.out.println("age : " + document.get("age"));
                System.out.println("content : " + document.get("content"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (indexReader != null) {
                    indexReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除索引
     */
    @Test
    public static void delete() {
        IndexWriter indexWriter = null;
        try {
            Directory directory = FSDirectory.open(new File("F:/test/lucene/index"));
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_35, analyzer);
            indexWriter = new IndexWriter(directory, indexWriterConfig);
            /**
             * 参数是一个选项，可以是一个Query，也可以是一个term，term是一个精确查找的值
             *
             * 此时删除的文档并不会被完全删除，而是存储在一个回收站中的，可以恢复
             */

            //  方式一：通过Term删除

            /**
             * 注意Term构造器的意思，第一个参数为Field，第二个参数为Field的值
             */
            indexWriter.deleteDocuments(new Term("id", "1"));

            //  方式二：通过Query删除

            /**
             * 这里就要造一个Query出来，删掉查处的索引
             */
            //QueryParser queryParser = new QueryParser(Version.LUCENE_35, "content", analyzer);
            //  创建Query表示搜索域为content包含Lucene的文档
            //Query query = queryParser.parse("name");

            //indexWriter.deleteDocuments(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (indexWriter != null) {
                    indexWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 恢复删除的索引
     */
    public static void unDelete() {
        //  使用IndexReader进行恢复
        IndexReader indexReader = null;
        try {
            Directory directory = FSDirectory.open(new File("D:/test/lucene/index"));
            //   恢复时，必须把IndexReader的只读(readOnly)设置为false
            //   索引没有改变可以使用true，但现在是恢复删除的索引，显然是改变过的，所以只能是false
            indexReader = IndexReader.open(directory, false);
            indexReader.undeleteAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (indexReader != null) {
                    indexReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新索引
     */
    public static void update() {
        IndexWriter indexWriter = null;
        try {
            Directory directory = FSDirectory.open(new File("D:/test/lucene/index"));
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_35, analyzer);
            indexWriter = new IndexWriter(directory, indexWriterConfig);
            /**
             * Lucene并没有提供更新，这里的更新操作其实是如下两个操作的合集 先删除之后再添加
             */
            Document document = new Document();
            document.add(new Field("id", "1", Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
            document.add(new Field("name", name[0], Field.Store.YES, Field.Index.NOT_ANALYZED));
            document.add(new Field("age", age[0], Field.Store.YES, Field.Index.ANALYZED));
            document.add(new Field("content", content[1], Field.Store.NO, Field.Index.ANALYZED));
            indexWriter.updateDocument(new Term("id", "1"), document);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (indexWriter != null) {
                    indexWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查索引文件
     */
    @Test
    public static void check() {
        IndexReader indexReader = null;
        try {
            Directory directory = FSDirectory.open(new File("D:/test/lucene/index"));
            indexReader = IndexReader.open(directory);
            //  通过reader可以有效的获取到文档的数量
            //  有效的索引文档
            System.out.println("有效的索引文档:" + indexReader.numDocs());
            //  总共的索引文档
            System.out.println("总共的索引文档:" + indexReader.maxDoc());
            //  删掉的索引文档，其实不恰当，应该是在回收站里的索引文档
            System.out.println("删掉的索引文档:" + indexReader.numDeletedDocs());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (indexReader != null) {
                    indexReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //  合并索引 Lucene会自动优化索引，索引不用担心索引文件一直变大变多这个问题
}
