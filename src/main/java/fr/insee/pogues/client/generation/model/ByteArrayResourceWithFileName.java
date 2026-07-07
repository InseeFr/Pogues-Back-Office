package fr.insee.pogues.client.generation.model;

import org.springframework.core.io.ByteArrayResource;

public class ByteArrayResourceWithFileName extends ByteArrayResource {

    private final String fileName;

    public ByteArrayResourceWithFileName(String fileName, byte[] byteArray) {
        super(byteArray);
        this.fileName = fileName;
    }
    public String getFilename() {
        return fileName;
    }
}
