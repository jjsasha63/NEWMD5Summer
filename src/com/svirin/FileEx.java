package com.svirin;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class FileEx{
    private File file;
    private ImageView icon;
    private String name;

    FileEx(File fileArg){
        this.file = fileArg;
        name = fileArg.getName();
        setIcon();
    }

    FileEx(File fileArg, String name){
        this.file = fileArg;
        this.name = name;
        setIcon();
    }

    private void setIcon(){
        Icon iconSwing = FileSystemView.getFileSystemView().getSystemIcon(file);
        ImageIcon imageIcon = (ImageIcon) iconSwing;
        java.awt.Image awtImage = imageIcon.getImage();
        BufferedImage bi;
        bi = new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bi.createGraphics();
        graphics.drawImage(awtImage, 0, 0, null);
        graphics.dispose();
        javafx.scene.image.Image fxImage = SwingFXUtils.toFXImage(bi, null);
        icon = new ImageView(fxImage);
    }

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
