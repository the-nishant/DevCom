package com.example.chat;

public class Contacts
{
    private String Username, Status, ProfileImage ;

    public Contacts()
    {

    }

    public Contacts(String username, String status, String profileImage)
    {
        Username = username;
        Status = status;
        ProfileImage = profileImage;
    }

    public String getUsername()
    {
        return Username;
    }

    public void setUsername(String username)
    {
        Username = username;
    }

    public String getStatus()
    {
        return Status;
    }

    public void setStatus(String status)
    {
        Status = status;
    }

    public String getProfileImage()
    {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage)
    {
        ProfileImage = profileImage;
    }
}
