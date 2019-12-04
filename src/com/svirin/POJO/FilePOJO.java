package com.svirin.POJO;

import com.svirin.States;
import javafx.scene.shape.Circle;
//Клас, поля якого відображатимуться в таблиці
public class FilePOJO {
    private Circle state = States.UNPROCESSED.getState(); //Стан файла
    private String name; //Ім'я файла
    private String hash = ""; //Хеш-сума файла
    //Конструктор, що приймає ім'я файла в якості аргумента
    public FilePOJO(String nameArg){
        name = nameArg;
    }
    //Методи для встановлення нового та отримання поточного значення private членів
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
