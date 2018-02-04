package ca.mohile;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PersonChoices {
    public interface PersonChoicesReload{
        void reload();
    }
    @FXML
    JFXComboBox<GPerson> choiceSelector;
    @FXML
    JFXComboBox<Integer> strengthSelector;

    @FXML
    Button saveButton;

    @FXML VBox container;

    Node personChoice;

    GPerson person;

    Boolean changed = false;

    PersonChoicesReload pcr;
    public PersonChoices() {
    }

    void configure(Node personChoice, VBox root, GPerson person, GPerson choice, ArrayList<GPerson> people, PersonChoicesReload pcr) {
        this.person = person;
        this.personChoice = personChoice;
        choiceSelector = (JFXComboBox<GPerson>) personChoice.lookup("#choiceSelector");
        strengthSelector = (JFXComboBox<Integer>) personChoice.lookup("#strengthSelector");
        this.pcr = pcr;

        for (int x = 1; x <= 10; x++) {
            strengthSelector.getItems().add(x);
        }

        saveButton = (Button) personChoice.lookup("#saveButton");

        people.forEach((GPerson p) -> {
            if (person != p && (!person.choices.containsKey(p) || (person.choices.containsKey(p) && choice != null))) {
                choiceSelector.getItems().add(p);
            }
        });

        choiceSelector.getSelectionModel().select(choice);
        if(choice != null){
            strengthSelector.getSelectionModel().select((Integer)(person.getStrength(choice)));
        }else{
            strengthSelector.getSelectionModel().select((Integer) (person.choices.size() + 1));
        }
        container = root;
        container.getChildren().add(personChoice);
    }

    public void choiceChanged() {
        changed = true;
        configureSaveButton();
    }
    public void strengthChanged(){
        changed = true;
        configureSaveButton();
    }
    void configureSaveButton(){
        if(changed){
            saveButton.setDisable(false);
        }else{
            saveButton.setDisable(true);
        }
    }

    public void saveChoice() {
        GPerson choice = getChoice();
        person.addChoice(choice, getStrength());
        changed = false;
        configureSaveButton();
    }

    public void removeChoice() {
        container.getChildren().remove(personChoice);
        GPerson choice = getChoice();
        person.removeChoice(choice);
        pcr.reload();
    }

    public GPerson getChoice() {
        return choiceSelector.getSelectionModel().getSelectedItem();
    }

    public int getStrength() {
        return strengthSelector.getSelectionModel().getSelectedItem();
    }
}
