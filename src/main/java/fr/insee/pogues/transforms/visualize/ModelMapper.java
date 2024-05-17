package fr.insee.pogues.transforms.visualize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ModelMapper {

    public static String inputStream2String(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    public static InputStream string2InputStream(String input){
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }

    public static ByteArrayOutputStream string2BOAS(String string) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(string.getBytes(StandardCharsets.UTF_8));
        return byteArrayOutputStream;
    }

    public static InputStream output2Input(ByteArrayOutputStream outputStream){
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public static ByteArrayOutputStream input2Output(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(inputStream.readAllBytes());
        return byteArrayOutputStream;
    }
}
