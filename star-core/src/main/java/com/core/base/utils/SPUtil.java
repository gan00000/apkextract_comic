package com.core.base.utils;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
/**
 * 文件存储
 *
 */
public class SPUtil {
	
	//=======================================String型============================================
	/**
	 * 存储String类型数据
	 * @param ctx
	 * @param key
	 * @param info
	 */
	public static void saveSimpleInfo(Context ctx,String fileName,String key,String info){
		Map<String, String> map = new HashMap<String, String>();
		map.put(key, info);
		save(ctx, fileName , map);
	}
	/**
	 * 获取String类型数据
	 * @param context
	 * @param fileName
	 * @param key
	 * @return String
	 */
	public static String getSimpleString(Context context,String fileName,String key){
		SharedPreferences shared =  context.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
		return shared.getString(key,"");
	}
	
	//=======================================Boolean型============================================
	/**
	 * 存储Boolean类型数据
	 * @param ctx
	 * @param key
	 * @param info
	 */
	public static void saveSimpleInfo(Context ctx,String fileName,String key,boolean info){
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		map.put(key, info);
		save(ctx, fileName , map);
	}
	/**
	 * 获取Boolean类型数据
	 * @param context
	 * @param fileName
	 * @param key
	 * @return Boolean
	 */
	public static boolean getSimpleBoolean(Context context,String fileName,String key){
		SharedPreferences shared =  context.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
		return shared.getBoolean(key,false);
	}
	//=======================================Long型============================================
	/**
	 * 存储Long类型数据
	 * @param ctx
	 * @param key
	 * @param info
	 */
	public static void saveSimpleInfo(Context ctx,String fileName,String key,long info){
		Map<String, Long> map = new HashMap<String, Long>();
		map.put(key, info);
		save(ctx, fileName , map);
	}
	/**
	 * 获取Long类型数据
	 * @param context
	 * @param fileName
	 * @param key
	 * @return Long
	 */
	public static long getSimpleLong(Context context,String fileName,String key){
		SharedPreferences shared =  context.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
		return shared.getLong(key,0);
	}
	//=======================================字符型============================================
	/**
	 * 存储Double类型数据
	 * @param ctx
	 * @param key
	 * @param info
	 */
	public static void saveSimpleInfo(Context ctx,String fileName,String key,double info){
		Map<String, String> map = new HashMap<String, String>();
		map.put(key, String.valueOf(info));
		save(ctx, fileName , map);
	}
	/**
	 * 获取Double类型数据
	 * @param context
	 * @param fileName
	 * @param key
	 * @return Double
	 */
	public static double getSimpleDouble(Context context,String fileName,String key){
		SharedPreferences shared =  context.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
		return Double.valueOf(shared.getString(key, "0"));
	}
	
	//=======================================整型============================================
	/**
	 * 存储Integer类型数据
	 * @param ctx
	 * @param key
	 * @param info
	 */
	public static void saveSimpleInteger(Context ctx,String fileName,String key,int info){
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put(key, info);
		save(ctx, fileName , map);
	}
	/**
	 * 获取Integer类型数据
	 * @param context
	 * @param fileName
	 * @param key
	 * @return Integer
	 */
	public static int getSimpleInteger(Context context,String fileName,String key){
		SharedPreferences shared =  context.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
		return shared.getInt(key,0);
	}
	//=======================================字符型============================================
	
	/**
	 * 存储Character对象数据
	 * @param ctx
	 * @param key
	 * @param info
	 */
	public static void saveSimpleInfo(Context ctx,String fileName,String key,char info){
		Map<String, String> map = new HashMap<String, String>();
		map.put(key, String.valueOf(info));
		save(ctx, fileName , map);
	}

	public static char getSimpleCharacter(Context context,String fileName,String key){
		SharedPreferences shared =  context.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
		return shared.getString(key,"").charAt(0);
	}

	//=======================================Float型============================================
	public static void saveSimpleInfo(Context ctx,String fileName,String key,Float info){
		Map<String, Float> map = new HashMap<String, Float>();
		map.put(key, info);
		save(ctx, fileName , map);
	}

	public static float getSimpleFloat(Context ctx,String fileName,String key){
		SharedPreferences shared =  ctx.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
		return shared.getFloat(key, 0);
	}


	//=======================================存储对象============================================
	
    /**
     * 获得SharedPreferences
     */
    private static SharedPreferences getSharedPreferences(Context context, String fileName){
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }
    /**
     * 获得Editor
     */
	private static SharedPreferences.Editor getEditor(Context context, String fileName){
        return getSharedPreferences(context, fileName).edit();
    }
    /**
     * 保存数据
     */
    private static void save(Context context, String fileName, Map<String, ?> map){
		SharedPreferences.Editor editor = getEditor(context, fileName);
        for (Map.Entry<String, ?> entry : map.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Integer){
                editor.putInt(key, (Integer) value);
            }
            else if (value instanceof Long){
                editor.putLong(key, (Long) value);
            }
            else if (value instanceof Boolean){
                editor.putBoolean(key, (Boolean) value);
            }
            else if (value instanceof Float){
                editor.putFloat(key, (Float) value);
            }
            else{
                editor.putString(key, (String) value);
            }
        }
        // 提交
        editor.commit();
    }
}
