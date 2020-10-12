public class FileDispatch extends AbstractMessage {
    long size;
    String name;

    public void setSize(long size) {
        this.size = size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }
}
