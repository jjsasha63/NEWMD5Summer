package com.svirin;

import com.svirin.controller.Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * клас Data використовується для отримання данних з файлу типу .md5
 * виконує перетворення в потрібну форму хешу для подальшого порівняння
 * повертає результати порівняння хешів вибраних файлів з хешами що знаходяться у вибраному файлі типу .md5
 */
public class Data {
    /**Список рядків призначених для зберігання хешів в потрібній формі для порівняння */
    List<String> finalstr = new ArrayList<String>();
    /**Список рядків для проміжного збереження інформації*/
    List<String> lines = new ArrayList<String>();
    /**список станів результатів порівняння хешів*/
    public static List<Boolean> state = new ArrayList<Boolean>();
    /**список для зберігання шляхів до перевіряємих файлів*/
    public static List<String> names = new ArrayList<>();
    /**список для зберігання хешів перевіряємих файлів*/
    public static List<String> hashes = new ArrayList<>();
    /**рядкова змінна призначена для зберігання кореневої папки перевірки*/
    String rootfile = new String();
    /**рядкова змінна що зберігає шлях до вибраного файлу типу .md5*/
    String md5 = new String();
    /***/
    File temp;

    public Data(){}

    /**
     * конструктор для отримання та зберігання шляхів до вибраних файлів
     * @param dir
     * @param dir1
     * @param dir2
     */
    Data(File dir,String dir1,String dir2) {// input data from explorer
        Controller.key = true;
         temp = new File(String.valueOf(dir)); //перетворення та збереження файлу в тип String
         md5 = dir1; //збереження шляху до файлу .md5
         rootfile = dir2; //збереження кореневого шляху вибраної папки
    }

    /**
     * отримання хешів порівнюваних файлів
     * формування хешів та назв файлів в потрібному для поріняння форматі
     * реалізація порівняння хешів
     * формування результатів порівняння
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    void transform() throws IOException, NoSuchAlgorithmException { //create line like *......
        String line; //змінна для проміжного запису результату
        BufferedReader reader = new BufferedReader(new FileReader(md5));  // зчитування файлу .md5
        List<String> files = new ArrayList<>();
        /*порядкове зчитування данних з файлу*/
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        /*запис хешу в порівнювані рядки*/
        for(int i = 3; i < lines.size(); ++i) {
            if(Main.algorithm)
                finalstr.add(lines.get(i).substring(33));
            else
                finalstr.add(lines.get(i).substring(41));
        }
        int i = 0;
        /*
        * формування остаточного вигляду рядка для порівняння
        * додавання шляху в рядок
        */
        for (String a : finalstr) {
            if(Main.algorithm)
                finalstr.set(i, Main.md5(rootfile + "\\" + a.substring(1)) + " " + finalstr.get(i));
            else
                finalstr.set(i, Main.createSha1((rootfile + "\\" + a.substring(1))) + " " + finalstr.get(i));
            i++;
        }

        int z = 0;
        /*перевірка на рівність*/
        for( i=3;i<lines.size();i++){
            for(int k=0;k<finalstr.size();k++){
                if(lines.get(i).toUpperCase().equals(finalstr.get(k).toUpperCase())){
                    state.add(true); //збереження стану результату
                    /*отримання імен перевірених файлів*/
                    if(Main.algorithm)
                        names.add(lines.get(i).substring(33));
                    else
                        names.add(lines.get(i).substring(41));
                    hashes.add(finalstr.get(k).replace(names.get(z),"")); //отримання перевіреного хешу
                    System.out.println(hashes.get(z));
                    z++;
                }
            }
            /*при негативному результаті перевірки*/
            if(!names.contains(lines.get(i).substring(33))){
                state.add(false); //запис стану результату
                names.add(lines.get(i).substring(33)); //ім'я файлу для виведення
                hashes.add("Checksum did not match."); // повідомлення про негативний результат
            }
        }
    }
}