package bigdeli.reza.androidorm.model;

import bigdeli.reza.androidorm.orm.BaseEntity;

/**
 * An step is a phase of a tutorial
 */
public class Step implements BaseEntity {

    private long id;
    private int orderNumber;
    private String imageUrl;
    private String instruction;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int order) {
        this.orderNumber = order;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}
