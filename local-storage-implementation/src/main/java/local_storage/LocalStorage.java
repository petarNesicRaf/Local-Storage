package local_storage;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalStorage {

    //sve putanje se zadaju relativno u odnosu na root
    private File rootDir;
    private String rootName;
    private String rootPath;

    //config
    private long maxSize;
    private int maxFiles;
    private List<String> extensions;

    private ObjectMapper objectMapper;

    private String fullRootPath;
    private String configPath;

    private Map<String, Integer> numFileMap = new HashMap<String, Integer>();
    private Map<String, String> pathMap = new HashMap<String, String>();
    private Map<String, File> fileMap = new HashMap<String, File>();

    public LocalStorage(String rootPath, String rootName)
    {
        System.out.println("Kreiranje root direktorijuma");
        rootDir = new File(rootPath+"/"+rootName);
        if(rootDir.mkdir() == true)
        {
            System.out.println("Korenski direktorijum uspesno kreiran.");

        }else{
            System.out.println("Korenski direktorijum nije uspesno kreiran.");

        }

        this.rootName = rootName;
        this.rootPath = rootPath;

        this.fullRootPath = rootPath + "/" +rootName;
        this.configPath = fullRootPath + "/config.json";

        this.extensions = new ArrayList<String>();
        this.objectMapper = new ObjectMapper();
        //kreiranje default konfig file-a
        setDefaultConfig();

        numFileMap.put(fullRootPath, maxFiles);

        try {
            objectMapper.writeValue(new File(configPath), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDefaultConfig()
    {
        this.maxSize = 1024;
        this.maxFiles = 5;
        this.getExtensions().add("exe");
    }

    public void rewriteConfig()
    {
        try {
            objectMapper.writeValue(new File(configPath), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LocalStorage readConfig(String str)
    {
        LocalStorage ls = null;
        try {
            ls = objectMapper.readValue(new File(configPath), LocalStorage.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ls;
    }

    public File getRootDir() {
        return rootDir;
    }

    public void setRootDir(File rootDir) {
        this.rootDir = rootDir;
    }

    public String getRootName() {
        return rootName;
    }

    public void setRootName(String rootName) {
        this.rootName = rootName;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public int getMaxFiles() {
        return maxFiles;
    }

    public void setMaxFiles(int maxFiles) {
        this.maxFiles = maxFiles;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getFullRootPath() {
        return fullRootPath;
    }

    public void setFullRootPath(String fullRootPath) {
        this.fullRootPath = fullRootPath;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public Map<String, Integer> getNumFileMap() {
        return numFileMap;
    }

    public void setNumFileMap(Map<String, Integer> numFileMap) {
        this.numFileMap = numFileMap;
    }

    public Map<String, File> getFileMap() {
        return fileMap;
    }

    public Map<String, String> getPathMap() {
        return pathMap;
    }

    public void setPathMap(Map<String, String> pathMap) {
        this.pathMap = pathMap;
    }

    public void setFileMap(Map<String, File> fileMap) {
        this.fileMap = fileMap;
    }
}
