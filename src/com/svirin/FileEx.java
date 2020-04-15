package com.svirin;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
//Клас, що є розширеним класом File. Додатково містить системну іконку файла.
public class FileEx{
    private File file; //Файл
    private ImageView icon; //Системна іконка
    private String name; //"Нікнейм" файлу
    //Конструктор, що приймає в якості аргументу файл
    FileEx(File fileArg){
        this.file = fileArg;
        name = fileArg.getName();
        setIcon();
    }
    //Конструктор, що приймає в якості аргументу файл та його "нікнейм"
    FileEx(File fileArg, String name){
        this.file = fileArg;
        this.name = name;
        setIcon();
    }
    //Метод, що встановлює системну іконку файла
    private void setIcon(){
        //Оскільки фреймворк JavaFX не має жодного методу, за допомогою якого можна б було отримати системну іконку файла,
        //скористаємось іншим фреймворком - Swing. Для отримання системної іконки слід викликати метод getSystemIcon класу
        //FileSystemView, в який передається файл. Метод повертає об'єкт класу Icon
        Icon iconSwing = FileSystemView.getFileSystemView().getSystemIcon(file);
        //Явно перетворюємо об'єкт класу Icon в ImageIcon для отримання об'єкту Image
        ImageIcon imageIcon = (ImageIcon) iconSwing;
        java.awt.Image awtImage = imageIcon.getImage();
        //BufferedImage - основний клас фреймворку Swing для нас, який можна перетворити в клас Image з JavaFX
        BufferedImage bi;
        //Створюємо об'єкт класу BufferedImage, що має розміри отриманої раніше іконки
        bi = new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        //Отримаємо об'єкт класу Graphics2D, що є полотном нашого BufferedImage. Все, що буде малюватися на цьому об'єкті,
        //буде відображатися у BufferedImage
        Graphics2D graphics = bi.createGraphics();
        //Малюємо на полотні отриману іконку, починаючи з координат 0, 0
        graphics.drawImage(awtImage, 0, 0, null);
        //Утилізовуємо графічний контекст
        graphics.dispose();
        //За допомогою методу toFXImage класу SwingFXUtils перетворюємо BufferedImage в Image, що належить JavaFX
        javafx.scene.image.Image fxImage = SwingFXUtils.toFXImage(bi, null);
        //Створюємо об'єкт ImageView з об'єкту Image
        icon = new ImageView(fxImage);
    }
    //Методи для отримання значеннь з приватних членів класу
    public ImageView getIcon(){
        return icon;
    }

    public File getFile(){
        return file;
    }

    public String getName(){
        return name;
    }
}
