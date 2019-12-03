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
        rootDirectory = directoryArg;//Ініціалізуємо поле, що позначає кореневу папку, аргументом конструктора
        nextDir = rootDirectory;//Ініціалізуємо поле, що позначає наступну директорію
        filesInDirL = FXCollections.observableArrayList();//Створення масиву, що зберігає файли поточного каталогу
        //Ініціалізація масиву, що зберігає файли поточного каталогу. Для цього використовується цикл for each.
        //Загальний запис циклу: for(тип назва_зміної : масив або колекція). На кожній ітерація зміна, що вказана в
        //дужках прийматиме значення наступного елемента масива. Даний цикл зручно використовувати для перебору масиву
        for(File x : rootDirectory.listFiles())
            //Додаємо до масиву об'єкт FileEx, що є об'єктом типу File з розширеним функціоналом, методом add()
            filesInDirL.add(new FileEx(x));
    }
    //Метод, що будує вікно вибору файлів - додаються елементи інтерфейсу та назначаються обробники подій
    public void buildingWindow(){
        //Встановлення кольору фону лейблів, що дорівнює #b9adb9
        folders.setStyle("-fx-text-fill: #b9adb9");
        numberOfItems.setStyle("-fx-text-fill: #b9adb9");
        //Додаємо до контейнеру лейбли методом addAll
        labelsFlowPane.getChildren().addAll(folders, numberOfItems);
        //Встановлення відступу між елементами контейнеру, що дорівнює 200 пікселів
        labelsFlowPane.setHgap(200);
        //Встановлення відступу контейнера від лівого боку вікна в 100 пікселів, та від верху - 5 пікселів
        labelsFlowPane.setPadding(new Insets(5, 0, 0, 100));
        //Закріплюємо контейнер з лейблами у верхній частині вікна
        bp.setTop(labelsFlowPane);

        //Створюємо об'єкт класу ListView, що приймає в якості аргументу масив даних. Таким чином, вміст списку - це
        //вміст переданого масиву. В даному випадку - файли\директорії поточного каталогу
        filesInDir = new ListView<>(filesInDirL);
        //Встановлення максимального розміру списку: висота - 250 пікселів, ширина - 200 пікселів
        filesInDir.setMaxHeight(250);
        filesInDir.setMaxWidth(200);
        //Встановлення режиму вибору - множинний тип вибору, тобто можна вибрати більше однієї позиції. Для досягнення
        //цієї цілі слід отримати модель вибору методом getSelectionModel() і встановити один з режимів вибору (описані
        //в перерахуванні SelectionMode)
        filesInDir.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //Стандартний вигляд елементу списку, що відображає просто рядок не підходить під цілі нашої програми.
        //Метод setCellFactory дозволяє змінити зовнішній вигляд комірки списку. В якості аргументу приймає об'єкт
        //класу, що наслідується від ListCell, тобто будь-яку реалізацію комірки. Інтерфейс Callback потрібний для
        //заміни стандартної комірки. Метод call() буде викликатися при кожньому зверненні до аргументу, що передається
        //в цей метод. call() повертає в аргумент методу setCellFactory нашу реалізацію комірки, яка прописана в класі
        //FileCell
        filesInDir.setCellFactory(new Callback<ListView<FileEx>, ListCell<FileEx>>() {
            @Override
            public ListCell<FileEx> call(ListView<FileEx> param) {
                return new FileCell();
            }
        });
        //Встановлення опрацьовувача події на список, що містить файли поточної директорії. Подія, що опрацьовується -
        //натискання на кнопку миші. Потрібно зробити, щоб при подвійному кліку файл додавався до списку вибраних файлів
        //або відкривалась вибрана директорія
        filesInDir.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //Отримуємо кількість натискань на кнопку миші. Для цього викликаємо метод getClickCount об'єкту класу
                //MouseEvent. Якщо кількість кліків не дорівнює 2, то робота опрацьовувача події завершується
                if(event.getClickCount() != 2)
                    return;
                //Якщо елемент списку, на який було натиснуто - не директорія, а файл, то програмно натискаємо кнопку,
                //що додає обраний файл до списку обраних файлів. Для цього отримуємо модель вибору списку методом
                //getSelectionModel(), з якої отримуємо вибраний елемент (getSelectedItem()). Клас FileEx має метод
                //getFile(), що повертає одне зі свої полів - об'єкт класу File. Перевірити, чи цей об'єкт є директорією
                //можливо викликом методу isDirectory(), що поверне істину, якщо об'єкт - це каталог.
                else if(!filesInDir.getSelectionModel().getSelectedItem().getFile().isDirectory()) {
                    add.fire();//Метод fire() дозволяє програмно натиснути кнопку
                    return;
                }
                //Якщо вибраний об'єкт - директорія, то присвоюємо зміній nextDir вибраний каталог
                nextDir = filesInDir.getSelectionModel().getSelectedItem().getFile();
                //Виклик методу, що змінює вміст списку файлів каталогу. Видаляє поточний вміст та додає до списку
                //файли\директорії вибраного користувачем
                setFilesObList();
                //оновлюємо списки для коректного відображення змісту
                filesInDir.refresh();
                selectedFiles.refresh();
            }
        });
        //Створюємо об'єкт класу ListView, що приймає в якості аргументу масив даних. Таким чином, вміст списку - це
        //вміст переданого масиву. В даному випадку - масив вибраних для підрахунку хеш-сум файлів
        selectedFiles = new ListView<>(selectedFilesL);
        //Встановлення максимального розміру списку: висота - 250 пікселів, ширина - 200 пікселів
        selectedFiles.setMaxHeight(250);
        selectedFiles.setMaxWidth(200);
        //Встановлення режиму вибору - множинний тип вибору, тобто можна вибрати більше однієї позиції. Для досягнення
        //цієї цілі слід отримати модель вибору методом getSelectionModel() і встановити один з режимів вибору (описані
        //в перерахуванні SelectionMode)
        selectedFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //Встановлення реалізації зовнішнього виду комірки, що прописана у класі FileCell
        selectedFiles.setCellFactory(new Callback<ListView<FileEx>, ListCell<FileEx>>() {
            @Override
            public ListCell<FileEx> call(ListView<FileEx> param) {
                return new FileCell();
            }
        });
        //Додаємо до контейнера обидва списки
        listsFlowPane.getChildren().addAll(filesInDir, selectedFiles);
        //Встановлення відступу контейнера від лівого боку вікна в 90 пікселів
        listsFlowPane.setPadding(new Insets(0, 0, 0, 90));
        //Встановлення відступу між елементами контейнеру, що дорівнює 10 пікселів
        listsFlowPane.setHgap(10);
        //Прикріплюємо групу списків до центру вікна
        bp.setCenter(listsFlowPane);

        //Встановлення мінімальної ширини кнопки в 150 пікселів
        selectAll.setMinWidth(150);
        //Встановлення кольору заднього фону, що дорівнює #585858, та кольору тексту #b9adb9
        selectAll.setStyle("-fx-background-color: #585858; -fx-text-fill: #b9adb9");
        //Встановлення мінімальної ширини кнопки в 150 пікселів
        clearList.setMinWidth(150);
        //Встановлення кольору заднього фону, що дорівнює #585858, та кольору тексту #b9adb9
        clearList.setStyle("-fx-background-color: #585858; -fx-text-fill: #b9adb9");
        //Встановлення мінімальної ширини кнопки в 150 пікселів
        add.setMinWidth(150);
        //Встановлення кольору заднього фону, що дорівнює #585858, та кольору тексту #b9adb9
        add.setStyle("-fx-background-color: #585858; -fx-text-fill: #b9adb9");
        //Встановлення мінімальної ширини кнопки в 150 пікселів
        addRec.setMinWidth(150);
        //Встановлення кольору заднього фону, що дорівнює #585858, та кольору тексту #b9adb9
        addRec.setStyle("-fx-background-color: #585858; -fx-text-fill: #b9adb9");
        //Встановлення мінімальної ширини кнопки в 100 пікселів
        ok.setMinWidth(100);
        //Встановлення кольору заднього фону, що дорівнює #585858, та кольору тексту #b9adb9
        ok.setStyle("-fx-background-color: #585858; -fx-text-fill: #b9adb9");
        //Встановлення мінімальної ширини кнопки в 100 пікселів
        cancel.setMinWidth(100);
        //Встановлення кольору заднього фону, що дорівнює #585858, та кольору тексту #b9adb9
        cancel.setStyle("-fx-background-color: #585858; -fx-text-fill: #b9adb9");
        //Встановлення опрацьовувача подій на кнопку, що дозволяє вибрати всі файли каталогу
        selectAll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Для того, щоб вибрати всі елементи зі списку, треба отримати модель вибору методом getSelectionModel(),
                //після чого викликати метод selectAll()
                filesInDir.getSelectionModel().selectAll();
            }
        });
        //Встановлення опрацьовувача подій на кнопку, що очищає список вибраних файлів
        clearList.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Очищаємо масив, вміст якого відображається у списку. Для цього викликаємо метод remove(), що
                //приймає в якості аргументів індекс-початок та індекс-кінець проміжку, що потрібно видалити
                selectedFilesL.remove(0, selectedFilesL.size());
                //Змінній, що зберігає кількість вибраних файлів, присвоюємо значення розміру очищеного масиву, тобто 0
                items = selectedFilesL.size();
                //Оновлюємо лейбл, що відображає на екрані кількість вибраних файлів
                numberOfItems.setText(itemsStr + items);
                //оновлюємо списки для коректного відображення змісту
                selectedFiles.refresh();
                filesInDir.refresh();
            }
        });
        //Встановлення опрацьовувача подій на кнопку, що додає до масиву обраних файлів, нові файли
        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //При множинному режимі вибору, для отримання всіх вибраних елементів слід викликати метод
                //getSelectedItems() моделі вибору (для отримання якої викликається getSelectionModel()).
                //getSelectedItems() повертає масив ObservableList виділених елементів. В даній ситуації
                //створюється буфер, що містить виділені елементи списку.
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
