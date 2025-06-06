package fr.insee.pogues.utils.model;

import lombok.Getter;

@Getter
public class ZipFile {
    private final String entryName;
    private final byte[] content;

    public ZipFile(String entryName, byte[] content) {
        this.entryName = entryName;
        this.content = content;
    }

}
