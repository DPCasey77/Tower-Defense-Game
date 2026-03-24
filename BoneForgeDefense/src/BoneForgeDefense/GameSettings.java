package BoneForgeDefense;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class GameSettings {
	
	//add new settings variables here "NameAsString", Default Value
	private HashMap<String, Object> settingsMap = new HashMap<>(Map.of(
		"CONFIG_FILE", "config.dat",
		"screenHeight", 1080,
		"screenWidth", 1920,
		"volume", 100,
		"test","test"
		));
	
	
	//Constructor
	public GameSettings() {
		
		//checks if the settings files exist, if exists load settings, if not create new default settings file
		Path currentDirPath = Paths.get("").toAbsolutePath();
		Path path = Path.of(currentDirPath + "/" + settingsMap.get("CONFIG_FILE"));
		try {Files.createFile(path);
			saveSettings();
            System.out.println("File created successfully at: " + path.toAbsolutePath());
        } catch (java.nio.file.FileAlreadyExistsException e) {
            loadSettings();
            saveSettings();
        } catch (IOException e) {
            // Handle other I/O errors
            System.out.println("An I/O error occurred: " + e.getMessage());
            e.printStackTrace();
        }
	}
	
	//Load Settings
	@SuppressWarnings("unchecked")
	public void loadSettings() {
		String fileName = (String) settingsMap.get("CONFIG_FILE");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            settingsMap = (HashMap<String, Object>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
	}
	
	//Save Settings
	public void saveSettings() {
		String fileName = (String) settingsMap.get("CONFIG_FILE");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(settingsMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	//get settings method
	public Object getSetting(String key) 
	{
		return settingsMap.get(key);
	}
	
	//set settings method
	public void setSetting(String key, Object value)
	{
		settingsMap.put(key,value);
	}

}
