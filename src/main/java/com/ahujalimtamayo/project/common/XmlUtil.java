package com.ahujalimtamayo.project.common;

import com.ahujalimtamayo.project.model.Warrior;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class XmlUtil {

    private static final XmlMapper XML_MAPPER = new XmlMapper();

    static {
        XML_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        XML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    public static Warrior readWarriorFromFile(String path) throws IOException {
        String warriorInString = FileUtils.readFileToString(new File(path));
        return XML_MAPPER.readValue(warriorInString, Warrior.class);
    }

    public static void writeWarriorToFile(Warrior warrior, String path) throws IOException {
        XML_MAPPER.writeValue(new File(path), warrior);
    }


}
