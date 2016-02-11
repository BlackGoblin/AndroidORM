package bigdeli.reza.androidorm.model;

import java.util.ArrayList;

import bigdeli.reza.androidorm.orm.BaseEntity;

/**
 * User is a person who creates tutorials.
 */
public class User implements BaseEntity {

    private long id;
    private String name;
    private String familyName;
    private String email;
    private String userName;
    private String phoneNumber;
    private boolean verified;
    private ArrayList<User> friends;
    private ArrayList<User> blocks;
    private ArrayList<Tutorial> tutorials;
    private ArrayList<Tutorial> favoriteTutorials;
    private AccountType accountType;

    public enum AccountType {
        PLAIN, SIMPLE
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public ArrayList<User> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
    }

    public ArrayList<User> getBlocks() {
        return blocks;
    }

    public void setBlocks(ArrayList<User> blocks) {
        this.blocks = blocks;
    }

    public ArrayList<Tutorial> getTutorials() {
        return tutorials;
    }

    public void setTutorials(ArrayList<Tutorial> tutorials) {
        this.tutorials = tutorials;
    }

    public ArrayList<Tutorial> getFavoriteTutorials() {
        return favoriteTutorials;
    }

    public void setFavoriteTutorials(ArrayList<Tutorial> favoriteTutorials) {
        this.favoriteTutorials = favoriteTutorials;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

}
