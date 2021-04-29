package shortener.strategy;

public class OurHashMapStorageStrategy implements StorageStrategy{
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    Entry[] table = new Entry[DEFAULT_INITIAL_CAPACITY];
    int size;
    int threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
    float loadFactor = DEFAULT_LOAD_FACTOR;

    private int hash(Long k){
        int h;
        return (k == null) ? 0 : (h = k.hashCode()) ^ (h >>> 16);
    }

    private int indexFor(int hash, int length){
        return hash & (length - 1);
    }

    private Entry getEntry(Long key){
       Entry[] tab;
       Entry first, e;
       int n, hash;
       Long k;

        if ((tab = table) != null && (n = tab.length) > 0 &&
                (first = tab[(n - 1) & (hash = hash(key))]) != null) {
            if (first.hash == hash && ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            if ((e = first.next) != null) {
                do {
                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }

    private void resize(int newCapacity){
        final int MAXIMUM_CAPACITY = 1 << 30;
        Entry[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                table = oldTab;
                return;
            }
            else if ((newCap = newCapacity) < MAXIMUM_CAPACITY &&
                    oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                    (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        Entry[] newTab = new Entry[newCap];
        transfer(newTab);
        table = newTab;
    }

    private void transfer(Entry[] newTab){
        Entry[] oldTab = table;
        int oldCap = oldTab.length;
        int newCap = newTab.length;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Entry e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else { // preserve order
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
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
    }

    private void addEntry(int hash, Long key, String value, int bucketIndex){
       Entry[] tab; Entry p; int n, i;
       tab = table;
       n = tab.length;
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = new Entry(hash, key, value, null);
        else {
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
        }
        if (++size > threshold)
            resize(table.length * 2);
    }

    void createEntry(int hash, Long key, String value, int bucketIndex){
        table[bucketIndex] = new Entry(hash, key, value, null);
        if (++size > threshold)
            resize(table.length * 2);
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
        Entry current;
        if ((current = getEntry(key)) == null){
            addEntry(hash(key), key, value, indexFor(hash(key), table.length));
        } else{
            current.value = value;
        }
    }

    @Override
    public Long getKey(String value) {
        for (int i = 0; i < table.length; i++) {
            Entry iterator = table[i];
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
