package pw.biome.biomechat.obj;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class NicknameConfig {
    private static File file;

    @Getter
    private static FileConfiguration config;

    public static void setupConfig(){
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("BiomeChat").getDataFolder(),"nicknameconfig.yml");

        if(!file.exists()){
            try{
                file.createNewFile();

            }catch(IOException e){
                e.printStackTrace();
            }
        }

        config = new YamlConfiguration().loadConfiguration(file);


    }

    public static void saveConfig(){
        try {
            config.save(file);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void reloadConfig() {
        config = new YamlConfiguration().loadConfiguration(file);
    }
}
