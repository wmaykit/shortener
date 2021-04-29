package shortener;

import shortener.strategy.*;
import shortener.tests.SpeedTest;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Solution {
    public static void main(String[] args) throws IOException, InterruptedException {
        testStrategy(new HashMapStorageStrategy(), 10_000l);
        testStrategy(new OurHashMapStorageStrategy(), 10_000l);
        testStrategy(new OurHashBiMapStorageStrategy(), 10_000l);
        testStrategy(new HashBiMapStorageStrategy(), 10_000l);
        testStrategy(new DualHashBidiMapStorageStrategy(), 10_000l);
        testStrategy(new FileStorageStrategy(), 100l);
    }

    public static Set<Long> getIds(Shortener shortener, Set<String> strings){
        Set<Long> ids = new HashSet<>();
        for (String str : strings){
            ids.add(shortener.getId(str));
        }
        return ids;
    }

    public static Set<String> getStrings(Shortener shortener, Set<Long> keys){
        return keys.stream().map((k)->shortener.getString(k)).collect(Collectors.toSet());
    }

    public static void testStrategy(StorageStrategy strategy, long elementsNumber){
        Helper.printMessage(strategy.getClass().getSimpleName());
        Set<String> set = new HashSet<>();
        Set<Long> ids;
        Set<String> values;
        for (int i = 0; i < elementsNumber; i++) {
            set.add(Helper.generateRandomString());
        }
        Shortener shortener = new Shortener(strategy);
        Date begin = new Date();
        ids = getIds(shortener, set);
        Date end = new Date();
        Helper.printMessage(Long.toString(end.getTime() - begin.getTime()));
        begin = new Date();
        values = getStrings(shortener, ids);
        end = new Date();
        Helper.printMessage(Long.toString(end.getTime() - begin.getTime()));
        if (set.containsAll(values)){
            Helper.printMessage("Тест пройден.");
        } else{
            Helper.printMessage("Тест не пройден.");
        }
    }
}
