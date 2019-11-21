package com.svirin.POJO;

import com.svirin.States;
import javafx.scene.shape.Circle;

public class FilePOJO {
    private Circle state = States.UNPROCESSED.getState();
    private String name;
    private String hash = "";

    public FilePOJO(String nameArg){
        name = nameArg;
    }

    public FilePOJO(){}

    public Circle getState(){
        return state;
    }
    public void setState(Circle stateArg){
        state = stateArg;
    }

    public String getName(){
        return name;
    }
    public void setName(String nameArg){
        name = nameArg;
    }

    public String getHash() {
        return hash;
    }
    public void setHash(String hashArg) {
        hash = hashArg;
    }
}
