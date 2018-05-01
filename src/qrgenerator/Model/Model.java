/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qrgenerator.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class Model {
    private String modelName;
    private ArrayList<String> elements = new ArrayList<String>();


    public Model() {
    }

    public Model(String modelName, ArrayList<String> elements) {

        this.modelName = modelName;
        this.elements = elements;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setElements(ArrayList<String> elements) {
        this.elements = elements;
    }

    public String getModelName() {

        return modelName;
    }

    public ArrayList<String> getElements() {
        return elements;
    }

    public void addElement(String str) {
        this.elements.add(str);
    }
}
