package com.svirin;//директива package вказує на назву пакету (бібліотеки), до якої відноситься даний клас
//імпорт (підключення) пакетів (бібліотек), що містять потрібні елементи інтерфейсу та класи
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.File;
//Клас FileSelector будує вікно вибору файлів, хеш-суми яких потрібно підрахувати
public class FileSelector {
    //масив файлів, що містяться в директорії
    private ObservableList<FileEx> filesInDirL;
    //масив файлів, що було вибрано для підрахунку хеш-сум
    public static ObservableList<FileEx> selectedFilesL = FXCollections.observableArrayList();
    //об'єкт класу File, що позначає кореневу директорію, з якої вибиратимуться файли
    public static File rootDirectory;
    //Об'єкт класу File, що позначає наступну директорію, з якої вибиратимуться файли
    private File nextDir;
    //Вікно програми, за правилом театру - сцена
    private Stage selectorStage = new Stage();
    //Кількість вибраних файлів
    private int items = 0;
    //Рядок, який відображатиметься на екрані та показуватиме користувачу кількість обраних файлів
    private String itemsStr = "Number of items: ";
    //Кнопка, за допомогою якої можна буде вибрати всі файли з кореневої директорії та внутрішніх директорій
    private Button selectAll = new Button("Select all");
    //Кнопка, за допомогою якої можно очистити список вибраних файлів
    private Button clearList = new Button("Clear list");
    //Кнопка, за допомогою якої додається вибраний файл до списку
    private Button add = new Button("Add");
    //Кнопка, що дозволяє додати всі файли с обраної директорії без відкриття цієї директорії
    private Button addRec = new Button("Add recursively");
    //Кнопка, за допомогою якої відбувається перехід до наступного вікна - вікна підрахунку хеш-сум
    private Button ok = new Button("Ok");
    //Кнопка скасування, що переносить на початкове вікно
    private Button cancel = new Button("Cancel");
    //Елемент інтерфейсу - список, в якому знаходяться файли та папки обраної директорії.
    //Клас ListView є шаблонним, в трикутних дужках вказується тип елементів, що зберігає список.
    //В даному випадку списки зберігають об'єкти класу FileEx, який описано в однойменному файлі.
    private ListView<FileEx> filesInDir;
    //Список, який зберігає вибрані для підрахунку хеш-сум файли
    private ListView<FileEx> selectedFiles;
    //Лейбл, що виводить на екран напис "Folders:" та позначає список з файлами\папками обраної директорії
    private Label folders = new Label("Folders:");
    //Лейбл, що виводить на екран напис "Number of items: n", де n - кількість обраних для підрахунку хеш-сум файлів
    private Label numberOfItems = new Label(itemsStr + items);
    //Контейнер, що працює за принципом таблиці
    private GridPane gp = new GridPane();
    //Контейнер, що дозволяє прив'язати елементи до сторін вікна додатку
    private BorderPane bp = new BorderPane();
    //Контейнер, що працює за принципом таблиці. Зберігає всі кнопки вікна
    private GridPane buttonsGridPane = new GridPane();
    //Контейнер, що послідовно додає елементи. Зберігає всі лейбли вікна
    private FlowPane labelsFlowPane = new FlowPane();
    //Контейнер, що послідовно додає елементи. Зберігає всі списки вікна
    private FlowPane listsFlowPane = new FlowPane();
    //Конструктор класу. В якості аргументу приймає об'єкт класу File, що позначає кореневу папку
    public FileSelector(File directoryArg){
        rootDirectory = directoryArg;//
        nextDir = rootDirectory;
        filesInDirL = FXCollections.observableArrayList();
        for(File x : rootDirectory.listFiles())
            filesInDirL.add(new FileEx(x));
    }

    public void buildingWindow(){
        //adding labels "Folders:" and "Number of items"
        folders.setStyle("-fx-text-fill: #b9adb9");
        numberOfItems.setStyle("-fx-text-fill: #b9adb9");
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
        selectAll.setStyle("-fx-background-color: #585858; -fx-text-fill: #b9adb9");
        clearList.setMinWidth(150);
        clearList.setStyle("-fx-background-color: #585858; -fx-text-fill: #b9adb9");
        add.setMinWidth(150);
        add.setStyle("-fx-background-color: #585858; -fx-text-fill: #b9adb9");
        addRec.setMinWidth(150);
        addRec.setStyle("-fx-background-color: #585858; -fx-text-fill: #b9adb9");
        ok.setMinWidth(100);
        ok.setStyle("-fx-background-color: #585858; -fx-text-fill: #b9adb9");
        cancel.setMinWidth(100);
        cancel.setStyle("-fx-background-color: #585858; -fx-text-fill: #b9adb9");
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
                    if (x != null && !containsFile(selectedFilesL, x)
                            && !x.getFile().isDirectory()) {
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

        Rectangle background = new Rectangle(620, 420, Color.web("#424242"));
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
            else {
                selectedFilesL.add(new FileEx(x));
                items++;
            }
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
