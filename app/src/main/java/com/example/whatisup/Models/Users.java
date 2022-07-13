package com.example.whatisup.Models;

public class Users {


   String name;
   String email;
   String password;
   String profilePic;
   String id;
   String lastMessage;
   String about;
   String status;
   Long timestamp;

   public Long getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(Long timestamp) {
      this.timestamp = timestamp;
   }

   public Users(String name, String email, String password, String profilePic, String id, String lastMessage,Long timestamp) {
      this.name = name;
      this.email = email;
      this.password = password;
      this.profilePic=profilePic;
      this.id=id;
      this.lastMessage=lastMessage;
      this.timestamp=timestamp;
   }



   //Empty Constructor for Firebase
   public Users(){}

   //Constructor for login/signup details
   public Users(String name, String email, String password) {
      this.name = name;
      this.email = email;
      this.password = password;
   }


   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getProfilePic() {
      return profilePic;
   }

   public void setProfilePic(String profilePic) {
      this.profilePic = profilePic;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

   public void getId(String key) {
   }

   public String getAbout() {
      return about;
   }

   public void setAbout(String about) {
      this.about = about;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }
}
