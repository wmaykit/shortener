package shortener.tests;

import org.junit.Assert;
import org.junit.Test;
import shortener.Helper;
import shortener.Shortener;
import shortener.strategy.*;

public class FunctionalTest {

    public void testStorage(Shortener shortener){
        String[] string = new String[4];
        long[] id = new long[4];

        string[1] = Helper.generateRandomString();
        string[2] = Helper.generateRandomString();
        string[3] = string[1];

        for (int i = 1; i < string.length; i++) {
            id[i] = shortener.getId(string[i]);
        }

        Assert.assertNotEquals(id[2], id[1]);
        Assert.assertNotEquals(id[2], id[3]);
        Assert.assertEquals(id[1], id[3]);

        for (int i = 1; i < string.length; i++) {
            Assert.assertEquals(shortener.getString(id[i]), string[i]);
        }
    }

    @Test
    public void testHashMapStorageStrategy(){
        Shortener shortener = new Shortener(new HashMapStorageStrategy());
        testStorage(shortener);
    }

    @Test
    public void testOurHashMapStorageStrategy(){
        Shortener shortener = new Shortener(new OurHashMapStorageStrategy());
        testStorage(shortener);
    }

    @Test
    public void testFileStorageStrategy(){
        Shortener shortener = new Shortener(new FileStorageStrategy());
        testStorage(shortener);
    }

    @Test
    public void testHashBiMapStorageStrategy(){
        Shortener shortener = new Shortener(new HashBiMapStorageStrategy());
        testStorage(shortener);
    }

    @Test
    public void testDualHashBidiMapStorageStrategy(){
        Shortener shortener = new Shortener(new DualHashBidiMapStorageStrategy());
        testStorage(shortener);
    }

    @Test
    public void testOurHashBiMapStorageStrategy(){
        Shortener shortener = new Shortener(new OurHashBiMapStorageStrategy());
        testStorage(shortener);
    }
}
