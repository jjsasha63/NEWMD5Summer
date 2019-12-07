package com.svirin.controller;//Назва пакету, до якого належить клас
//Підключення потрібних пакетів (бібліотек)
import com.svirin.*;
import com.svirin.POJO.FilePOJO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.regex.Matcher;

//Клас, що контролює роботу вікна з результатами роботи програми. Інші вікна "будувалися"
//ручним кодуванням, без використання графічних редакторів інтерфейсу. Це ж вікно
//будувалось в додатку Scene Builder, результатом роботи якого є файл розмітки з 
//розширенням .fxml. Однак, це лише розмітка вікна, логіку його роботи слід прописувати
//в класі, що називається контролером. Для прив'язки контролера до розмітки, треба зазначити
//назву класу-контролера у файлі розмітки.
public class Controller{
    //Ключ, що вказує на клас, який користуватиметься вікном. Так як вікно результату буде
    //одним, незалежно від вибору функції (підрахувати хеш-суму або перевірити хеш). Якщо
    //значення ключа true, то вікно використовується для виводу результату підрахунку хеша.
    //Якщо false, то вікно виводить результат перевірки хеш-суми.
    public static boolean key = false;
    //Масив файлів, хеш-суми яких підраховані\перевірені. Клас FilePOJO зберігає файл,
    //стан роботи, хеш-суму.
    private ObservableList<FilePOJO> files = FXCollections.observableArrayList();
    //Анотація, що дозволяє файлу розмітки мати доступ до private членів контролера
    @FXML
    //Таблиця, що виводить стан роботи, назву файлу та хеш-суму
    private TableView<FilePOJO> tableFiles;
    @FXML
    //Колонка таблиці, виводить стани роботи. Працює з об'єктами класу FilePOJO, виводить
    //на екран об'єкт Circle - коло
    private TableColumn<FilePOJO, Circle> stateColumn;
    @FXML
    //Колонка таблиці, виводить ім'я файлів. 
    private TableColumn<FilePOJO, String> idColumn;
    @FXML
    //Колонка таблиці, виводить хеш-суму
    private TableColumn<FilePOJO, String> hashColumn;
    @FXML
    //Кнопка, що дозволяє зберегти результат роботи у файл
    private Button SaveButton;
    @FXML
    //Кнопка, що закриває поточне вікно та відкриває початкове вікно
    private Button CancelButton;
    @FXML
    //Прогрес-бар, що заповнюється по мірі виконання підрахунків
    private ProgressBar ProcessingBar;
    @FXML
    //Текстове поле, що виводить назву файла, що оброблюється
    private TextField NameLine;
    @FXML
    //Текстове поле, що виводить шлях до файлу, що оброблюється
    private TextField PathLine;
    @FXML
    //Текстове поле, що виводить розмір у байтах файлу, що оброблюється
    private TextField SizeLine;
    //Метод, що викликається автоматично при завантаженні файлу розмітки
    @FXML
    private void initialize(){
        Main m = new Main();
        /*блок зміни кольору отриманого з головного вікна*/
        String textcolor = "#" + Integer.toHexString(m.colorPicker.getValue().hashCode()).substring(0, 6).toUpperCase();
        String finalColor = "-fx-text-fill: " + textcolor + "; -fx-background-color: #585858;";
        hashColumn.setStyle(finalColor);
        idColumn.setStyle(finalColor);
        //Метод, що ініціалізує масив з інформацією про файли
        initData();
        //Заборона редактування таблиці користувачу
        NameLine.setEditable(false);
        PathLine.setEditable(false);
        SizeLine.setEditable(false);
        //Встановлення значень, що будуть виводитися у таблиці. Для цього вказуємо назву
        //класу, назву та тип поля, що виводиметься
        stateColumn.setCellValueFactory(new PropertyValueFactory<FilePOJO, Circle>("state"));
        idColumn.setCellValueFactory(new PropertyValueFactory<FilePOJO, String>("name"));
        hashColumn.setCellValueFactory(new PropertyValueFactory<FilePOJO, String>("hash"));
        //Вказуємо таблиці масив, вміст якого вона виводить
        tableFiles.setItems(files);
        //Виклик методу, що контролює вікно
        controllingWindow();
    }
    //Метод, що ініціалізує масив файлів
    private void initData(){
        //Якщо вікно виводить результат підрахунку хеш-сум
        if(!key) {
            //Ініціалізуємо наш масив значеннями з масиву вибраних користувачем файлів, що є полем класу FileSelector
            for (FileEx x : FileSelector.selectedFilesL)
                files.add(new FilePOJO(x.getName()));
        }
        //Якщо вікно виводить результат перевірки хеш-сум
        else {
            //Ініціалізуємо масив значеннями з масиву класу Data, що зберігає файли, хеш-суми яких перевіряються
            for (int i = 0; i < Data.names.size(); ++i)
                files.add(new FilePOJO(Data.names.get(i)));
            //Заповнюємо індикатор прогресу повністю, оскільки перевірка хеш-суми проводиться до відкриття вікна результату
            ProcessingBar.setProgress(1);
        }

    }
    //Метод, що встановлює опрацьовувачі подій на кнопки та викликає потрібні методи, залежно від вибраного
    //користувачем функціоналу
    private void controllingWindow(){
        //Встановлення опрацьовувача подій на кнопку, за допомогою якої можна зберегти результати у файл
        SaveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Оскільки виключна ситуація IOException в методі writingHash() не опрацьовується, то поміщаємо
                //виклик цього методу в блок try-catch.
                try {
                    //Виклик методу, що записує результат роботи у текстовий файл
                    if(!key)
                        writingHash();
                    else
                        writeVerifyResults();
                } catch (IOException e) {
                    //Виводимо трасування стеку
                    e.printStackTrace();
                }
            }
        });
        //Встановлення опрацьовувача подій на кнопку, що закриває поточне вікно та відкриває початкове
        CancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Очищаємо всі масиви та оновлюємо таблицю
                files.clear();
                Data.names.clear();
                Data.state.clear();
                Data.hashes.clear();
                FileSelector.selectedFilesL.clear();
                tableFiles.refresh();
                //Закриваємо поточне вікно
                Main.processing.close();
                //Відкриваємо початкове
                Main.mainWindow.show();
            }
        });
        //Якщо вікно використовується для відображення результатів підрахунку хеш-сум
        if(!key)
            //Викликаємо метод, що рахує хеш-суми
            calculateHash();
        else{//інакше
            //Записуємо до масиву файлів стан перевірки хеш-суми та значення хеш-суми
            for(int i = 0; i < files.size(); ++i){
                if(Data.state.get(i))
                    files.get(i).setState(States.OK.getState());
                else
                    files.get(i).setState(States.ERROR.getState());
                files.get(i).setHash(Data.hashes.get(i));
            }
            NameLine.setText("-");
            PathLine.setText("-");
            SizeLine.setText("-");
        }
    }
    //Метод, що рахує хеш-суми файлів
    private void calculateHash(){
        //Для кожного елементу масива рахуємо хеш
        for(int i = 0; i < files.size(); ++i) {
            //Встановлюємо стан елемента на "В ПРОЦЕСІ"
            files.get(i).setState(States.PROCESSING.getState());
            //Виводимо на екран інформацію про файл, який оброблюється
            NameLine.setText(FileSelector.selectedFilesL.get(i).getName());
            PathLine.setText(FileSelector.selectedFilesL.get(i).getFile().getAbsolutePath());
            SizeLine.setText("" + FileSelector.selectedFilesL.get(i).getFile().length() + " bytes");
            //Оскільки методи md5 та createSha1 можуть видати виключну ситуацію, яку не оброблюють, помішаємо виклики
            //цих методів в блок try-catch
            try {
                //Якщо користувач вибрав алгоритм md5 - рахуємо хеш по цьому алгоритму
                if(Main.algorithm)
                    files.get(i).setHash(Main.md5(FileSelector.selectedFilesL.get(i).getFile().getAbsolutePath()));
                else//Інакше рахуємо хеш по алгоритму SHA-1
                    files.get(i).setHash(Main.createSha1(FileSelector.selectedFilesL.get(i).getFile().getAbsolutePath()));
            } catch (NoSuchAlgorithmException e) {
                //Якщо виникла виключна ситуація, то змінюємо стан елемента на "ПОМИЛКА"
                files.get(i).setState(States.ERROR.getState());
                continue;
            } catch (IOException e) {
                //Якщо виникла виключна ситуація, то змінюємо стан елемента на "ПОМИЛКА"
                files.get(i).setState(States.ERROR.getState());
                continue;
            }
            //Якщо хеш-сума підрахована - змінюємо стан елемента на "ОК"
            files.get(i).setState(States.OK.getState());
            //Оновлюємо таблицю
            tableFiles.refresh();
            //Заповнюємо індикатор прогресу в залежності від кількості опрацьованих файлів.
            ProcessingBar.setProgress(((double)i + 1.0) / (double)files.size());
        }
    }
    //Метод для запису результату роботи у файл
    private void writingHash() throws IOException {
        //Створюємо об'єкт FileChooser, що є вікном вибору файлів
        FileChooser fc = new FileChooser();
        //Встановлюємо фільтр розширень, аби користувач міг вибрати або створити новий файл ЛИШЕ з розширенням .md5
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MD5 files (*.md5)", "*.md5"));
        //Показуємо вікно збереження файлу
        File file = fc.showSaveDialog(Main.processing);
        //Якщо файл, в який зберігатиметься результат, не існує або до нього немає доступу - кінець роботи методу
        if(file == null) return;
        //Створюємо об'єкт класу FileWriter, який дозволяє записати інформацію у файл. В конструктор передаємо файл, в
        //який ведеться запис
        FileWriter writer = new FileWriter(file);
        //Записуємо до файлу рядок, що інформує про дату створення хеш-сум
        writer.write("#Genereted " + new Date().toString() + "\n\n\n");
        //Записуємо у файл інформацію про кожний файл
        for(int i = 0; i < files.size(); ++i) {
            //Створюємо рядок-назву файлу, що містить шлях до файлу відносно кореневої папки. Тобто, отримуємо повний
            //шлях до файлу та повний шлях до кореневої папки. У рядку, що є повним шляхом до файлу, змінюємо підрядок,
            //що є повним шляхом до кореневої папки, на порожній рядок. Наприклад, якщо файл знаходиться за шляхом
            //"C:\Folder1\Folder2\file.txt", а коренева папка "C:\Folder1", то в результаті отримаємо "Folder2\file.txt"
            String name = FileSelector.selectedFilesL.get(i).getFile().
                    getAbsolutePath().replace(FileSelector.rootDirectory.getAbsolutePath(), "");
            name = name.substring(1);
            //Записуємо у файл рядок виду "<хеш_сума> *ім'я файлу"
            writer.write(files.get(i).getHash() + " " + "*" +
                    name + "\n");
        }
        //Закриваємо файл, в який записувався результат роботи
        writer.close();
    }
    //Метод для запису результату перевірки хеш-сум
    void writeVerifyResults() throws IOException{
        //Створюємо об'єкт FileChooser, що є вікном вибору файлів
        FileChooser fc = new FileChooser();
        //Встановлюємо фільтр розширень, аби користувач міг вибрати або створити новий файл ЛИШЕ з розширенням .md5
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"));
        //Показуємо вікно збереження файлу
        File file = fc.showSaveDialog(Main.processing);
        //Якщо файл, в який зберігатиметься результат, не існує або до нього немає доступу - кінець роботи методу
        if(file == null) return;
        //Створюємо об'єкт класу FileWriter, який дозволяє записати інформацію у файл. В конструктор передаємо файл, в
        //який ведеться запис
        FileWriter writer = new FileWriter(file);
        for(int i = 0; i < files.size(); ++i)
            if(Data.state.get(i))
                writer.write(files.get(i).getName() + " OK\n");
            else
                writer.write(files.get(i).getName() + " ERROR: " + files.get(i).getHash() + "\n");
        writer.close();
    }
}
