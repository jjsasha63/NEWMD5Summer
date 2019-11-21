package com.svirin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Data {
    List<String> finalstr = new ArrayList<String>();
    List<String> lines = new ArrayList<String>();
    List<Boolean> state = new ArrayList<Boolean>();
    List<String> names = new ArrayList<>();
    List<String> hashes = new ArrayList<>();
    String rootfile = new String();
    String md5 = new String();
    File temp;

    public Data(){}
    Data(File dir,String dir1,String dir2) {// input data from explorer
         temp = new File(String.valueOf(dir));
         md5 = dir1;
         rootfile = dir2;
    }

    void transform() throws IOException, NoSuchAlgorithmException { //create line like *......
        Main main = new Main();
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(md5));
        File[] array = temp.listFiles();
        String files[] = Arrays.stream(array).map(File::getAbsolutePath)
                .toArray(String[]::new);
        rootfile += "\\";
        int i = 0;
        for(String a : files){
            a = a.replace(rootfile,"");
            finalstr.add(i," *" + a);
            i++;
        }

         i = 0;
        for (String a : files) {
            a = main.md5(a);
            files[i] = a;
            i++;
        }
        for (i = 0;i<files.length;i++) {
            finalstr.set(i,files[i] + finalstr.get(i));
        }

        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

    //    String[] md5hashes = lines.toArray(new String[lines.size()]);
        //check for equality
        int z = 0;
        for( i=1;i<lines.size();i++){
            state.add(false);
            for(int k=0;k<finalstr.size();k++){
                if(lines.get(i).equals(finalstr.get(k))) {
                    state.set(i - 1, true);
                    names.add(lines.get(i).substring(33));
                    hashes.add(finalstr.get(k).replace(names.get(z),""));
                    System.out.println(hashes.get(z));
                    z++;
                }
            }
        }
        System.out.println(state);


    }
}