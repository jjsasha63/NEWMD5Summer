package com.svirin.controller;

import com.svirin.*;
import com.svirin.POJO.FilePOJO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Controller{
    private ObservableList<FilePOJO> files = FXCollections.observableArrayList();
    @FXML
    private TableView<FilePOJO> tableFiles;
    @FXML
    private TableColumn<FilePOJO, Circle> stateColumn;
    @FXML
    private TableColumn<FilePOJO, String> idColumn;
    @FXML
    private TableColumn<FilePOJO, String> hashColumn;
    @FXML
    private Button SaveButton;
    @FXML
    private Button CancelButton;
    @FXML
    private ProgressBar ProcessingBar;

    @FXML
    private void initialize(){
        initData();

        stateColumn.setCellValueFactory(new PropertyValueFactory<FilePOJO, Circle>("state"));
        idColumn.setCellValueFactory(new PropertyValueFactory<FilePOJO, String>("name"));
        hashColumn.setCellValueFactory(new PropertyValueFactory<FilePOJO, String>("hash"));

        tableFiles.setItems(files);
        controllingWindow();
    }

    private void initData(){
        for(FileEx x : FileSelector.selectedFilesL)
            files.add(new FilePOJO(x.getName()));
    }

    private void controllingWindow(){
        SaveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    writingHash();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        CancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                files.remove(0, files.size());
                Main.processing.close();
                Main.mainWindow.show();
            }
        });
        calculateHash();
    }

    private void calculateHash(){
        for(int i = 0; i < files.size(); ++i) {
            files.get(i).setState(States.PROCESSING.getState());
            try {
                files.get(i).setHash(Main.md5(FileSelector.selectedFilesL.get(i).getFile().getAbsolutePath()));
            } catch (NoSuchAlgorithmException e) {
                files.get(i).setState(States.ERROR.getState());
                continue;
            } catch (IOException e) {
                files.get(i).setState(States.ERROR.getState());
                continue;
            }
            files.get(i).setState(States.OK.getState());
            tableFiles.refresh();
            ProcessingBar.setProgress(((double)i + 1.0) / (double)files.size());
        }
    }

    private void writingHash() throws IOException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MD5 files (*.md5)", "*.md5"));
        File file = fc.showSaveDialog(Main.processing);
        if(file == null) return;
        FileWriter writer = new FileWriter(file);
        writer.write("#Genereted " + new Date().toString() + "\n");
        for(int i = 0; i < files.size(); ++i) {
            writer.write(files.get(i).getHash() + " " + "*" + files.get(i).getName() + "\n");
        }
        writer.close();
    }
}
