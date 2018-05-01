/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qrgenerator;


import qrgenerator.Model.Model;
import java.io.*;
import java.util.ArrayList;

public class Utils {

    public static void main(java.lang.String[] args) {

  /*      String src = "src/model/file.txt";
        ArrayList<Model> models = new ArrayList<>();

        models = readModel(src);

        for (int i = 0; i < models.size(); i++) {
            Model m = models.get(i);
            System.out.println("-------------------------------");
            System.out.print("Model Name : ");
            System.out.println(m.getModelName());
            System.out.println("Attr is : ");

            ArrayList<String> stringArrayList = m.getElements();
            for (int j = 0; j < stringArrayList.size(); j++) {
                System.out.println("\t " + stringArrayList.get(j));
            }
            System.out.println("-------------------------------");

        }

        Model m = models.get(0);
        addModel("file_out", m);*/

    }
    public static ArrayList<String> getAttr(String name) {
        //System.out.println("=============================================" );
        String src = "src/model/file.txt";
        Model m;
        ArrayList<Model> models = new ArrayList<>();
        ArrayList<String> stringArrayList = null;
        models = readModel(src);

        for (int i = 0; i < models.size(); i++) {
            m = models.get(i);
            if(m.getModelName().equals(name)) {
                //System.out.println(m.getElements());
                stringArrayList = m.getElements();
                return stringArrayList;
            }
        }
        //m = models.get(0);
        //addModel("file_out", m);
        return stringArrayList;
        
    }

    public static ArrayList<Model> readModel(String src) {
        try {

            File f = new File(src);

            BufferedReader b = new BufferedReader(new FileReader(f));

            String readLine = "";

            ArrayList<Model> modelArrayList = new ArrayList<Model>();

            while ((readLine = b.readLine()) != null) {
                Model model = new Model();
                String[] strArray = readLine.split("\\|");
                model.setModelName(strArray[0]);
               // System.out.println(model.getModelName());

                for (int i = 1; i < strArray.length; i++) {
                    model.addElement(strArray[i]);
                }

                modelArrayList.add(model);
            }
            return modelArrayList;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addModel(String src, Model model) {
        String str = model.getModelName();

        ArrayList<String> arrayList = model.getElements();
        for (int i = 1; i < arrayList.size(); i++) {
            str = str.concat("|" + arrayList.get(i));
        }

        File dir = new File(".");
        try {


            String loc = dir.getCanonicalPath() + File.separator + "file";

            FileWriter fstream = new FileWriter(loc, true);
            BufferedWriter out = new BufferedWriter(fstream);

            out.write(""+str);
            out.newLine();

            //close buffer writer
            out.close();
        } catch (Exception e) {

        }

    }
}
