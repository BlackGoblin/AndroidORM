package bigdeli.reza.androidorm.orm;

/**
 * Base entity. All other entities should extend this class
 */
public interface BaseEntity {

    long getId();

    void setId(long id);
}
