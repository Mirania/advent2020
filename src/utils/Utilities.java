package utils;

import java.util.List;

public class Utilities {

    public static String join(Object[] objects, String separator) {
        var joined = "";
        for (var i = 0; i < objects.length; i++)
            joined += objects[i].toString() + (i < objects.length - 1 ? separator : "");
        return joined;
    }

    public static String join(List<?> objects, String separator) {
        return join(objects.toArray(), separator);
    }

}
