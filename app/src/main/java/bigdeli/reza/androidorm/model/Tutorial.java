package bigdeli.reza.androidorm.model;

import java.util.ArrayList;

import bigdeli.reza.androidorm.orm.BaseEntity;

/**
 * A tutorial is related to only one category. Its purpose is to teach something related to its category
 */
public class Tutorial implements BaseEntity {

    private long id;
    private String title;
    private String description;
    private String coverImageUrl;
    private String coverVideoUrl;
    private User user;
    private float rate;
    private ArrayList<Step> steps;
    private Category category;
    private ArrayList<String> tags;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getCoverVideoUrl() {
        return coverVideoUrl;
    }

    public void setCoverVideoUrl(String coverVideoUrl) {
        this.coverVideoUrl = coverVideoUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
