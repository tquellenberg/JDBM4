package net.kotek.jdbm;

import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class CacheWeakSoftRefTest {


    @Test
    public void weak_htree_inserts_delete() throws InterruptedException {
        DB db = DBMaker
                .newMemoryDB()
                .cacheWeakRefEnable()
                .asyncWriteDisable()
                .make();
        testMap(db);
    }

    @Test
    public void soft_htree_inserts_delete() throws InterruptedException {
        DB db = DBMaker
                .newMemoryDB()
                .cacheSoftRefEnable()
                .make();
        testMap(db);
    }


    private void testMap(DB db) throws InterruptedException {
        Map<Integer, Integer> m = db.getHashMap("name");
        for(Integer i = 0;i<1000;i++){
            m.put(i,i);
        }
        CacheWeakSoftRef recman = (CacheWeakSoftRef)db.recman;
        assertTrue(recman.items.size()!=0);

        for(Integer i = 0;i<1000;i++){
            Integer a = m.remove(i);
            assertEquals(i, a);
        }
        Thread t = recman.queueThread;
        db.close();
        Thread.sleep(100);
        assertEquals(Thread.State.TERMINATED, t.getState());
    }
}
