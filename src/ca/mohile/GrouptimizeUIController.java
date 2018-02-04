package ca.mohile;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.application.Application;

public class GrouptimizeUIController{

    @FXML
    Tab peopleTab;


    /*
    People management
     */
    @FXML
    TextField nameField;

    @FXML
    ListView peopleList;
    /*
    Choices management
     */
    @FXML
    VBox choicesContainer;
    @FXML
    Button addChoiceButton;

    @FXML
    Slider numGroupsSlider;
    @FXML
    Slider peoplePerGroupSlider;

    @FXML
    ChoiceBox<Iteration.Types> iterationTypeSelector;

    @FXML
    ListView groupsList;
    static Iteration iteration;

    PersonChoices.PersonChoicesReload pcr = new PersonChoices.PersonChoicesReload() {
        @Override
        public void reload() {
            configureAddChoiceButton();
        }
    };

    public GrouptimizeUIController(){};
    public void config(Node root, Iteration iteration) throws Exception {
        this.iteration = iteration;
        iterationTypeSelector.getItems().addAll(Iteration.Types.values());
    }


    public void addPerson() {
        String name = nameField.getText();
        if (!iteration.has(name) && name.length() > 0) {
            iteration.add(new GPerson(name));
            peopleList.getItems().add(name);
        }
        nameField.setText("");
        configureAddChoiceButton();
        configureGroupSliders();
    }

    public void removePerson() {
        String name = peopleList.getSelectionModel().getSelectedItem().toString();
        iteration.remove(name);
        peopleList.getItems().remove(peopleList.getSelectionModel().getSelectedItem());
        nameField.setText("");
        configureAddChoiceButton();
        configureGroupSliders();
    }

    public void configureGroupSliders() {
        numGroupsSlider.setMax(iteration.people.size());
        numGroupsSlider.setMin(1);
        peoplePerGroupSlider.setMax(iteration.people.size());
        peoplePerGroupSlider.setMin(1);
    }


    public void loadPerson() throws IOException {
        String name = peopleList.getSelectionModel().getSelectedItem().toString();
        GPerson person = iteration.get(name);
        nameField.setText(name);

        loadPersonChoices(person);

    }

    public void loadPersonChoices(GPerson person) throws IOException {
        choicesContainer.getChildren().clear();

        for (GPerson choice : person.choices.keySet()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PersonChoices.fxml"));
            Node personChoice = loader.load();
            loader.<PersonChoices>getController().configure(personChoice, choicesContainer, person, choice, iteration.people, pcr);
        }
        configureAddChoiceButton();
    }

    public void configureAddChoiceButton() {
        if (choicesContainer.getChildren().size() >= iteration.people.size() - 1) {
            addChoiceButton.setDisable(true);
        } else {
            addChoiceButton.setDisable(false);
        }
    }

    public void savePersonName() {
        String oldName = peopleList.getSelectionModel().getSelectedItem().toString();
        String newName = nameField.getText();
        iteration.get(oldName).changeName(newName);
        peopleList.getItems().set(peopleList.getSelectionModel().getSelectedIndex(), newName);
    }

    public void addChoice() throws Exception {
        String name = peopleList.getSelectionModel().getSelectedItem().toString();
        GPerson person = iteration.get(name);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PersonChoices.fxml"));
        Node personChoice = loader.load();
        loader.<PersonChoices>getController().configure(personChoice, choicesContainer, person, null, iteration.people, pcr);
        configureAddChoiceButton();
    }

    public void createGroups() {
        int numGroups = (int) numGroupsSlider.getValue();
        int numPerGroup = (int) peoplePerGroupSlider.getValue();
        groupsList.getItems().clear();

        iteration.optimize(numGroups, numPerGroup, iterationTypeSelector.getValue());
        for (Groupable g : iteration.getGroups()) {
            groupsList.getItems().add(g);
        }
    }
}


