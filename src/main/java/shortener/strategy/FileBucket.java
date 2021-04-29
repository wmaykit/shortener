package shortener.strategy;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;


public class FileBucket {
    private Path path;

    public FileBucket() {
        try {
            this.path = Files.createTempFile("temp-", ".tmp");
            Files.deleteIfExists(path);
            Files.createFile(path);
            this.path.toFile().deleteOnExit();
        } catch (IOException e) {
        }
    }

    public long getFileSize(){
        long size = 0;
        try {
            size = Files.size(this.path);
        } catch (IOException e) {
        }
        return size;
    }

    public void putEntry(Entry entry){
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(path))) {
            int count = 0;
            Entry iterator = entry;
            while (iterator != null) {
                count++;
                iterator = iterator.next;
            }
            objectOutputStream.writeInt(count);
            iterator = entry;
            while (iterator != null) {
                objectOutputStream.writeObject(iterator);
                iterator = iterator.next;
            }
        } catch (Exception e) {
        }
    }

    public Entry getEntry() {
        if (getFileSize() > 0) {
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
                int amountEntry = in.readInt() - 1;
                Entry result = (Entry) in.readObject();
                Entry iterator = result;
                while (amountEntry-- > 0) {
                    iterator.next = (Entry) in.readObject();
                    iterator = iterator.next;
                }
                return result;
            } catch (Exception e){
            }
        }
        return null;
    }

    public void remove(){
        try {
            Files.delete(this.path);
        } catch (IOException e) {
        }
    }
}
