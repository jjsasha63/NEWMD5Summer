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
        folders.setStyle(Main.textcolor);
        numberOfItems.setStyle(Main.textcolor);
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
        //Встановлюємо опрацьовувач події на список обраних файлів. При подвійному кліку - елемент видаляється
        selectedFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() != 2) return;
                selectedFilesL.remove(selectedFiles.getSelectionModel().getSelectedItem());
                items--;
                //Оновлюємо лейбл, що відображає на екрані кількість вибраних файлів
                numberOfItems.setText(itemsStr + items);
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
        selectAll.setStyle(Main.buttoncol);
        //Встановлення мінімальної ширини кнопки в 150 пікселів
        clearList.setMinWidth(150);
        //Встановлення кольору заднього фону, що дорівнює #585858, та кольору тексту #b9adb9
        clearList.setStyle(Main.buttoncol);
        //Встановлення мінімальної ширини кнопки в 150 пікселів
        add.setMinWidth(150);
        //Встановлення кольору заднього фону, що дорівнює #585858, та кольору тексту #b9adb9
        add.setStyle(Main.buttoncol);
        //Встановлення мінімальної ширини кнопки в 150 пікселів
        addRec.setMinWidth(150);
        //Встановлення кольору заднього фону, що дорівнює #585858, та кольору тексту #b9adb9
        addRec.setStyle(Main.buttoncol);
        //Встановлення мінімальної ширини кнопки в 100 пікселів
        ok.setMinWidth(100);
        //Встановлення кольору заднього фону, що дорівнює #585858, та кольору тексту #b9adb9
        ok.setStyle(Main.buttoncol);
        //Встановлення мінімальної ширини кнопки в 100 пікселів
        cancel.setMinWidth(100);
        //Встановлення кольору заднього фону, що дорівнює #585858, та кольору тексту #b9adb9
        cancel.setStyle(Main.buttoncol);
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
                //Оновлюємо списки для коректного відображення змісту
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
                //Використовуємо цикл for-each для перебору масиву вибраних елементів
                for(FileEx x : buffer) {
                    //Перевірки: чи існує об'єкт (тобто, чи посилання на об'єкт не дорівнює null), чи вже не додано
                    //об'єкт до масиву вибраних файлів і чи не є вибраний елемент списку директорією, а є файлом
                    if (x != null && !containsFile(selectedFilesL, x)
                            && !x.getFile().isDirectory()) {
                        //Додаємо файл до списку обраних файлів методом add()
                        selectedFilesL.add(x);
                        //Збільшуємо кількість вибраних файлів на одиницю
                        items++;
                    }
                    //Якщо вибраний елемент - директорія, то додаємо всі файли з цієї директорії
                    else if(x.getFile().isDirectory()) addAll(x.getFile());
                }
                //Оновлюємо лейбл, що відображає на екрані кількість вибраних файлів
                numberOfItems.setText(itemsStr + items);
                //Оновлюємо списки для коректного відображення змісту
                filesInDir.refresh();
                selectedFiles.refresh();
            }
        });
        //Встановлення опрацьовувача подій на кнопку, що додає всі файли з обраної директорії без переходу
        //в цю директорію
        addRec.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Оскільки нам потрібен лише один елемент - директорія, то викликаємо метод getSelectedItem(),
                //що поверне саме один вибраний елемент
                FileEx buffer = filesInDir.getSelectionModel().getSelectedItem();
                //Якщо вибраний елемент не є директорією - завершення роботи опрацьовувача
                if(!buffer.getFile().isDirectory()) return;
                //Щоб отримати масив всіх файлів каталогу, слід викликати метод listFiles() класу File, який
                //працює для директорій. Записуємо результат роботи методу в масив.
                File[] files = buffer.getFile().listFiles();
                //Використовуємо цикл for-each для перебору масиву файлів вибраного каталогу
                for(File x : files) {
                    //Створюємо об'єкт класу FileEx, оскільки масив вибраних для підрахунку хеш-сум файлів зберігає
                    //об'єкти саме цього класу. Конструктор класу приймає об'єкт класу File в якості аргументу
                    FileEx extendedFile = new FileEx(x);
                    //Якщо об'єкт File є директорією - пропускаємо поточну ітерацію
                    if(x.isDirectory()) continue;
                    //Якщо файл існує і ще не був доданий до масиву вибраних для підрахунку хеш-сум файлів,
                    //то додаємо файл до цього масиву.
                    if(x != null && !containsFile(selectedFilesL, extendedFile)){
                        selectedFilesL.add(extendedFile);
                        //Збільшуємо кількість вибраних файлів на одиницю
                        items++;
                    }
                }
                //Оновлюємо лейбл, що відображає на екрані кількість вибраних файлів
                numberOfItems.setText(itemsStr + items);
            }
        });
        //Встановлення опрацьовувача подій на кнопку, що закриває поточне вікно та відкриває вікно з результатом роботи
        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Створюємо блок try-catch, що слугує для "перехвату" виключних ситуацій, які неможливо передбачити
                //на етапі компіляції (наприклад, файл не знайдено). У блоці try записується код, що може видати
                //виключну ситуацію, у блок catch передається та оброблюється виключення. Клас ProcessingWin може
                //видати виключення Exception, якщо не було знайдено файл розмітки вікна (див. коментарі класу
                //ProcessingWin)
                try {
                    //Створюємо об'єкт класу ProcessingWin, який повертає вікно з результатами роботи програми
                    ProcessingWin outWin = new ProcessingWin();
                    //Передаємо в клас Main вікно з результатами роботи програми, яке отримали методом getProcessStage()
                    Main.processing = outWin.getProcessStage();
                    //Закриваємо вікно вибору файлів для підрахунку хеш-сум
                    Main.fileSelection.close();
                    //Відкриваємо вікно з результатами роботи програми
                    Main.processing.show();
                } catch (Exception e) {//Перехоплюємо можливе виключення
                    //При виключній ситуації виводимо трасування стеку. Простими словами, це список методів, що були
                    //викликані до моменту, коли в додатку з'явилось виключення. Це допомагає віднайти клас, метод якого
                    //видав виключну ситуацію.
                    e.printStackTrace();
                }
            }
        });
        //Встановлення опрацьовувача подій на кнопку, що повертає користувача на крок назад - до вибору кореневої
        //папки, хеш-суми файлів з якої треба підрахувати
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //З масиву, що зберігає вибрані для підрахунку хешу файли, видаляємо всі елементи
                selectedFilesL.remove(0, selectedFilesL.size());
                //Закриваємо вікно вибору файлів
                selectorStage.close();
                //Відображаємо вікно вибору кореневої папки
                Main.mainWindow.show();
            }
        });
        //Додаємо до "табличного" контейнеру кнопки. Для цього викликається метод add(), в який
        //передається елемент, що потрібно додати, номер стовпчика та номер рядка
        buttonsGridPane.add(selectAll, 0, 0);
        buttonsGridPane.add(clearList, 0, 1);
        buttonsGridPane.add(add, 1, 0);
        buttonsGridPane.add(addRec, 1, 1);
        buttonsGridPane.add(ok, 2, 1);
        buttonsGridPane.add(cancel, 3, 1);
        buttonsGridPane.setHgap(10); //Встановлення відступу між стовпчиками - 10 пікселів
        buttonsGridPane.setVgap(10); //Встановлення відступу між рядками - 5 пікселів
        //Відступи для контейнера відносно сторін контейнеру. Від лівого краю - 10 пікселів, від верхнього - 10 пікселів
        buttonsGridPane.setPadding(new Insets(10, 0, 0, 10));
        //Прив'язуємо групу кнопок до нижнього краю вікна
        bp.setBottom(buttonsGridPane);
        //Прямокутник розміром 620, 420 с кольором заливки #424242, що є фоном всього вікна
        Rectangle background = new Rectangle(620, 420);
        background.setStyle(Main.backgroundcol);
        //Група компонентів, за допомогою яких користувач керує додатком. За правилом театру - група акторів постановки
        Group g = new Group();
        //Додаємо фон до групи
        g.getChildren().add(background);
        //Додаємо компоненти до групи
        g.getChildren().add(bp);
        //Створюємо об'єкт класу Scene, конструктор якого приймає контейнер з елементами та розмір вікна. За правилом
        //театру - це постанова, до якої ми додаємо групу акторів (контейнер з елементами інтерфейсу)
        Scene scene = new Scene(g, 600, 400);
        //Додаємо на сцену постанову
        selectorStage.setScene(scene);
        //Додаємо заголовок вікна
        selectorStage.setTitle("Create list of files to sum");
        //Метод, що дозволяє зробити вікно незмінним за розміром. Для цього слід передати false в якості аргумента
        selectorStage.setResizable(false);
    }
    //Метод, що оновлює вміст масиву файлів каталогу при переході користувача до іншої папки. Аргументів не приймає.
    private void setFilesObList(){
        //Видаляємо всі елементи масиву
        filesInDirL.remove(0, filesInDirL.size());
        //Якщо папка, в яку переходить користувач, не є кореневою... Для перевірки цього викликається метод
        //getAbsolutePath(), що повертає рядок - повний шлях до директорії. Для порівняння рядків використовується метод
        //equals().
        if(!nextDir.getAbsolutePath().equals(rootDirectory.getAbsolutePath()))
            //Додаємо до масиву файлів директорії першим елементом папку з назвою "..". При подвійному кліку на цю папку
            //користувач повернеться до минулої папки
            filesInDirL.add(new FileEx(getPrevDir(nextDir), ".."));
        //За допомогою циклу for-each додаємо до масиву всі файли\директорії, що містить нова папка
        for(File x : nextDir.listFiles())
            filesInDirL.add(new FileEx(x));
    }
    //Метод для отримання абсолютного шляху минулої папки. В якості аргументу приймає поточний каталог. Повертає
    //об'єкт класу File, що є минулою папкою
    private File getPrevDir(File dir){
        //Створюємо буфер, в який записуємо шлях до поточної папки
        String buf = dir.getAbsolutePath();
        //Змінна, що зберігає індекс символу \
        int lastInd = 0;
        //Цикл, що з кінця перебирає символи шляху до поточної папки. Результатом роботи циклу є індекс символу \.
        //Якщо файл до папки виглядатиме як "C:\Folder1\Folder2", то цикл пропускає останні 7 символів і записує
        //у змінну lastInd індекс символу \, що знаходиться перед Folder2
        for(int i = buf.length() - 1; i >= 0; i--){
            if(buf.charAt(i) == '\\'){
                lastInd = i;
                break;
            }
        }
        //Створюємо об'єкт класу File, що є минулою папкою. В якості аргументу передаємо підрядок шляху поточного
        //каталогу. Підрядок починається на початку основного рядку і закінчується на індексі, що зберігається в
        //змінній lastInd. Якщо шлях поточного каталогу "C:\Folder1\Folder2", то підрядок виглядатиме як "C:\Folder1"
        File ret = new File(buf.substring(0, lastInd));
        //Повертаємо новостворений об'єкт
        return ret;
    }
    //Метод, що визначає, чи було додано файл до масиву. В якості аргументів приймає масив файлів та файл, наявність
    //якого в масиві перевіряється. Повертає true, якщо файл вже було додано, інакше false.
    private boolean containsFile(ObservableList<FileEx> list, FileEx file){
        //Використовуємо цикл for-each для перебору масиву
        for(FileEx x : list)
            //Якщо назви файлів співпадають - повертаємо true
            if(x.getName().equals(file.getName())) return true;
        //Якщо файл не було знайдено в масиві - повертаємо false
        return false;
    }
    //Метод, що додає всі файли з поточної папки та папок, що знаходяться всередині поточної. В якості аргументу
    //приймає поточну папку
    private void addAll(File dir){
        //Отримуємо масив всіх файлів\папок поточної директорії
        File[] files = dir.listFiles();
        //Використовуємо цикл for-each для перебору масиву
        for(File x : files){
            //Якщо поточний елемент масиву - папка, то викликаємо рекурсію
            if(x.isDirectory()) addAll(x);
            //Інакше додаємо до масиву обраних для підрахунку хеш-сум файлів поточний елемент
            else {
                selectedFilesL.add(new FileEx(x));
                //Збільшуємо кількість обраних файлів на одиницю
                items++;
            }
        }
    }
    //Метод, що повертає вікно вибору файлів.
    public Stage getSelectorStage(){
        return selectorStage;
    }
}
//Клас, що є новою реалізацією комірки списку. Звичайна комірка відображає лише рядок, а нам потрібно відобразити
//ще й системну іконку файлу. Для "переписання" комірки слід наслідуватись від класу ListCell, що зберігатиме в собі
//об'єкти класу FileEx
class FileCell extends ListCell<FileEx>{
    //Для зміни зовнішнього вигляду комірку треба перевизначити метод updateItem батьківського класу. В аргументи
    //приймається об'єкт, що зберігається в комірці та стан комірки empty (true - комірка порожня, інакше - комірка
    // зберігає об'єкт)
    @Override
    public void updateItem(FileEx name, boolean empty){
        //Для того, щоб нова реалізація комірки працювала, обов'язково треба викликати метод батьківського класу,
        //інакше поведінка програми буде невідома. Як правило, при відсутньому виклику методу суперкласу, не працює
        //вибір елементів зі списку.
        super.updateItem(name, empty);
        //Якщо комірка порожня - виставляємо текст та графіку в null, що означатиме пустоту комірки
        if(empty){
            setText(null);
            setGraphic(null);
        }
        //Якщо комірка зберігає значення
        else{
            //За допомогою методу setText записуємо в комірку рядок - назву файлу
            setText(name.getName());
            //За допомогою методу setGraphic передаємо в комірку зображення - іконку файлу
            setGraphic(name.getIcon());
        }
    }
}
