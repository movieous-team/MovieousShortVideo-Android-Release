package com.movieous.media.api.vendor.stsdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import com.movieous.media.R;
import com.movieous.media.api.vendor.stsdk.view.FilterItem;
import com.movieous.media.api.vendor.stsdk.view.ObjectItem;
import com.movieous.media.api.vendor.stsdk.view.StickerItem;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sensetime on 16-11-16.
 */

public class FileUtils {
    public static final String MODEL_NAME_ACTION= "M_SenseME_Action_5.5.1.model";
    public static final String MODEL_NAME_FACE_ATTRIBUTE = "M_SenseME_Attribute_1.0.1.model";
    public static final String MODEL_NAME_EYEBALL_CENTER = "M_Eyeball_Center.model";
    public static final String MODEL_NAME_EYEBALL_CONTOUR = "M_SenseME_Iris_1.11.1.model";
    public static final String MODEL_NAME_FACE_EXTRA = "M_SenseME_Face_Extra_5.6.0.model";

    public static final String MODEL_NAME_BODY_FOURTEEN = "M_SenseME_Body_Fourteen_1.2.0.model";
    public static final String MODEL_NAME_HAND = "M_SenseME_Hand_5.0.0.model";
    public static final String MODEL_NAME_AVATAR_CORE = "M_SenseME_Avatar_Core_1.0.0.model";

    public static String getActionModelName(){
        return MODEL_NAME_ACTION;
    }

    public static String getEyeBallCenterModelName(){
        return MODEL_NAME_EYEBALL_CENTER;
    }

    public static String getEyeBallContourModelName(){
        return MODEL_NAME_EYEBALL_CONTOUR;
    }

    public static String getFaceExtraModelName(){
        return MODEL_NAME_FACE_EXTRA;
    }

