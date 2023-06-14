package slimon.mods.aos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Notezway on 12.08.2016.
 */
public class RecipeConfig {

    private File configFile;
    private Map<String, Entry> entryMap;
    private Parser parser;

    public RecipeConfig(File file) {
        if(file == null) {
            createDefaultConfigFile();
        }
        else if(!file.exists()) {
            System.out.println("not exist");
            if(!createConfigFile(file)) {
                createDefaultConfigFile();
            }
        }
        else {
            configFile = file;
        }
        entryMap = new HashMap<String, Entry>();
        parser = new Parser(configFile);
    }

    public void load() {
        entryMap = parser.load();
    }

    public void save() {
        parser.save(entryMap);
    }

    public Entry getEntry(String name, Class type, Object defaultValue) {
        Entry entry;
        if(!entryMap.containsKey(name)) {
            entry = new Entry(type, defaultValue);
            entryMap.put(name, entry);
        } else {
            entry = entryMap.get(name);
        }
        return entry;
    }

    public void setEntry(String name, Entry entry) {
        entryMap.put(name, entry);
    }

    public String getString(String name, String defaultValue) {
        return (String) getEntry(name, String.class, defaultValue).value;
    }

    public String getString(String name) {
        return getString(name, "");
    }

    public int getInteger(String name, int defaultValue) {
        return (Integer) getEntry(name, Integer.class, defaultValue).value;
    }

    public int getInteger(String name) {
        return getInteger(name, 0);
    }

    public float getFloat(String name, float defaultValue) {
        return (Float) getEntry(name, Float.class, defaultValue).value;
    }

    public float getFloat(String name) {
        return getFloat(name, 0F);
    }

    public void setString(String name, String value) {
        setEntry(name, new Entry(String.class, value));
    }

    public void setInteger(String name, int value) {
        setEntry(name, new Entry(Integer.class, value));
    }

    public void setFloat(String name, float value) {
        setEntry(name, new Entry(Float.class, value));
    }

    private boolean createConfigFile(File file) {
        configFile = file;
        try {
            return configFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean createDefaultConfigFile() {
        return createConfigFile(new File("config.cfg"));
    }

    public

    class Entry<T> {
        final Class<T> typeParameterClass;
        T value;

        public Entry(Class<T> typeParameterClass, T value) {
            this.typeParameterClass = typeParameterClass;
            this.value = value;
        }
    }

    class Parser {

        File file;

        public Parser(File file) {
            this.file = file;
        }

        public Map<String, Entry> load() {
            Map<String, Entry> entryMap = new HashMap<String, Entry>();
            try {
                Scanner scanner = new Scanner(file);
                String line;
                String[] buffer;
                while (scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    if (line.startsWith("//")) continue;
                    buffer = line.split("=", 2);
                    if (buffer.length >= 2) {
                        String name = buffer[0];
                        buffer = buffer[1].split(":", 2);
                        if (buffer.length >= 2) {
                            Class type = getTypeByName(buffer[0]);
                            String param = buffer[1];
                            Object value = type.equals(String.class) ? param : type.getMethod("valueOf", String.class).invoke(null, param);
                            entryMap.put(name, new Entry(type, value));
                        }
                    }
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return entryMap;
        }

        public String combine(String[] array, int pos, int length) {
            String ret = "";
            for(int i = pos; i < pos + length; i++) {
                ret += array[i];
            }
            return ret;
        }

        public void save(Map<String, Entry> entryMap) {
            try {
                if(!file.delete() && !file.createNewFile()) {
                    throw new Exception("Unable to save config file!");
                }
                FileWriter fw = new FileWriter(file);
                fw.write("//Last update: " + new Date().toString() + "\n");
                for (String name : entryMap.keySet()) {
                    Entry entry = entryMap.get(name);
                    String type = getNameByType(entry.typeParameterClass);
                    fw.write(name + "=" + type + ":" + entry.value + "\n");
                }
                fw.flush();
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Class getTypeByName(String name) {
        if (name.equals("S")) {
            return String.class;
        } else if (name.equals("I")) {
            return Integer.class;
        } else if (name.equals("F")) {
            return Float.class;
        }
        return null;
    }

    public static String getNameByType(Class type) {
        String[] array = type.getName().split("\\.");
        if (array[array.length - 1].equals("String")) {
            return "S";
        } else if (array[array.length - 1].equals("Integer")) {
            return "I";
        } else if (array[array.length - 1].equals("Float")) {
            return "F";
        }
        return null;
    }
}
