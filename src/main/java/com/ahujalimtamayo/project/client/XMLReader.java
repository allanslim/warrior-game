package com.ahujalimtamayo.project.client;

import com.ahujalimtamayo.project.model.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.io.FileUtils;

import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.IOException;

public class XMLReader {




    public static void main(String[] args) {
        Warrior warrior = new Warrior("Hercules", "Man with Godlike strength.","Rome");
//        warrior.getAttacks().add(new Kick());
//        warrior.getAttacks().add(new Punch());
//        warrior.getAttacks().add(new Stab());

//        warrior.getDefenses().add(new Parry());
//        warrior.getDefenses().add(new SideStep());
//        warrior.getDefenses().add(new Block());



        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {

            xmlMapper.writeValue(new File("Warrior.wdat"), warrior);

            String warriorInString = FileUtils.readFileToString(new File("/Users/lima012/notes/warrior-game/src/main/resources/Hercules.wdat"));
            Warrior warrior1 = xmlMapper.readValue(warriorInString, Warrior.class);

            System.out.print("Warrior is: " + warrior1);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
