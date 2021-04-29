package shortener;
import shortener.strategy.StorageStrategy;


public class Shortener {
    private Long lastId = 0l;
    private StorageStrategy storageStrategy;

    public Shortener(StorageStrategy storageStrategy) {
        this.storageStrategy = storageStrategy;
    }

    synchronized public Long getId(String string){
        if (storageStrategy.containsValue(string)){
            return storageStrategy.getKey(string);
        }
        // if don't have storage value
        lastId++;
        storageStrategy.put(lastId, string);
        return lastId;
    }

    synchronized public String getString(Long id){
        return storageStrategy.getValue(id);
    }
}
