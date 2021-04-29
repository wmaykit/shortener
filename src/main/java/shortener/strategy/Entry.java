package shortener.strategy;

import java.io.Serializable;
import java.util.Objects;

public class Entry implements Serializable {
    int hash;
    Long key;
    String value;
    Entry next;

    public Entry(int hash, Long key, String value, Entry next) {
        this.hash = hash;
        this.key = key;
        this.value = value;
        this.next = next;
    }

    public String getValue() {
        return value;
    }

    public Long getKey(){
        return key;
    }

    public int hashCode(){
        return Objects.hashCode(key) ^ Objects.hashCode(value);
    }

    public boolean equals(Object o){
        if (o == this)
            return true;
        if (o instanceof Entry) {
            Entry e = (Entry) o;
            if (Objects.equals(key, e.getKey()) &&
                    Objects.equals(value, e.getValue()))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
