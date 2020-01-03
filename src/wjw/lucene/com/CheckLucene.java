package wjw.lucene.com;

import org.junit.Test;

public class CheckLucene {

    /**
     * 创建索引
     */
    @Test
    public void testCreateIndex(){
        IndexUtil.createIndex();
        IndexUtil.check();
    }

    /**
     * 搜索
     */
    @Test
    public void testSearchFile(){
        IndexUtil.searchFile();
    }

    /**
     * 删除索引
     */
    @Test
    public void testDelete(){
        IndexUtil.delete();
        IndexUtil.check();
    }

    /**
     * 删除索引
     */
    @Test
    public void testUpdate(){
        IndexUtil.update();
        IndexUtil.check();
    }

}
