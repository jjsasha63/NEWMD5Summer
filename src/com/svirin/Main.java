package com.svirin;

import com.svirin.controller.Controller;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main extends Application {
    public static Stage mainWindow;
    public static Stage fileSelection;
    public static Stage processing;
    String gdirectory = new String();
    File dir;

    public static String md5(String filename)
            throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(Paths.get(filename)));
        byte[] digest = md.digest();
        String myChecksum = DatatypeConverter
                .printHexBinary(digest).toUpperCase();
        return myChecksum;
    }
    public byte[] createSha1(File file) throws Exception  {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        InputStream fis = new FileInputStream(file);
        int n = 0;
        byte[] buffer = new byte[8192];
        while (n != -1) {
            n = fis.read(buffer);
            if (n > 0) {
                digest.update(buffer, 0, n);
            }
        }
        return digest.digest();
    }

    //Creating the main window
    private BorderPane initDirectorySelection(Stage primaryStage){
        //Building the window
        BorderPane pane = new BorderPane();
        pane.setMinWidth(300);
        pane.setMinHeight(300);
        GridPane grid = new GridPane();
        VBox folderPane = new VBox();

        Label sumTypeL = new Label("Checksum type:");
        ObservableList<String> sumTypeA = FXCollections.observableArrayList("MD5", "SHA1");
        ComboBox<String> sumTypeC = new ComboBox<>(sumTypeA);
        sumTypeC.setValue("MD5");

        Button createSum = new Button("Create sums");
        Button verify = new Button(" Verify sums ");
        Button about = new Button("About");

        DirectoryChooser dc = new DirectoryChooser();
        FileChooser fc = new FileChooser();
        Label enterDir = new Label("Please, enter the path to the root folder:");
        Label selectDir = new Label("or select the root folder:");
        TextField directoryT = new TextField();
        TextArea anotherDirectory = new TextArea();
        directoryT.setMaxSize(170, 70);
        Button selectDirB = new Button("Select Folder");

        //opens directory-chooser and then searching all files in selected directory
        selectDirB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                 dir = dc.showDialog(primaryStage);
                if(dir == null) return;
                directoryT.setText(dir.getAbsolutePath());
                //gdirectory = directoryT.getText();
            }
        });
        createSum.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File dir = new File(directoryT.getText());
                directoryT.setText("");
                FileSelector fs = new FileSelector(dir);
                fs.buildingWindow();
                //closing the main window and opening
                //the file-chooser window
                mainWindow.close();
                fileSelection = fs.getSelectorStage();
                fileSelection.show();
                Controller.key = false;
            }
        });

        verify.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MD5 files (*.md5)", "*.md5"));
                File dir1 = fc.showOpenDialog(primaryStage);
                if(dir1 == null) return;
                anotherDirectory.setText(dir1.getAbsolutePath());
                String md5directory = anotherDirectory.getText();
                Data data = new Data(dir, md5directory, dir.getAbsolutePath());
                try {
                    data.transform();
                    //data.md5file();
                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                try {
                    ProcessingWin outWin = new ProcessingWin();
                    processing = outWin.getProcessStage();
                    processing.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        folderPane.getChildren().addAll(directoryT, selectDir, selectDirB);
        folderPane.setPadding(new Insets(0, 20, 0, 80));
        folderPane.setSpacing(30);


        grid.add(sumTypeL, 1, 0);
        grid.add(sumTypeC, 2, 0);
        grid.add(createSum, 0, 1);
        grid.add(verify, 1, 1);
        grid.add(about, 2, 1);
        grid.setStyle("-fx-border-color: gray");
        grid.setHgap(5); //horizontal gap in pixels
        grid.setVgap(5); //vertical gap in pixels
        grid.setPadding(new Insets(5, 5, 5, 5));

        pane.setBottom(grid);
        pane.setTop(enterDir);
        pane.setCenter(folderPane);
        return pane;
    }

    @Override
    public void start(Stage primaryStage){
        Group g = new Group();
        g.getChildren().add(initDirectorySelection(primaryStage));
        Scene scene = new Scene(g, 300, 300);

        mainWindow = primaryStage;
        mainWindow.setScene(scene);
        mainWindow.setTitle("MD5Summer");
        mainWindow.setResizable(false);
        mainWindow.show();
        //TODO: delete this code after testing the file-choser window
        //File dir = new File("C:/YOU_DIED");
        //FileSelector fs = new FileSelector(dir);
        //fs.buildingWindow();
        //fileSelection = fs.getSelectorStage();
        //fileSelection.show();
        //ProcessingWin pw = new ProcessingWin();
        //processing = pw.getProcessStage();
        //processing.show();
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
	    Application.launch(args);
    }
}
