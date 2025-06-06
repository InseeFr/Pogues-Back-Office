package fr.insee.pogues.utils.model;

public class ZipFile {
    private final String entryName;
    private final byte[] content;

    public ZipFile(String entryName, byte[] content) {
        this.entryName = entryName;
        this.content = content;
    }

    public String getEntryName() {
        return entryName;
    }

    public byte[] getContent() {
        return content;
    }
}