    public static ArrayList<String> copyStickerFiles(Context context) {
        String files[] = null;
        ArrayList<String> zipfiles = new ArrayList<String>();

        try {
            files = context.getAssets().list("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath();
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if(str.indexOf(".zip") != -1){
                copyFileIfNeed(context, str);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为zip结尾
                if (filename.trim().toLowerCase().endsWith(".zip")) {
                    zipfiles.add(filename);
                }
            }
        }

        return zipfiles;
    }

    public static boolean copyFileIfNeed(Context context, String fileName) {
        String path = getFilePath(context, fileName);
        if (path != null) {
            File file = new File(path);
            if (!file.exists()) {
                //如果模型文件不存在
                try {
                    if (file.exists())
                        file.delete();

                    file.createNewFile();
                    InputStream in = context.getApplicationContext().getAssets().open(fileName);
                    if(in == null)
                    {
                        LogUtils.e("copyMode", "the src is not existed");
                        return false;
                    }
                    OutputStream out = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int n;
                    while ((n = in.read(buffer)) > 0) {
                        out.write(buffer, 0, n);
                    }
                    in.close();
                    out.close();
                } catch (IOException e) {
                    file.delete();
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean copyFileIfNeed(Context context, String fileName, String className) {
        String path = getFilePath(context, className + File.separator + fileName);
        if (path != null) {
            File file = new File(path);
            if (!file.exists()) {
                //如果模型文件不存在
                try {
                    if (file.exists())
                        file.delete();

                    file.createNewFile();

                    InputStream in = context.getAssets().open(className + File.separator + fileName);
                    if(in == null)
                    {
                        LogUtils.e("copyMode", "the src is not existed");
                        return false;
                    }
                    OutputStream out = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int n;
                    while ((n = in.read(buffer)) > 0) {
                        out.write(buffer, 0, n);
                    }
                    in.close();
                    out.close();
                } catch (IOException e) {
                    file.delete();
                    return false;
                }
            }
        }
        return true;
    }

    public static String getFilePath(Context context, String fileName) {
        String path = null;
        File dataDir = context.getApplicationContext().getExternalFilesDir(null);
        if (dataDir != null) {
            path = dataDir.getAbsolutePath() + File.separator + fileName;
        }
        return path;
    }

    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                LogUtils.e("FileUtil", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public static void copyModelFiles(Context context) {
        //action模型会从asset直接读取
        //copyFileIfNeed(context, MODEL_NAME_ACTION);
        copyFileIfNeed(context, MODEL_NAME_FACE_ATTRIBUTE);
        copyFileIfNeed(context, MODEL_NAME_AVATAR_CORE);
    }


    public static String getTrackModelPath(Context context) {
        return getFilePath(context, MODEL_NAME_ACTION);

    }

    public static String getFaceAttributeModelPath(Context context) {
        return getFilePath(context, MODEL_NAME_FACE_ATTRIBUTE);
    }

    public static String getAvatarCoreModelPath(Context context){
        return getFilePath(context, MODEL_NAME_AVATAR_CORE);
    }

    public static List<ObjectItem> getObjectList(){
        List<ObjectItem> objectList = new ArrayList<>();

//        objectList.add(new ObjectItem("close", R.drawable.close_object));
//        objectList.add(new ObjectItem("null", R.drawable.none));
        objectList.add(new ObjectItem("object_hi", R.drawable.object_hi));
        objectList.add(new ObjectItem("object_happy", R.drawable.object_happy));
        objectList.add(new ObjectItem("object_star", R.drawable.object_star));

        objectList.add(new ObjectItem("object_sticker", R.drawable.object_sticker));
        objectList.add(new ObjectItem("object_love", R.drawable.object_love));
        objectList.add(new ObjectItem("object_sun", R.drawable.object_sun));


        return objectList;
    }

    public static void copyStickerFiles(Context context, String index){
        copyStickerZipFiles(context, index);
        copyStickerIconFiles(context, index);
    }

    public static void copyFilterFiles(Context context, String index){
        copyFilterModelFiles(context, index);
        copyFilterIconFiles(context, index);
    }

    public static ArrayList<StickerItem> getStickerFiles(Context context, String index){
        ArrayList<StickerItem> stickerFiles = new ArrayList<StickerItem>();
        //Bitmap iconClose = BitmapFactory.decodeResource(context.getResources(), R.drawable.close_sticker);
        Bitmap iconNone = BitmapFactory.decodeResource(context.getResources(), R.drawable.none);

        List<String> stickerModels = getStickerZipFilesFromSd(context, index);
        Map<String, Bitmap> stickerIcons = getStickerIconFilesFromSd(context, index);
        List<String> stickerNames = getStickerNames(context, index);

        for(int i = 0;i< stickerModels.size(); i++){
            if(stickerIcons.get(stickerNames.get(i)) != null)
                stickerFiles.add(new StickerItem(stickerNames.get(i), stickerIcons.get(stickerNames.get(i)), stickerModels.get(i)));
            else{
                stickerFiles.add(new StickerItem(stickerNames.get(i), iconNone, stickerModels.get(i)));
            }
        }

        return  stickerFiles;
    }

    public static List<String> copyStickerZipFiles(Context context, String className){
        String files[] = null;
        ArrayList<String> modelFiles = new ArrayList<String>();

        try {
            files = context.getAssets().list(className);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if(!folder.exists()){
                folder.mkdir();
            }
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if(str.indexOf(".zip") != -1 || str.indexOf(".model") != -1){
                copyFileIfNeed(context, str, className);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        if(subFile == null || subFile.length == 0){
            return modelFiles;
        }

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为model结尾
                if (filename.trim().toLowerCase().endsWith(".zip")|| filename.trim().toLowerCase().endsWith(".model")) {
                    modelFiles.add(filename);
                }
            }
        }

        return modelFiles;
    }

    public static List<String> getStickerZipFilesFromSd(Context context, String className){
        ArrayList<String> modelFiles = new ArrayList<String>();

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if(!folder.exists()){
                folder.mkdir();
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        if(subFile == null || subFile.length == 0){
            return modelFiles;
        }

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为model结尾
                if (filename.trim().toLowerCase().endsWith(".zip") || filename.trim().toLowerCase().endsWith(".model")) {
                    modelFiles.add(filename);
                }
            }
        }

        return modelFiles;
    }

    public static Map<String, Bitmap> copyStickerIconFiles(Context context, String className){
        String files[] = null;
        TreeMap<String, Bitmap> iconFiles = new TreeMap<String, Bitmap>();

        try {
            files = context.getAssets().list(className);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if(!folder.exists()){
                folder.mkdir();
            }
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if(str.indexOf(".png") != -1){
                copyFileIfNeed(context, str, className);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        if(subFile == null || subFile.length == 0){
            return iconFiles;
        }

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为png结尾
                if (filename.trim().toLowerCase().endsWith(".png") && filename.indexOf("mode_") == -1) {
                    String name = subFile[i].getName();
                    iconFiles.put(getFileNameNoEx(name), BitmapFactory.decodeFile(filename));
                }
            }
        }

        return iconFiles;
    }

    public static Map<String, Bitmap> getStickerIconFilesFromSd(Context context, String className){
        TreeMap<String, Bitmap> iconFiles = new TreeMap<String, Bitmap>();

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if(!folder.exists()){
                folder.mkdir();
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        if(subFile == null || subFile.length == 0){
            return iconFiles;
        }

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为png结尾
                if (filename.trim().toLowerCase().endsWith(".png") && filename.indexOf("mode_") == -1) {
                    String name = subFile[i].getName();
                    iconFiles.put(getFileNameNoEx(name), BitmapFactory.decodeFile(filename));
                }
            }
        }

        return iconFiles;
    }

    public static List<String> getStickerNames(Context context, String className){
        ArrayList<String> modelNames = new ArrayList<String>();
        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;
            File folder = new File(folderpath);

            if(!folder.exists()){
                folder.mkdir();
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        if(subFile == null || subFile.length == 0){
            return modelNames;
        }

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                // 判断是否为model结尾
                if (filename.trim().toLowerCase().endsWith(".zip") || filename.trim().toLowerCase().endsWith(".model")) {
                    String name = subFile[i].getName();
                    modelNames.add(getFileNameNoEx(name));
                }
            }
        }

        return modelNames;
    }

    public static ArrayList<FilterItem> getFilterFiles(Context context, String index){
        ArrayList<FilterItem> filterFiles = new ArrayList<FilterItem>();
        Bitmap iconNature = BitmapFactory.decodeResource(context.getResources(), R.drawable.mode_original);

        if(index.equals("filter_portrait")){
            iconNature = BitmapFactory.decodeResource(context.getResources(), R.drawable.filter_portrait_nature);
        }else if(index.equals("filter_scenery")){
            iconNature = BitmapFactory.decodeResource(context.getResources(), R.drawable.filter_scenery_nature);
        }else if(index.equals("filter_still_life")){
            iconNature = BitmapFactory.decodeResource(context.getResources(), R.drawable.filter_still_life_nature);
        }else if(index.equals("filter_food")){
            iconNature = BitmapFactory.decodeResource(context.getResources(), R.drawable.filter_food_nature);
        }
        filterFiles.add(new FilterItem("original", iconNature, null));
        //filterFiles.add(new FilterItem("null", iconNature, null));

        List<String> filterModels = copyFilterModelFiles(context, index);
        Map<String, Bitmap> filterIcons = copyFilterIconFiles(context, index);
        List<String> filterNames = getFilterNames(context, index);

        if(filterModels == null || filterModels.size() == 0){
            return filterFiles;
        }

        for(int i = 0;i< filterModels.size(); i++){
            if(filterIcons.get(filterNames.get(i)) != null)
                filterFiles.add(new FilterItem(filterNames.get(i), filterIcons.get(filterNames.get(i)), filterModels.get(i)));
            else{
                filterFiles.add(new FilterItem(filterNames.get(i), iconNature, filterModels.get(i)));
            }
        }

        return  filterFiles;
    }

    public static List<String> copyFilterModelFiles(Context context, String index){
        String files[] = null;
        ArrayList<String> modelFiles = new ArrayList<String>();

        try {
            files = context.getAssets().list(index);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + index;

            File folder = new File(folderpath);

            if(!folder.exists()){
                folder.mkdir();
            }
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if(str.indexOf(".model") != -1){
                copyFileIfNeed(context, str, index);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        if(subFile == null || subFile.length == 0){
            return modelFiles;
        }

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为model结尾
                if (filename.trim().toLowerCase().endsWith(".model") && filename.indexOf("filter") != -1) {
                    modelFiles.add(filename);
                }
            }
        }

        return modelFiles;
    }

    public static Map<String, Bitmap> copyFilterIconFiles(Context context, String index){
        String files[] = null;
        TreeMap<String, Bitmap> iconFiles = new TreeMap<String, Bitmap>();

        try {
            files = context.getAssets().list(index);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + index;

            File folder = new File(folderpath);

            if(!folder.exists()){
                folder.mkdir();
            }
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if(str.indexOf(".png") != -1){
                copyFileIfNeed(context, str, index);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        if(subFile == null || subFile.length == 0){
            return iconFiles;
        }

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为png结尾
                if (filename.trim().toLowerCase().endsWith(".png") && filename.indexOf("filter") != -1) {
                    String name = subFile[i].getName().substring(13);
                    iconFiles.put(getFileNameNoEx(name), BitmapFactory.decodeFile(filename));
                }
            }
        }

        return iconFiles;
    }

    public static List<String> getFilterNames(Context context, String index){
        ArrayList<String> modelNames = new ArrayList<String>();
        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + index;
            File folder = new File(folderpath);

            if(!folder.exists()){
                folder.mkdir();
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        if(subFile == null || subFile.length == 0){
            return modelNames;
        }

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                // 判断是否为model结尾
                if (filename.trim().toLowerCase().endsWith(".model") && filename.indexOf("filter") != -1) {
                    String name = subFile[i].getName().substring(13);
                    modelNames.add(getFileNameNoEx(name));
                }
            }
        }

        return modelNames;
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

}
