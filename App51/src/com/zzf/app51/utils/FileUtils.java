package com.zzf.app51.utils;

import java.io.File;

import android.os.Environment;

public class FileUtils {
	public File getFile(){
		boolean sdCardExist=Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		String fodlerPath="";
		if(sdCardExist){
			fodlerPath=Environment.getExternalStorageDirectory()+File.separator+"caches";
			
		}
		File cache=new File(fodlerPath);
		if(!cache.exists())
			cache.mkdirs();
		return cache;
	}
}
