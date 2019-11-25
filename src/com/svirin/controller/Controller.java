package com.svirin.controller;

import com.sun.deploy.security.SelectableSecurityManager;
import com.svirin.*;
import com.svirin.POJO.FilePOJO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Controller{
    //false - fileselector, true - data
    public static boolean key = false;
    private ObservableList<FilePOJO> files = FXCollections.observableArrayList();
    private ObservableList<FilePOJO> empty = FXCollections.observableArrayList();
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
    private TextField NameLine;
    @FXML
    private TextField PathLine;
    @FXML
    private TextField SizeLine;

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
        if(!key)
            for(FileEx x : FileSelector.selectedFilesL)
                files.add(new FilePOJO(x.getName()));
        else
            for(int i = 0; i < Data.names.size(); ++i)
                files.add(new FilePOJO(Data.names.get(i)));

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
                files.clear();
                Data.names.clear();
                Data.state.clear();
                Data.hashes.clear();
                tableFiles.refresh();
                Main.processing.close();
                Main.mainWindow.show();
            }
        });
        if(!key)
            calculateHash();
        else{
            for(int i = 0; i < files.size(); ++i){
                if(Data.state.get(i))
                    files.get(i).setState(States.OK.getState());
                else
                    files.get(i).setState(States.ERROR.getState());
                files.get(i).setHash(Data.hashes.get(i));
            }
        }
    }

    private void calculateHash(){
        for(int i = 0; i < files.size(); ++i) {
            files.get(i).setState(States.PROCESSING.getState());
            NameLine.setText(FileSelector.selectedFilesL.get(i).getName());
            PathLine.setText(FileSelector.selectedFilesL.get(i).getFile().getAbsolutePath());
            SizeLine.setText("" + FileSelector.selectedFilesL.get(i).getFile().length() + " bytes");
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
        writer.write("#Genereted " + new Date().toString() + "\n\n\n");
        for(int i = 0; i < files.size(); ++i) {
            writer.write(files.get(i).getHash() + " " + "*" +
                    FileSelector.selectedFilesL.get(i).getFile().
                            getAbsolutePath().replace(FileSelector.rootDirectory.getAbsolutePath(), "") + "\n");
        }
        writer.close();
    }
}
