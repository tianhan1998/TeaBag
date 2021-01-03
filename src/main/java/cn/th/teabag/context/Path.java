package cn.th.teabag.context;

import java.io.File;

public class Path {

    public static String BEATMAPSETS_COVER_PATH;
    public static String BEATMAPS_BG_PATH;
    public static String DEVICE_JSON_PATH;
    public static String RESOURCE_BASE_PATH;
    static{
        String os=System.getProperty("os.name");
        if (os != null && os.toLowerCase().startsWith("windows")) {
            RESOURCE_BASE_PATH =System.getProperty("user.dir")+ File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator;
        } else if (os != null && os.toLowerCase().startsWith("linux")) {
            RESOURCE_BASE_PATH = "/usr/local/teabag/";
        }
        BEATMAPS_BG_PATH = RESOURCE_BASE_PATH +"beatmapsBG";
        BEATMAPSETS_COVER_PATH= RESOURCE_BASE_PATH +"beatmapsCover";
        DEVICE_JSON_PATH = RESOURCE_BASE_PATH +"device.json";
    }
}
