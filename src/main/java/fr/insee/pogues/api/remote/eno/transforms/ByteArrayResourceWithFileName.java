package fr.insee.pogues.api.remote.eno.transforms;

import org.springframework.core.io.ByteArrayResource;

public class ByteArrayResourceWithFileName extends ByteArrayResource {

    private String fileName;

    public ByteArrayResourceWithFileName(String fileName, byte[] byteArray) {
        super(byteArray);
        this.fileName = fileName;
    }
    public String getFilename() {
        return fileName;
    }
}
