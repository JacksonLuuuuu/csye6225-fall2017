package com.csye6225.demo.entities;

import javax.persistence.*;

/**
 * Amitha_Murali, 001643826, murali.a@husky.neu.edu
 * Jyoti Sharma, 001643410, sharma.j@husky.neu.edu
 * Surabhi Patil, 001251860, patil.sur@husky.neu.edu
 **/

@Entity // This tells Hibernate to make a table out of this class
public class FileAttachment {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    private String path;

    private int taskId;

    private int userId;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


}
