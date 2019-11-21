package com.svirin;

import com.svirin.controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class FileSelector {
    private ObservableList<FileEx> filesInDirL;
    public static ObservableList<FileEx> selectedFilesL = FXCollections.observableArrayList();
    private File rootDirectory;
    private File nextDir;
    private Stage selectorStage = new Stage();
    private int items = 0;
    private String itemsStr = "Number of items: ";
    //Interface elements of the window
    private Button selectAll = new Button("Select all");
    private Button clearList = new Button("Clear list");
    private Button add = new Button("Add");
    private Button addRec = new Button("Add recursively");
    private Button ok = new Button("Ok");
    private Button cancel = new Button("Cancel");
    private ListView<FileEx> filesInDir;
    private ListView<FileEx> selectedFiles;
    private Label folders = new Label("Folders:");
    private Label numberOfItems = new Label(itemsStr + items);
    private GridPane gp = new GridPane();
    private BorderPane bp = new BorderPane();
    private GridPane buttonsGridPane = new GridPane();
    private FlowPane labelsFlowPane = new FlowPane();
    private FlowPane listsFlowPane = new FlowPane();



    public FileSelector(File directoryArg){
        rootDirectory = directoryArg;
        nextDir = rootDirectory;
        filesInDirL = FXCollections.observableArrayList();
        for(File x : rootDirectory.listFiles())
            filesInDirL.add(new FileEx(x));
    }

    public void buildingWindow(){
        //adding labels "Folders:" and "Number of items"
        labelsFlowPane.getChildren().addAll(folders, numberOfItems);
        labelsFlowPane.setHgap(200);
        labelsFlowPane.setPadding(new Insets(5, 0, 0, 100));
        bp.setTop(labelsFlowPane);

        //adding ListViews
        filesInDir = new ListView<>(filesInDirL);
        filesInDir.setMaxHeight(250);
        filesInDir.setMaxWidth(200);
        filesInDir.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        filesInDir.setCellFactory(new Callback<ListView<FileEx>, ListCell<FileEx>>() {
            @Override
            public ListCell<FileEx> call(ListView<FileEx> param) {
                return new FileCell();
            }
        });
        filesInDir.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() != 2)
                    return;
                else if(!filesInDir.getSelectionModel().getSelectedItem().getFile().isDirectory()) {
                    add.fire();
                    return;
                }
                nextDir = filesInDir.getSelectionModel().getSelectedItem().getFile();
                setFilesObList();
                filesInDir.refresh();
                selectedFiles.refresh();
            }
        });
        selectedFiles = new ListView<>(selectedFilesL);
        selectedFiles.setMaxHeight(250);
        selectedFiles.setMaxWidth(200);
        selectedFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedFiles.setCellFactory(new Callback<ListView<FileEx>, ListCell<FileEx>>() {
            @Override
            public ListCell<FileEx> call(ListView<FileEx> param) {
                return new FileCell();
            }
        });
        listsFlowPane.getChildren().addAll(filesInDir, selectedFiles);
        listsFlowPane.setPadding(new Insets(0, 0, 0, 90));
        listsFlowPane.setHgap(10);
        bp.setCenter(listsFlowPane);

        //adding buttons
        selectAll.setMinWidth(150);
        clearList.setMinWidth(150);
        add.setMinWidth(150);
        addRec.setMinWidth(150);
        ok.setMinWidth(100);
        cancel.setMinWidth(100);
        selectAll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                filesInDir.getSelectionModel().selectAll();
            }
        });
        clearList.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedFilesL.remove(0, selectedFilesL.size());
                items = selectedFilesL.size();
                numberOfItems.setText(itemsStr + items);
                selectedFiles.refresh();
                filesInDir.refresh();
            }
        });
        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<FileEx> buffer = filesInDir.getSelectionModel().getSelectedItems();
                for(FileEx x : buffer) {
                    if (x != null && !containsFile(selectedFilesL, x)) {
                        selectedFilesL.add(x);
                        items++;
                    }
                    else if(x.getFile().isDirectory()) addAll(x.getFile());
                }
                numberOfItems.setText(itemsStr + items);
                filesInDir.refresh();
                selectedFiles.refresh();
            }
        });
        addRec.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileEx buffer = filesInDir.getSelectionModel().getSelectedItem();
                if(!buffer.getFile().isDirectory()) return;
                File[] files = buffer.getFile().listFiles();
                for(File x : files) {
                    FileEx extendedFile = new FileEx(x);
                    if(x.isDirectory()) continue;
                    if(x != null && !containsFile(selectedFilesL, extendedFile)){
                        selectedFilesL.add(extendedFile);
                        items++;
                    }
                }
                numberOfItems.setText(itemsStr + items);
            }
        });
        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    ProcessingWin outWin = new ProcessingWin();
                    Main.processing = outWin.getProcessStage();
                    Main.fileSelection.close();
                    Main.processing.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedFilesL.remove(0, selectedFilesL.size());
                selectorStage.close();
                Main.mainWindow.show();
            }
        });
        buttonsGridPane.add(selectAll, 0, 0);
        buttonsGridPane.add(clearList, 0, 1);
        buttonsGridPane.add(add, 1, 0);
        buttonsGridPane.add(addRec, 1, 1);
        buttonsGridPane.add(ok, 2, 1);
        buttonsGridPane.add(cancel, 3, 1);
        buttonsGridPane.setHgap(10);
        buttonsGridPane.setVgap(10);
        buttonsGridPane.setPadding(new Insets(10, 0, 0, 10));
        gp.add(buttonsGridPane, 0, 0);
        bp.setBottom(gp);

        Rectangle background = new Rectangle(620, 420, new Color(0.64, 0.63, 0.61, 0.65));
        Group g = new Group();
        g.getChildren().add(background);
        g.getChildren().add(bp);
        Scene scene = new Scene(g, 600, 400);
        selectorStage.setScene(scene);
        selectorStage.setTitle("Create list of files to sum");
        selectorStage.setResizable(false);
    }

    private void setFilesObList(){
        filesInDirL.remove(0, filesInDirL.size());
        if(!nextDir.getAbsolutePath().equals(rootDirectory.getAbsolutePath()))
            filesInDirL.add(new FileEx(getPrevDir(nextDir), ".."));
        for(File x : nextDir.listFiles())
            filesInDirL.add(new FileEx(x));
    }

    private File getPrevDir(File dir){
        String buf = dir.getAbsolutePath();
        int lastInd = 0;
        for(int i = buf.length() - 1; i >= 0; i--){
            if(buf.charAt(i) == '\\'){
                lastInd = i;
                break;
            }
        }
        File ret = new File(buf.substring(0, lastInd));
        return ret;
    }

    private boolean containsFile(ObservableList<FileEx> list, FileEx file){
        for(FileEx x : list)
            if(x.getName().equals(file.getName())) return true;
        return false;
    }

    private void addAll(File dir){
        File[] files = dir.listFiles();
        for(File x : files){
            if(x.isDirectory()) addAll(x);
            else selectedFilesL.add(new FileEx(x));
        }
    }

    public Stage getSelectorStage(){
        return selectorStage;
    }
}

class FileCell extends ListCell<FileEx>{
    @Override
    public void updateItem(FileEx name, boolean empty){
        super.updateItem(name, empty);
        if(empty){
            setText(null);
            setGraphic(null);
        }
        else{
            setText(name.getName());
            setGraphic(name.getIcon());
        }
    }
}
