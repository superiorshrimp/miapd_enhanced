package main;

import org.json.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

public class PhonesGenerator{
    private final LinkedList<Phone> phones = new LinkedList<>();
    public PhonesGenerator(String directoryPath){
        File folder = new File(directoryPath);
        File[] listOfFiles = folder.listFiles();

        if(listOfFiles != null){
            for(File file : listOfFiles){
                if(file.isFile()){
                    try{
                        String content = Files.readString(Path.of(directoryPath + file.getName()));
                        JSONObject json = new JSONObject(content);
                        Phone phone = new Phone(
                                json.getString("name"),
                                json.getInt("price"),
                                1,
                                json.getInt("ram"),
                                json.getInt("cpu"),
                                json.getInt("storage"),
                                json.getInt("battery"),
                                json.getInt("charging"),
                                1
                        );
                        this.phones.add(phone);
                    }catch(Exception e){e.printStackTrace();}
                }
            }
        }
    }
    public LinkedList<Phone> getPhones(){return this.phones;}
}
