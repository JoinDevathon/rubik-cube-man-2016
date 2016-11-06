package org.devathon.contest2016.config;

import gnu.trove.map.hash.TObjectIntHashMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.devathon.contest2016.DevathonPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class ConfigSaver implements Listener{

    private static ConfigSaver instance;

    public static ConfigSaver getInstance(){
        return instance;
    }

    private Set<SaveableObject> saveableObjects = new HashSet<>();

    public ConfigSaver(DevathonPlugin plugin){
        instance = this;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        load();
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event){
        save();
    }

    public void addObject(SaveableObject object){
        saveableObjects.add(object);
    }

    public void removeObject(SaveableObject object){
        saveableObjects.remove(object);
    }

    public void load(){
        DevathonPlugin plugin = DevathonPlugin.getPlugin(DevathonPlugin.class);
        FileConfiguration config = plugin.getConfig();
        if (config.getConfigurationSection("objects") != null){
            for (String clazzName : config.getConfigurationSection("objects").getKeys(false)){
                Class<? extends SaveableObject> clazz;
                try {
                    clazz = (Class<? extends SaveableObject>) Class.forName(clazzName.replace("#", "."));
                } catch (Exception e){
                    e.printStackTrace();
                    continue;
                }
                try {
                    Method method = clazz.getMethod("load", ConfigurationSection.class);
                    if (config.getConfigurationSection("objects." + clazzName) != null){
                        for (String id : config.getConfigurationSection("objects." + clazzName).getKeys(false)){
                            SaveableObject object = (SaveableObject) method.invoke(null, config.getConfigurationSection("objects." + clazzName + "." + id));
                            if (object != null)
                                addObject(object);
                        }
                    }
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void save(){
        DevathonPlugin plugin = DevathonPlugin.getPlugin(DevathonPlugin.class);
        FileConfiguration config = plugin.getConfig();
        config.set("objects", null);
        TObjectIntHashMap<Class<? extends SaveableObject>> classCount = new TObjectIntHashMap<>();
        for (SaveableObject object : saveableObjects){
            Class<? extends SaveableObject> clazz = object.getClass();
            int value = classCount.adjustOrPutValue(clazz, 1, 1);
            String sectionName = "objects." + clazz.getCanonicalName().replace(".", "#") + "." + value;
            ConfigurationSection section = plugin.getConfig().createSection(sectionName);
            object.save(section);
        }
        try {
            config.save(plugin.getDataFolder() + File.separator + "config.yml");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
