package com.auction.common.model;

import java.time.LocalDateTime;
import java.util.UUID;
import java.io.Serializable;

public abstract class Entity implements Serializable{
    private String id;
    private LocalDateTime createAt;
    public Entity(String id,LocalDateTime creatAt){
        this.id= UUID.randomUUID().toString();
        this.createAt=createAt;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public LocalDateTime getCreateAt() {
        return createAt;
    }
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
