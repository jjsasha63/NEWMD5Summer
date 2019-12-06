package com.svirin;//Ім'я пакету (бібліотеки), до якого відноситься клас
//Підключення потрібних пакетів (бібліотек)
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
//Клас, що загружає файл розмітки вікна та відкриває це вікно
public class ProcessingWin {
    //Вікно з результатами роботи програми
    private Stage processStage;
    //Контейнер, що зберігатиме всі елементи інтерфейсу
    private AnchorPane ap;


    //Конструктор класу, що загружає файл розмітки .fxml. В конструкторі може
    //бути виключення - програма може не знайти файл розмітки, тож ключові слова
    //throws Exception попереджують про те, що при створенні класу слід
    //перехопити та обробити виключення.
    public ProcessingWin() throws Exception{
        //Завантажити файл розмітки можна за допомогою методу load класу FXMLLoader
        //Відкриваємо файл методом getResource
        ap = FXMLLoader.load(getClass().getResource("views/sample.fxml"));
        //Створюємо постанову (об'єкт класу Scene), передаючи в аргумент контейнер з
        //усіма елементами інтерфейсу
        Scene sc = new Scene(ap);
        //Створюємо сцену, на яку ставимо постанову методом setScene
        processStage = new Stage();
        processStage.setScene(sc);
    }
    //Метод, що повертає вікно з результатами роботи програми
    public Stage getProcessStage(){
        return processStage;
    }
}
