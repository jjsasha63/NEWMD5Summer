package com.svirin;

import com.svirin.POJO.FilePOJO;
import com.svirin.States;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;

public class ProcessingWin {
    private Stage processStage;
    private AnchorPane ap;



    public ProcessingWin() throws Exception{
        ap = FXMLLoader.load(getClass().getResource("views/sample.fxml"));
        Scene sc = new Scene(ap);
        processStage = new Stage();
        processStage.setScene(sc);
        processStage.setResizable(true);
    }

    public Stage getProcessStage(){
        return processStage;
    }
}
