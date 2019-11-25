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


public class Data {
    List<String> finalstr = new ArrayList<String>();
    List<String> lines = new ArrayList<String>();
    public static List<Boolean> state = new ArrayList<Boolean>();
    public static List<String> names = new ArrayList<>();
    public static List<String> hashes = new ArrayList<>();
    String rootfile = new String();
    String md5 = new String();
    File temp;

    public Data(){}
    Data(File dir,String dir1,String dir2) {// input data from explorer
        Controller.key = true;
         temp = new File(String.valueOf(dir));
         md5 = dir1;
         rootfile = dir2;
    }

    void transform() throws IOException, NoSuchAlgorithmException { //create line like *......
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(md5));
        //adding ONLY files, without directories
        List<String> files = new ArrayList<>();
        for(File x : temp.listFiles())
            if(!x.isDirectory()) files.add(x.getAbsolutePath());
        rootfile += "\\";
        int i = 0;
        for(String a : files){
            a = a.replace(rootfile,"");
            finalstr.add(i," *" + a);
            i++;
        }

         i = 0;
        for (String a : files) {
            a = Main.md5(a);
            files.set(i, a);
            i++;
        }
        for (i = 0;i<files.size();i++) {
            finalstr.set(i,files.get(i) + finalstr.get(i));//string like "<hash> *filename"
        }

        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

    //    String[] md5hashes = lines.toArray(new String[lines.size()]);
        //check for equality
        int z = 0;
        for( i=1;i<lines.size();i++){
            for(int k=0;k<finalstr.size();k++){
                if(lines.get(i).equals(finalstr.get(k))) {
                    state.add(true);
                    names.add(lines.get(i).substring(33));
                    hashes.add(finalstr.get(k).replace(names.get(z),""));
                    System.out.println(hashes.get(z));
                    z++;
                }
            }
            if(!names.contains(lines.get(i).substring(33))){
                state.add(false);
                names.add(lines.get(i).substring(33));
                hashes.add("Checksum did not match.");
            }
        }
    }
}