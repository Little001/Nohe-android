package nohe.nohe_android.nohe_cz.services;

public class DataFile {
    private String fileName;
    private byte[] content;
    private String type;

    public DataFile() {
    }

    public DataFile(String name, byte[] data) {
        fileName = name;
        content = data;
    }

    String getFileName() {
        return fileName;
    }

    byte[] getContent() {
        return content;
    }

    String getType() {
        return type;
    }
}
