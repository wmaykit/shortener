package shortener.tests;

import org.junit.Assert;
import org.junit.Test;
import shortener.Helper;
import shortener.Shortener;
import shortener.strategy.HashBiMapStorageStrategy;
import shortener.strategy.HashMapStorageStrategy;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class SpeedTest {

    public long getTimeToGetIds(Shortener shortener, Set<String> strings, Set<Long> ids){
        Date begin = new Date();
        strings.stream().map((s) -> shortener.getId(s)).forEach(ids::add);
        return new Date().getTime() - begin.getTime();
    }

    public long getTimeToGetStrings(Shortener shortener, Set<Long> ids, Set<String> strings){
        Date begin = new Date();
        ids.stream().map((i) -> shortener.getString(i)).forEach(strings::add);
        return new Date().getTime() - begin.getTime();
    }

    @Test
    public void testHashMapStorage(){
        Shortener shortener1 = new Shortener(new HashMapStorageStrategy());
        Shortener shortener2 = new Shortener(new HashBiMapStorageStrategy());

        Set<String> origStrings = new HashSet<>();
        Stream.generate(() -> Helper.generateRandomString()).limit(10_000).map(origStrings::add);

        Set<Long> idsShortener1 = new HashSet<Long>();
        Set<Long> idsShortener2 = new HashSet<Long>();

        long timeShortener1 = getTimeToGetIds(shortener1, origStrings, idsShortener1);
        long timeShortener2 = getTimeToGetIds(shortener2, origStrings, idsShortener2);

        Assert.assertTrue(timeShortener1 > timeShortener2);

        timeShortener1 = getTimeToGetStrings(shortener1, idsShortener1, new HashSet<>());
        timeShortener2 = getTimeToGetStrings(shortener2, idsShortener2, new HashSet<>());

        Assert.assertEquals(timeShortener1, timeShortener2, 30);
    }
}
