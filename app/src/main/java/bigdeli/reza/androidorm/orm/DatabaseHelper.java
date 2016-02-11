package bigdeli.reza.androidorm.orm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * generic database helper which does the simple CRUD
 */
public class DatabaseHelper {

    // data objects
    private HashMap<String, Integer> columnReadCount;

    // helper objects
    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        database = DatabaseOpenHelper.getInstance(context).getWritableDatabase();
        columnReadCount = new HashMap<>();
    }

    public <T extends BaseEntity> ArrayList<T> get(Class<T> tClazz) {
        ArrayList<T> ts = null;

        // get the table name
        String tableName = DatabaseNameManager.getTableName(tClazz);

        // select * from tableName
        Cursor cursor = database.rawQuery("select * from " + tableName, null);

        try {
            // map the ts with cursor
            if (cursor.moveToFirst()) {
                ts = new ArrayList<T>();
                do {
                    // pass the cursor to map
                    T t = map(tClazz, cursor);
                    ts.add(t);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.isClosed();
                }
            }
        }

        return ts;
    }

    public <T extends BaseEntity> T get(Class<T> tClazz, long id) {
        T t = null;

        // get the table name and id column
        String tableName = DatabaseNameManager.getTableName(tClazz);
        String idColumn = DatabaseNameManager.getIdColumn(tClazz);

        // select * from tableName where ID = id
        Cursor cursor = database.rawQuery("select * from " + tableName + " where " + idColumn + " = " + id, null);

        try {
            // map the t with cursor
            if (cursor.moveToFirst()) {
                // pass the cursor to map
                t = map(tClazz, cursor);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.isClosed();
                }
            }
        }

        return t;
    }

    public <T extends BaseEntity> void save(ArrayList<T> ts) {
        for (T t : ts) {
            save(t);
        }
    }

    public <T extends BaseEntity> long save(T t) {

        // get the table name
        String tableName = DatabaseNameManager.getTableName(t.getClass());

        // get content values
        ContentValues contentValues = extract(t);

        // insert the values to the table
        return database.insert(tableName, null, contentValues);
    }

    public <T extends BaseEntity> void set(ArrayList<T> ts) {
        for (T t : ts) {
            set(t);
        }
    }

    public <T extends BaseEntity> long set(T t) {

        // get the table name
        String tableName = DatabaseNameManager.getTableName(t.getClass());

        // get content values
        ContentValues contentValues = extract(t);

        // update the values
        return database.update(tableName, contentValues, DatabaseNameManager.getIdColumn(t.getClass()) + " = " + t.getId(), null);
    }

    public <T extends BaseEntity> void remove(ArrayList<T> ts) {
        for (T t : ts) {
            remove(t);
        }
    }

    public <T extends BaseEntity> long remove(T t) {

        // get the table name and id column
        String tableName = DatabaseNameManager.getTableName(t.getClass());

        // delete the row
        return database.delete(tableName, DatabaseNameManager.getIdColumn(t.getClass()) + " = " + t.getId(), null);
    }

    public <T extends BaseEntity, S extends BaseEntity> ArrayList<T> getRelated(Class<T> tClazz, Class<S> sClazz, long relatedId) {
        ArrayList<T> ts = null;

        // get the table name and related table name
        String tableName = DatabaseNameManager.getTableName(tClazz);
        String relatedTableName = DatabaseNameManager.getTableName(sClazz);

        // select * from tableName where relatedTableNameId = relatedId
        Cursor cursor = database.rawQuery("select * from " + tableName + " where " + DatabaseNameManager.getRelatedColumn(sClazz) + " = " + relatedId, null);

        try {
            // map the ts with cursor
            if (cursor.moveToFirst()) {
                ts = new ArrayList<T>();
                do {
                    // pass the cursor to map
                    T t = map(tClazz, cursor);
                    ts.add(t);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.isClosed();
                }
            }
        }

        return ts;
    }

    public <T extends BaseEntity, S extends BaseEntity> ArrayList<T> getWhitCondition(Class<T> tClazz, Comparison comparison) {
        ArrayList<T> ts = null;

        // get the table name
        String tableName = DatabaseNameManager.getTableName(tClazz);

        // select * from tableName
        Cursor cursor = database.rawQuery("select * from " + tableName, null);

        try {
            // map the ts with cursor
            if (cursor.moveToFirst()) {
                ts = new ArrayList<T>();
                do {
                    // pass the cursor to map
                    T t = map(tClazz, cursor);

                    // check if the comparison meets condition
                    if (comparison != null && comparison.where(t)) {
                        ts.add(t);
                    }
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.isClosed();
                }
            }
        }

        return ts;
    }

    private <T extends BaseEntity> T map(Class<T> tClazz, Cursor cursor) throws IllegalAccessException, InstantiationException {
        T t = tClazz.newInstance();

        for (Method method : tClazz.getMethods()) {
            if (isSetter(method)) {

                String columnName = DatabaseNameManager.getColumnName(method);

                try {
                    // check if it is Parameterize Type or not
                    Type genericParameterType = method.getGenericParameterTypes()[0];
                    if (isParameterized(genericParameterType)) {
                        // it is Parameterize
                        ParameterizedType parameterizedType = (ParameterizedType) genericParameterType;
                        Class parameterArgClass = (Class<?>) (parameterizedType.getActualTypeArguments()[0]);

                        // check if the generic type is a subclass of BaseEntity
                        if (isAssignableFrom(BaseEntity.class, parameterArgClass)) {
                            // it is a subclass of BaseEntity

                            // avoid circular reference
                            int lastIteration = (columnReadCount.get(tClazz.getSimpleName()) != null ? columnReadCount.get(tClazz.getSimpleName()) : 0);
                            columnReadCount.put(tClazz.getSimpleName(), ++lastIteration);
                            if (columnReadCount.get(tClazz.getSimpleName()) < 2) {
                                method.invoke(t, get(parameterArgClass.asSubclass(BaseEntity.class)));
                            }
                        } else {
                            // it is not a subclass of BaseEntity
                            StringBuilder stringBuilder;
                            switch (parameterArgClass.getSimpleName()) {
                                case "short":
                                    ArrayList<Short> shorts = new ArrayList<>();
                                    stringBuilder = new StringBuilder();
                                    for (char c : cursor.getString(cursor.getColumnIndex(columnName)).toCharArray()) {
                                        if (c != ',') {
                                            stringBuilder.append(c);
                                        } else {
                                            shorts.add(Short.parseShort(stringBuilder.toString()));
                                            stringBuilder = new StringBuilder();
                                        }
                                    }
                                    shorts.add(Short.parseShort(stringBuilder.toString()));
                                    method.invoke(t, shorts);
                                    break;
                                case "int":
                                    ArrayList<Integer> integers = new ArrayList<>();
                                    stringBuilder = new StringBuilder();
                                    for (char c : cursor.getString(cursor.getColumnIndex(columnName)).toCharArray()) {
                                        if (c != ',') {
                                            stringBuilder.append(c);
                                        } else {
                                            integers.add(Integer.parseInt(stringBuilder.toString()));
                                            stringBuilder = new StringBuilder();
                                        }
                                    }
                                    integers.add(Integer.parseInt(stringBuilder.toString()));
                                    method.invoke(t, integers);
                                    break;
                                case "long":
                                    ArrayList<Long> longs = new ArrayList<>();
                                    stringBuilder = new StringBuilder();
                                    for (char c : cursor.getString(cursor.getColumnIndex(columnName)).toCharArray()) {
                                        if (c != ',') {
                                            stringBuilder.append(c);
                                        } else {
                                            longs.add(Long.parseLong(stringBuilder.toString()));
                                            stringBuilder = new StringBuilder();
                                        }
                                    }
                                    longs.add(Long.parseLong(stringBuilder.toString()));
                                    method.invoke(t, longs);
                                    break;
                                case "float":
                                    ArrayList<Float> floats = new ArrayList<>();
                                    stringBuilder = new StringBuilder();
                                    for (char c : cursor.getString(cursor.getColumnIndex(columnName)).toCharArray()) {
                                        if (c != ',') {
                                            stringBuilder.append(c);
                                        } else {
                                            floats.add(Float.parseFloat(stringBuilder.toString()));
                                            stringBuilder = new StringBuilder();
                                        }
                                    }
                                    floats.add(Float.parseFloat(stringBuilder.toString()));
                                    method.invoke(t, floats);
                                    break;
                                case "double":
                                    ArrayList<Double> doubles = new ArrayList<>();
                                    stringBuilder = new StringBuilder();
                                    for (char c : cursor.getString(cursor.getColumnIndex(columnName)).toCharArray()) {
                                        if (c != ',') {
                                            stringBuilder.append(c);
                                        } else {
                                            doubles.add(Double.parseDouble(stringBuilder.toString()));
                                            stringBuilder = new StringBuilder();
                                        }
                                    }
                                    doubles.add(Double.parseDouble(stringBuilder.toString()));
                                    method.invoke(t, doubles);
                                    break;
                                case "boolean":
                                    ArrayList<Boolean> booleans = new ArrayList<>();
                                    stringBuilder = new StringBuilder();
                                    for (char c : cursor.getString(cursor.getColumnIndex(columnName)).toCharArray()) {
                                        if (c != ',') {
                                            stringBuilder.append(c);
                                        } else {
                                            booleans.add(Boolean.parseBoolean(stringBuilder.toString()));
                                            stringBuilder = new StringBuilder();
                                        }
                                    }
                                    booleans.add(Boolean.parseBoolean(stringBuilder.toString()));
                                    method.invoke(t, booleans);
                                    break;
                                case "String":
                                    ArrayList<String> strings = new ArrayList<>();
                                    stringBuilder = new StringBuilder();
                                    for (char c : cursor.getString(cursor.getColumnIndex(columnName)).toCharArray()) {
                                        if (c != ',') {
                                            stringBuilder.append(c);
                                        } else {
                                            strings.add(stringBuilder.toString());
                                            stringBuilder = new StringBuilder();
                                        }
                                    }
                                    strings.add(stringBuilder.toString());
                                    method.invoke(t, strings);
                                    break;
                            }
                        }
                    } else {
                        // it is NOT Parameterize
                        Class<?> parameterType = method.getParameterTypes()[0];

                        if (isAssignableFrom(BaseEntity.class, parameterType)) {
                            // it is a subclass of BaseEntity

                            // avoid circular reference
                            int lastIteration = (columnReadCount.get(tClazz.getSimpleName()) != null ? columnReadCount.get(tClazz.getSimpleName()) : 0);
                            columnReadCount.put(tClazz.getSimpleName(), ++lastIteration);
                            if (columnReadCount.get(tClazz.getSimpleName()) < 2) {
                                method.invoke(t, get(parameterType.asSubclass(BaseEntity.class), cursor.getLong(cursor.getColumnIndex(columnName + DatabaseNameManager.getIdColumn(t.getClass())))));
                            }
                        } else {
                            // it is not a subclass of BaseEntity
                            switch (parameterType.getSimpleName()) {
                                case "short":
                                    method.invoke(t, cursor.getShort(cursor.getColumnIndex(columnName)));
                                    break;
                                case "int":
                                    method.invoke(t, cursor.getInt(cursor.getColumnIndex(columnName)));
                                    break;
                                case "long":
                                    method.invoke(t, cursor.getLong(cursor.getColumnIndex(columnName)));
                                    break;
                                case "float":
                                    method.invoke(t, cursor.getFloat(cursor.getColumnIndex(columnName)));
                                    break;
                                case "double":
                                    method.invoke(t, cursor.getDouble(cursor.getColumnIndex(columnName)));
                                    break;
                                case "boolean":
                                    method.invoke(t, cursor.getString(cursor.getColumnIndex(columnName)).equals("false") ? false : true);
                                    break;
                                case "String":
                                    method.invoke(t, cursor.getString(cursor.getColumnIndex(columnName)));
                                    break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        // refresh counting
        columnReadCount.put(tClazz.getSimpleName(), 0);

        return t;
    }

    private <T extends BaseEntity> ContentValues extract(T t) {
        ContentValues contentValues = new ContentValues();

        for (Method method : t.getClass().getMethods()) {
            if (isGetter(method)) {
                String columnName = DatabaseNameManager.getColumnName(method);

                try {
                    // check if it is Parameterized Type or not
                    Type genericParameterType = method.getGenericReturnType();
                    if (isParameterized(genericParameterType)) {
                        // it is Parameterized
                        ParameterizedType parameterizedType = (ParameterizedType) genericParameterType;
                        Class parameterArgClass = (Class<?>) (parameterizedType.getActualTypeArguments()[0]);

                        // check if the generic type is a subclass of BaseEntity
                        if (isAssignableFrom(BaseEntity.class, parameterArgClass)) {
                            // it is a subclass of BaseEntity

                            // avoid circular reference
                            int lastIteration = (columnReadCount.get(t.getClass().getSimpleName()) != null ? columnReadCount.get(t.getClass().getSimpleName()) : 0);
                            columnReadCount.put(t.getClass().getSimpleName(), ++lastIteration);
                            if (columnReadCount.get(t.getClass().getSimpleName()) < 2) {
                                // check to see if the relation is one-to-many or many-to-many
                                if (doesFirstClassHaveMoreThanOneInstanceOfSecondClass(parameterArgClass, t.getClass())) {
                                    // relation is many-to-many
                                    saveManyToMany((ArrayList<BaseEntity>) method.invoke(t), t.getClass(), t.getId());
                                } else {
                                    // relation is one-to-many
                                    saveOneToMany((ArrayList<BaseEntity>) method.invoke(t), DatabaseNameManager.getColumnName(t.getClass()), t.getId());
                                }
                            }

                        } else {
                            // it is not a subclass of BaseEntity

                            StringBuilder stringBuilder = new StringBuilder();
                            for (Object object : (List<?>) method.invoke(t)) {
                                stringBuilder.append(object.toString()).append(",");
                            }
                            contentValues.put(columnName, stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1));
                        }

                    } else {
                        // it is NOT Parameterized
                        if (isAssignableFrom(BaseEntity.class, (Class<?>) genericParameterType)) {
                            // it is a subclass of BaseEntity
                            contentValues.put(columnName + DatabaseNameManager.getIdColumn(t.getClass()), String.valueOf(((BaseEntity) (method.invoke(t))).getId()));
                        } else {
                            // it is not a subclass of BaseEntity nor an enum
                            contentValues.put(columnName, String.valueOf(method.invoke(t)) != null ? String.valueOf(method.invoke(t)) : "");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // refresh counting
        columnReadCount.put(t.getClass().getSimpleName(), 0);

        return contentValues;
    }

    private <T extends BaseEntity, S extends BaseEntity> void saveManyToMany(ArrayList<T> ts, Class<S> sClazz, long sId) {
        // not supported yet
    }

    private <T extends BaseEntity> void saveOneToMany(ArrayList<T> ts, String idColumnName, long id) {
        for (T t : ts) {
            // get the table name
            String tableName = DatabaseNameManager.getTableName(t.getClass());

            // get content values
            ContentValues contentValues = extract(t);
            contentValues.put(idColumnName, String.valueOf(id));

            // insert the values to the table
            database.insert(tableName, null, contentValues);
        }
    }

    private <T extends BaseEntity, S extends BaseEntity> boolean doesFirstClassHaveMoreThanOneInstanceOfSecondClass(Class<T> tClazz, Class<S> sClazz) {
        for (Method method : tClazz.getMethods()) {
            if (isGetter(method)) {
                try {
                    // check if it is Parameterize Type or not
                    Type genericParameterType = method.getGenericReturnType();
                    if (genericParameterType instanceof ParameterizedType) {
                        // it is Parameterize
                        ParameterizedType parameterizedType = (ParameterizedType) genericParameterType;
                        Class parameterArgClass = (Class<?>) (parameterizedType.getActualTypeArguments()[0]);
                        if (sClazz.isAssignableFrom((Class<?>) parameterArgClass)) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private boolean isGetter(Method method) {
        if (!method.getName().startsWith("get")) {
            return false;
        }
        if (method.getParameterTypes().length != 0) {
            return false;
        }
        if (void.class.equals(method.getReturnType())) {
            return false;
        }
        if (!Modifier.isPublic(method.getModifiers())) {
            return false;
        }
        return true;
    }

    private boolean isSetter(Method method) {
        if (!method.getName().startsWith("set")) {
            return false;
        }
        if (method.getParameterTypes().length != 1) {
            return false;
        }
        if (!Modifier.isPublic(method.getModifiers())) {
            return false;
        }
        return true;
    }

    private boolean isParameterized(Type type) {
        return type instanceof ParameterizedType;
    }

    private boolean isAssignableFrom(Class<?> clazz, Class<?> parameterArgClass) {
        return (clazz.isAssignableFrom(parameterArgClass));
    }

    private boolean isEnum(Class<?> clazz) {
        return clazz.isEnum();
    }

    public interface Comparison {
        public <T extends BaseEntity> boolean where(T t);
    }

}