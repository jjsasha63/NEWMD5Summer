package com.svirin;//директива package вказує на назву пакету (бібліотеки), до якої відноситься даний клас
//імпорт (підключення) пакетів (бібліотек), що містять потрібні елементи інтерфейсу та класи
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
//Основний клас програми. Він повинен бути нащадком класу Application, що містить метод start(). Цей метод є
//вхідною точкою для побудови додатку JavaFX. Директива extends вказує на наслідування. Рядок "Main extends Application"
//означає, що клас Main розширяється класом Application, тобто наслідується від нього.
public class Main extends Application {
    //директива static вказує на поле класу, що матиме лише ОДИН екземпляр, незалежно від кількості об'єктів класу.
    //Таке поле може бути доступним навіть якщо не було створено жодного об'єкту. В такій ситуації доступ до статичного
    //поля здійснюється наступним чином: Назва_класу.назва_поля (наприклад Main.mainWindow).
    public static Stage mainWindow;//Основне вікно програми, де користувач вибирає тип хешування та функцію (створити
                                   //хеш-суму або перевірити хеш-суму).
    public static Stage fileSelection;//Вікно вибору файлів з кореневої директорії, хеш яких потрібно створити
    public static Stage processing;//Вікно з результатом роботи програми - таблиця з назвами файлів, хеш-сумами та станом роботи(успішно\помилка...)
    public static boolean algorithm;//Тип алгоритму хешування (true - MD5, false - SHA-1)
    File dir;
    //Статичний метод, що рахує хеш-суму файлу за алгоритмом md5. В якості аргументу приймає рядок - шлях до файлу. Повертає рядок - хеш-суму
    //файлу. Директива throws означає, що метод НЕ опрацьовує виключення, що можуть статися під час роботи, і при виклику
    //цього методу потрібно опрацювати можливі виключення. NoSuchAlgorithmException - алгоритм хешування не існує, або
    //не доступний, IOException - виникла помилка вводу\виводу, в даній ситуації - файл не існує.
    public static String md5(String filename)
            throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(Paths.get(filename)));
        byte[] digest = md.digest();
        String myChecksum = DatatypeConverter
                .printHexBinary(digest).toUpperCase();
        return myChecksum;
    }
    //Статичний метод, що рахує хеш-суму за алгоритмом SHA-1. Аргумент той же, що в минулому методі. Повертає рядок -
    //хеш-суму файлу.
    public static String createSha1(String filename)
            throws NoSuchAlgorithmException, IOException {
        File file = new File(filename);
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
        return DatatypeConverter.printHexBinary(digest.digest()).toUpperCase();
    }

    //Метод, що "будує" основне вікно - додаються елементи інтерфейсу та назначаються обробники подій. В якості аргументу
    //отримує основне вікно програми. Повертає BorderPane - контейнерний елемент, що містить всі інші елементи інтерфейсу.
    private BorderPane initDirectorySelection(Stage primaryStage){
        //Створення контейнеру, до якого додаватимуться всі елементи інтерфейсу вікна. Це контейнер, в якому елементи
        //"притискаються" до однієї зі сторін вікна - верх, низ, центр, ліва сторона вікна, права сторона вікна.
        BorderPane pane = new BorderPane();
        //Встановлення мінімальних розмірів контейнеру - 300х300 пікселів.
        pane.setMinWidth(300);//встановлення мінімальної ширини
        pane.setMinHeight(300);//встановлення мінімальної висоти
        //Контейнер, шо працює за принципом таблиці - елементи додаються до комірок, що характеризуються
        //номером рядка таблиці та номером стовпчика.
        GridPane grid = new GridPane();
        //Контейнер, що є простим списком - елементи додаються один за одним вертикально - вниз.
        VBox folderPane = new VBox();
        //Елемент інтерфейсу Label - відображає напис "Checksum type:"
        Label sumTypeL = new Label("Checksum type:");
        //Метод, що встановлює стиль елементу інтерфейсу. Приймає CSS код. В даному випадку цей код встановлює
        //колір шрифту рівним #b9adb9
        sumTypeL.setStyle("-fx-text-fill: #b9adb9");
        //Створення масиву, що дозволяє відстежувати зміни, що відбуваються з масивом, тобто можна встановити
        //прослуховувач. В даному випадку масив зберігає 2 елементи - назви алгоритмів хешування.
        ObservableList<String> sumTypeA = FXCollections.observableArrayList("MD5", "SHA1");
        //Створення випадаючого списку, що містить в собі елементи масиву, створеного рядок тому. Тобто, у випадаючому
        //списку можна вибрати тип хешування, що буде проводитись.
        ComboBox<String> sumTypeC = new ComboBox<>(sumTypeA);
        //Встановлюємо елемент за замовчуванням. Тобто, рядок "MD5" буде вибрано одразу.
        sumTypeC.setValue("MD5");
        //Створення кнопки, що містить напис "Create sums". За допомогою цієї кнопки користувач вибирає функцію створення
        //хеш-суми файлу\файлів.
        Button createSum = new Button("Create sums");
        //Встановлення кольору заднього фону, що рівний #585858 та кольору тексту, що рівний #b9adb9
        createSum.setStyle("-fx-background-color: #585858; -fx-text-fill: #b9adb9");
        //Створення кнопки, що містить напис "Verify sums". За допомогою цієї кнопки користувач вибирає функцію перевірки
        //хеш-суми файлу\файлів
        Button verify = new Button(" Verify sums ");
        //Встановлення кольору заднього фону, що рівний #585858 та кольору тексту, що рівний #b9adb9
        verify.setStyle("-fx-background-color: #585858; -fx-text-fill: #b9adb9");
        //Створення кнопки, що містить напис "About". За допомогою цієї кнопки користувач вибирає функцію, що відкриває
        //інформацію про програму
        Button about = new Button("About");
        //Встановлення кольору заднього фону, що рівний #585858 та кольору тексту, що рівний #b9adb9
        about.setStyle("-fx-background-color: #585858; -fx-text-fill: #b9adb9");
        //Створення елементу інтерфейсу, що дозволяє вибрати директорію
        DirectoryChooser dc = new DirectoryChooser();
        //Створення елементу інтерфейсу, що дозволяє вибрати файл
        FileChooser fc = new FileChooser();
        //Елемент інтерфейсу Label - відображає напис "Please, enter the path to the root folder:"
        Label enterDir = new Label("Please, enter the path to the root folder:");
        //Встановлення кольору тексту, що рівний #b9adb9
        enterDir.setStyle("-fx-text-fill: #b9adb9");
        //Елемент інтерфейсу Label - відображає напис "or select the root folder:"
        Label selectDir = new Label("or select the root folder:");
        //Встановлення кольору тексту, що рівний #b9adb9
        selectDir.setStyle("-fx-text-fill: #b9adb9");
        //Створення текстового поля, в якому можна написати шлях до кореневої папки
        TextField directoryT = new TextField();
        TextArea anotherDirectory = new TextArea();
        //Встановлення максимального розміру текстового поля - ширина - 170 пікселів, висота - 70 пікселів
        directoryT.setMaxSize(170, 70);
        //Створення кнопки, що відкриватиме вікно вибору кореневої папки
        Button selectDirB = new Button("Select Folder");
        //Встановлення кольору заднього фону, що рівний #585858 та кольору тексту, що рівний #b9adb9
        selectDirB.setStyle("-fx-background-color: #585858; -fx-text-fill: #b9adb9");

        //Встановлення опрацьовувача подій на кнопку, за допомогою якої вибирається коренева директорія. Для цього слід
        //викликати метод setOnAction, в який передається реалізація інтерфейсу EventHandler.
        // У методі handle визначаються дії, які будуть викликатися при натисканні на кнопку
        selectDirB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Відкриваємо вікно вибору директорії методом showDialog, в який передається вікно програми.
                //Повертає повний шлях до директорії.
                 dir = dc.showDialog(primaryStage);
                 //Якщо сталася помилка або доступ до папки заборонений, то змінна dir дорівнюватиме null.
                //В цьому випадку - завершення роботи опрацьовувача події.
                if(dir == null) return;
                //Якщо вибрана директорія існує і доступ дозволено - вставляємо повний шлях до директорії у текстове поле
                //щоб користувач бачив шлях. Для отримання повного шляху викликається метод getAbsolutePath класу File
                directoryT.setText(dir.getAbsolutePath());
            }
        });
        //Встановлення опрацьовувача подій на кнопку, за допомогою якої вибирається функція створення хеш-суми файлу
        createSum.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Зчитуємо вибраний елемент з випадаючого списку. Для цього слід отримати модель вибору методом
                //getSelectionModel, після чого отримати вибраний елемент методом getSelectedItem. Якщо вибраний алгоритм -
                //MD5, то присвоюємо змінній algorithm - true, інакше - false
                if(sumTypeC.getSelectionModel().getSelectedItem() == "MD5")
                    algorithm = true;
                else
                    algorithm = false;
                //Створення об'єкту класу File, що буде кореневою директорією, хеш-суми файлів з якої будут підраховуватися
                //Шлях до кореневої директорії отримується з текстового поля
                File dir = new File(directoryT.getText());
                //Після отримання шляху -
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
                if(sumTypeC.getSelectionModel().getSelectedItem() == "MD5")
                    algorithm = true;
                else
                    algorithm = false;
                dir = new File(directoryT.getText());
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
        Rectangle background = new Rectangle(310, 310, Color.web("#424242"));
        g.getChildren().add(background);
        g.getChildren().add(initDirectorySelection(primaryStage));
        Scene scene = new Scene(g, 300, 300);

        mainWindow = primaryStage;
        mainWindow.setScene(scene);
        mainWindow.setTitle("MD5Summer");
        mainWindow.setResizable(false);
        mainWindow.show();
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
	    Application.launch(args);
    }
}
