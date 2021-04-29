package shortener.strategy;

public class FileStorageStrategy implements StorageStrategy {
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final long DEFAULT_BUCKET_SIZE_LIMIT = 10000;

    private FileBucket[] table = new FileBucket[DEFAULT_INITIAL_CAPACITY];
    int size;
    private long bucketSizeLimit = DEFAULT_BUCKET_SIZE_LIMIT;
    long maxBucketSize = 0;

    public long getBucketSizeLimit() {
        return bucketSizeLimit;
    }

    public void setBucketSizeLimit(long bucketSizeLimit) {
        this.bucketSizeLimit = bucketSizeLimit;
    }

    private int hash(Long k){
        int h;
        return (k == null) ? 0 : (h = k.hashCode()) ^ (h >>> 16);
    }

    private int indexFor(int hash, int length){
        return hash & (length - 1);
    }


    private Entry getEntry(Long key){
        FileBucket[] tab;
        FileBucket bucket;
        Entry first, e;
        int n, hash;
        Long k;

        if ((tab = table) != null && (n = tab.length) > 0 && (bucket = tab[(n - 1) & (hash = hash(key))]) != null) {
            first = bucket.getEntry();
            if (first.hash == hash &&
                    ((k = first.key) == key || (key != null && key.equals(k)))) {
                return first;
            }
            if ((e = first.next) != null) {
                do {
                    if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }

    private void resize(int newCapacity){
        FileBucket[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        if (newCapacity <= oldCap)
            return;
        FileBucket[] newTab = new FileBucket[newCapacity];
        transfer(newTab);
        table = newTab;
    }

    private void transfer(FileBucket[] newTab){
        FileBucket[] oldTab = table;
        int oldCap = oldTab.length;
        int newCap = newTab.length;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                FileBucket f;
                if ((f = oldTab[j]) != null) {
                    Entry e = f.getEntry();
                    oldTab[j].remove();
                    if (e.next == null){
                        f = new FileBucket();
                        f.putEntry(e);
                        newTab[e.hash & (newCap - 1)] = f;
                    } else { // preserve order
                        Entry loHead = null, loTail = null;
                        Entry hiHead = null, hiTail = null;
                        Entry next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            f = new FileBucket();
                            f.putEntry(loHead);
                            newTab[j] = f;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            f = new FileBucket();
                            f.putEntry(hiHead);
                            newTab[j + oldCap] = f;
                        }
                    }
                }
            }
        }
    }

    private void addEntry(int hash, Long key, String value, int bucketIndex){
        FileBucket[] tab;
        Entry p, save;
        FileBucket bucket;
        tab = table;
        if ((bucket = tab[bucketIndex]) == null) {
            createEntry(hash, key, value, bucketIndex);
            bucket = tab[bucketIndex];
        }else {
            p = bucket.getEntry();
            save = p;
            Entry e; Long k;
            if (p.hash == hash &&
                    ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            else {
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        p.next = new Entry(hash, key, value, null);
                        break;
                    }
                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key
                String oldValue = e.value;
                if (oldValue == null)
                    e.value = value;
            }
            bucket.putEntry(save);
        }
        if (bucket.getFileSize() > maxBucketSize)
            maxBucketSize = bucket.getFileSize();
        ++size;
        if (maxBucketSize > bucketSizeLimit)
            resize(table.length * 2);
    }

    void createEntry(int hash, Long key, String value, int bucketIndex){
        table[bucketIndex] = new FileBucket();
        table[bucketIndex].putEntry(new Entry(hash, key, value, null));
    }

    @Override
    public boolean containsKey(Long key) {
        return getEntry(key) != null;
    }

    @Override
    public boolean containsValue(String value) {
        return getKey(value) != null;
    }

    @Override
    public void put(Long key, String value) {
        addEntry(hash(key), key, value, indexFor(hash(key), table.length));
    }

    @Override
    public Long getKey(String value) {
        for (int i = 0; i < table.length; i++) {
            FileBucket bucket = table[i];
            Entry iterator = (bucket == null ? null : bucket.getEntry());
            while (iterator != null){
                if (iterator.value.equals(value))
                    return iterator.getKey();
                iterator = iterator.next;
            }
        }
        return null;
    }

    @Override
    public String getValue(Long key) {
        Entry current = getEntry(key);
        return (current == null ? null : current.getValue());
    }
}
