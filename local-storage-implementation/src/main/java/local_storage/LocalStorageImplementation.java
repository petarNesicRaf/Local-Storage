package local_storage;

import org.specification.StorageHandler;
import org.specification.StorageManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalStorageImplementation implements StorageHandler {

    static{
        StorageManager.registerSpecificationInterface(new LocalStorageImplementation());
    }


    private LocalStorage mainLocalStorage;
    private  String rootPath;


    @Override
    public boolean createFile(String name) {
        String ext[] = name.split("\\.");
        String path = rootPath + "/" + name;


        if(checkFileExtensions(ext[1]))
        {
            System.out.println("Throw unsupported extension exception");
            return false;
        }
        if(mainLocalStorage.getNumFileMap().get(rootPath) == 0)
        {
            System.out.println("Throw max num files exception");
            return false;
        }


        File newFile = new File(path);
        if(mainLocalStorage.getNumFileMap().get(rootPath) > 0)
        {
            try {
                newFile.createNewFile();

                int numOfFiles = mainLocalStorage.getNumFileMap().get(rootPath);
                numOfFiles -= 1;
                mainLocalStorage.getNumFileMap().put(rootPath, numOfFiles);
                mainLocalStorage.getFileMap().put(rootPath, newFile);

                mainLocalStorage.rewriteConfig();
                System.out.println("Fajl uspesno kreiran");
                return true;

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }else{
            System.out.println("throw max file exception");
            return false;
        }

    }

    @Override
    public boolean createFile(String path, String... names) {
        String p = mainLocalStorage.getPathMap().get(path);
        File newFile;
        System.out.println("OVDE::: " + p);

        for(String name: names)
        {
            String[] fileNames = name.split("\\.");
            if(!checkFileExtensions(fileNames[1]))
            {
                System.out.println(path);
                if(mainLocalStorage.getNumFileMap().get(p) > 0 )
                {
                    newFile = new File(p + "/" + name);
                    try {
                        newFile.createNewFile();
                        int numOfFiles = mainLocalStorage.getNumFileMap().get(path);
                        numOfFiles-=1;
                        mainLocalStorage.getNumFileMap().put(path, numOfFiles);
                        mainLocalStorage.getFileMap().put(path, newFile);
                        mainLocalStorage.rewriteConfig();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    System.out.println("Throw max num of files exception");
                }
            }else{
                System.out.println("Throw unsupported extension");
            }
        }
        return false;
    }

    @Override
    public boolean initRoot(String path, String name) {

        File file = new File(path + "/" + name);
        if(file.exists())
        {
            System.out.println("Korenski folder vec postoji");
            return false;
        }
        mainLocalStorage = new LocalStorage(path,name);
        this.rootPath = mainLocalStorage.getFullRootPath();
        if(mainLocalStorage.getRootDir().exists())
            return true;
        else return false;
    }

    //@todo cnt reset
    private int nameCnt=1;
    private String defaultDirName = "New Directory ";
    //path, cnt
    private Map<String, Integer> mapDefaultName = new HashMap<String, Integer>();

    @Override
    public boolean createDirectory(String path, String name) {
        if(!mainLocalStorage.getRootDir().exists()) {
            System.out.println("Korenski direktorijum nije inicijalizovan");
            return false;
        }

        path = rootPath + "/" + path;
        File f = new File(path);
        boolean alreadyExists = false;
        File newFile;
        int br1;
        int br2;

        //ako je ime direktorijuma zadato preko patterna
        if(name.contains("{") && name.contains("}"))
        {
            String[] strings = name.split("\\{");
            String interval = strings[1].replaceAll("[^0-9]", " ");
            interval = interval.trim().replaceAll(" +", " ");
            String brojevi[] = interval.split(" ");

            br1= Integer.parseInt(brojevi[0]);
            br2 = Integer.parseInt(brojevi[1]);
            String ime = strings[0];
            for(int i = br1; i<=br2;i++)
            {
                newFile = new File(path + "/" + ime+i);

                mainLocalStorage.getNumFileMap().put(path+"/"+ime+i, mainLocalStorage.getMaxFiles());
                if(newFile.mkdir() == false)
                {
                    System.out.println("Direktorijum ne moze da se kreira.");
                    return false;
                }
            }
            return true;
        }

        alreadyExists = checkDirForNames(f,name);

        //ukoliko postoji dir sa istim imenom dodaj default ime
        if(alreadyExists)
        {
            newFile = new File(path + "/" +defaultDirName +nameCnt);
            if(newFile.mkdir())
            {
                System.out.println("Direktorijum "+ defaultDirName + " uspesno kreiran.");
                mainLocalStorage.getNumFileMap().put(path + "/" +defaultDirName +nameCnt, mainLocalStorage.getMaxFiles());
                nameCnt++;
                return true;
            }
        }else{
            newFile = new File(path + "/" + name);

            if(newFile.mkdir())
            {
                System.out.println("Direktorijum " + name + " uspesno kreiran");
                System.out.println("XXX : " + path + "/" + name);

                mainLocalStorage.getNumFileMap().put(path + "/" + name, mainLocalStorage.getMaxFiles());
                mainLocalStorage.getPathMap().put(name, path);

                return true;
            }
        }

        System.out.println("Direktorijum " + name + " nije kreiran");
        return false;
    }


    @Override
    public boolean createDirectory(String name) {
        if(!mainLocalStorage.getRootDir().exists()) {
            System.out.println("Korenski direktorijum nije inicijalizovan");
            return false;
        }
        boolean exists =false;
        String path = rootPath+ "/" + name;

        exists = checkDirForNames(mainLocalStorage.getRootDir(), name);


        if(exists)
        {
            String dirPath = rootPath + "/"+ defaultDirName+nameCnt;
            File f = new File(dirPath);
            if(f.mkdir())
            {
                System.out.println("Direktorijum uspesno kreiran");
                mainLocalStorage.getNumFileMap().put(dirPath, mainLocalStorage.getMaxFiles());
                nameCnt++;
                return true;
            }
        }else{
            File f = new File(path);
            if(f.mkdir())
            {
                mainLocalStorage.getNumFileMap().put(path, mainLocalStorage.getMaxFiles());
                System.out.println("Direktorijum uspesno kreiran");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean createDirectory() {
        if(!mainLocalStorage.getRootDir().exists()) {
            System.out.println("Korenski direktorijum nije inicijalizovan");
            return false;
        }

        String path = rootPath + "/" + defaultDirName+nameCnt;
        File f = new File(path);

        if(f.mkdir()==false)
        {
            System.out.println("Direktorijum nije uspesno kreiran");
            return false;
        }else{
            mainLocalStorage.getNumFileMap().put(path, mainLocalStorage.getMaxFiles());
            System.out.println("Korenski direktorijum uspesno kreiran");
            return true;
        }
    }

    @Override
    public boolean uploadFile(String s, String s1, String... strings) {
        return false;
    }

    @Override
    public boolean uploadFile(String s, String... strings) {
        return false;
    }

    //brises iz direktorijuma
    //brises iz dire
    @Override
    public boolean delete(String path, String... names) {
        path = rootPath + "/" + path;
        File file = new File(path);
        if(!file.exists() && file.isDirectory())
        {
            System.out.println("Throw directory not found exception");
            return false;
        }
        String fullPath;
        File toBeDeleted;
        for(String name: names)
        {
            fullPath = path + "/" + name;
            toBeDeleted = new File(fullPath);
            if(!toBeDeleted.isDirectory())
            {
                if (toBeDeleted.exists()) {
                    if (toBeDeleted.delete()) {
                        System.out.println("Fajl uspesno obrisan");
                    }
                } else {
                    System.out.println("Throw file not found exception");
                    return false;
                }
            }else{
                toBeDeleted.delete();
                System.out.println("Deleted");
            }
        }
        return true;
    }

    @Override
    public boolean delete(String s) {
        return false;
    }

    @Override
    public boolean move(String path1, String path2, String... names) {
        return true;
    }

    @Override
    public boolean move(String s, String... strings) {
        return false;
    }

    @Override
    public boolean downloadFile(String s, String s1, String... strings) {
        return false;
    }

    @Override
    public boolean downloadFile(String s, String... strings) {
        return false;
    }

    @Override
    public boolean rename(String s, String s1, String s2) {
        return false;
    }

    @Override
    public boolean rename(String s, String s1) {
        return false;
    }

    @Override
    public boolean connect(String s, String s1, String s2) {
        return false;
    }

    //ovo je nepotrebno za sad
    @Override
    public boolean createConfig(long maxSize, int maxFiles, String... extensions) {
        return false;
    }


    @Override
    public boolean createConfig(long l) {
        return false;
    }

    @Override
    public boolean deleteConfig() {
        return false;
    }

    @Override
    public boolean changeConfig(long maxSize, int maxFiles, String... extensions) {
        File[] files = mainLocalStorage.getRootDir().listFiles();
        List<String> list = new ArrayList<String>();
        for(File f : files)
            list.add(f.getName());

        if(!list.contains("config.json"))
        {
            System.out.println("Config fajl ne postoji");
            return false;
        }

        int currentMax = mainLocalStorage.getMaxFiles();
        int numFiles;

        for(Map.Entry<String, Integer> map : mainLocalStorage.getNumFileMap().entrySet())
        {
            numFiles = map.getValue();
            //3 < 10 -> 10-3 = 7
            if(numFiles == currentMax)
            {
                mainLocalStorage.getNumFileMap().put(map.getKey(), maxFiles);
            }else if(numFiles == 0)
            {
                mainLocalStorage.getNumFileMap().put(map.getKey(), maxFiles - currentMax);
            }else if(numFiles >0 && numFiles < 0)
            {
                mainLocalStorage.getNumFileMap().put(map.getKey(), numFiles + (maxFiles-numFiles));
            }else if(numFiles > maxFiles){
                mainLocalStorage.getNumFileMap().put(map.getKey(), 0);
            }
        }

        mainLocalStorage.setMaxFiles(maxFiles);
        mainLocalStorage.setMaxSize(maxSize);

        for(String e : extensions)
        {
            if(!mainLocalStorage.getExtensions().contains(e))
                mainLocalStorage.getExtensions().add(e);
        }

        mainLocalStorage.rewriteConfig();

        return true;
    }

    @Override
    public boolean readConfig() {
        return false;
    }

    private boolean checkDirForNames(File folder, String name)
    {
        File[] files = folder.listFiles();
        for(File f : files)
        {
            if(f.getName().equals(name))
            {
                return true;
            }
        }
        return false;
    }
    private boolean checkFileExtensions(String extension)
    {
        for(String s : mainLocalStorage.getExtensions())
        {
            if(extension.equals(s))
            {
                return true;
            }
        }
        return false;
    }

    public void printHash()
    {
        for(Map.Entry<String, Integer> set :  mainLocalStorage.getNumFileMap().entrySet())
        {
            System.out.println(set.getKey() + " " + set.getValue());
        }
    }

}
