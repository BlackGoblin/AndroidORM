package bigdeli.reza.androidorm.orm;

import java.lang.reflect.Method;

import ir.beautyShare.app.model.BaseEntity;

/**
 * Database Utilities helps with naming the tables and columns
 */
public final class DatabaseNameManager {

    public static <T extends BaseEntity> String getTableName(Class<T> tClazz) {
        return tClazz.getSimpleName();
    }

    public static <T extends BaseEntity> String getIdColumn(Class<T> tClazz) {
        return "Id";
    }

    public static <T extends BaseEntity> String getRelatedColumn(Class<T> tClazz) {
        return getTableName(tClazz) + getIdColumn(tClazz);
    }

    public static String getColumnName(Method method) {
        return method.getName().substring(3, method.getName().length());
    }

    public static <T extends BaseEntity> String getColumnName(Class<T> tClazz) {
        return tClazz.getSimpleName() + getIdColumn(tClazz);
    }
}
