package wjw.lucene.com;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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
import java.io.FileReader;

public class HelloLuceneTest {

    /**
     * 创建文件索引
     */
    @Test
    public void index() {
        IndexWriter indexWriter = null;
        try {
            //  1、创建Directory
            //  建立在内存中
            //  Directory directory = new RAMDirectory();
            //  建立在硬盘式
            Directory directory = FSDirectory.open(new File("D:/test/lucene/index"));
            //  2、创建IndexWriter
            //  使用默认的标准分词器
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_35, analyzer);
            indexWriter = new IndexWriter(directory, indexWriterConfig);
            File dfile = new File("D:/test/lucene/document");
            File[] files = dfile.listFiles();
            for (File file : files) {
                // 3、创建Document对象
                Document document = new Document();

                // 4、为Document添加Field
                document.add(new Field("content", new FileReader(file)));
                document.add(new Field("filename", file.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
                document.add(new Field("filepath", file.getAbsolutePath(), Field.Store.YES, Field.Index.NOT_ANALYZED));

                // 5、通过IndexWriter添加文档到索引中
                indexWriter.addDocument(document);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 搜索文件
     */
    @Test
    public void search() {
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

            // 在content中搜索Darren
            // 创建parser来确定要搜索文件的内容，第二个参数为搜索的域

            QueryParser queryParser = new QueryParser(Version.LUCENE_35, "content", analyzer);
            // 创建Query表示搜索域为content包含Darren的文档
            Query query = queryParser.parse("王健伟");

            // 5、根据searcher搜索并且返回TopDocs
            TopDocs topDocs = indexSearcher.search(query, 10);
            // 6、根据TopDocs获取ScoreDoc对象
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                // 7、根据searcher和ScoreDoc对象获取具体的Document对象
                Document document = indexSearcher.doc(scoreDoc.doc);
                // 8、根据Document对象获取需要的值
                System.out.println(document.get("filename") + " " + document.get("filepath"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (indexReader != null) {
                    indexReader.clone();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}
