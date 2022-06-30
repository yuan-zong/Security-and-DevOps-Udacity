package com.example.demo;

import java.lang.reflect.Field;

/**
 * This class is created according to the instructions found in video
 * https://learn.udacity.com/nanodegrees/nd035/parts/cd0629/lessons/20a8cdf0-d88c-42c9-bc1e-b1559a9e80da/concepts/42ab6175-983a-490d-be61-42cc2a7b658f
 */
public class TestUtils {

    public static void injectObjects(Object target, String fieldName, Object toInject){
        boolean wasPrivate = false;

        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            if(!f.isAccessible()){
                f.setAccessible(true);
                wasPrivate  = true;
            }
            f.set(target, toInject);

            if(wasPrivate){
                f.setAccessible(false);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
