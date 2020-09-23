package sample.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sample.Main;
import sample.utils.processloader.ProcessEntry;


public class MainController {
    @FXML
    private TableView<ProcessEntry> processTable;
    @FXML
    private TableColumn<ProcessEntry, String> firstColumn;
    @FXML
    private TableColumn<ProcessEntry, String> thirdColumn;

    @FXML
    private Label parentIDLabel;
    @FXML
    private Label parentNameLabel;
    @FXML
    private Label pidLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label runEnvLabel;
    @FXML
    private Label depLabel;
    @FXML
    private Label dllLibsLabel;
    @FXML
    private Label sidLabel;
    @FXML
    private Label intLevelLabel;
    @FXML
    private Label privilegesLabel;
    @FXML
    private Label aclLabel;
    @FXML
    private Label fileOwnerLabel;

    // Ссылка на главное приложение.
    private Main mainApp;

    private boolean maxSize = false;

    public MainController() {
    }

    @FXML
    private void initialize() {
        // Инициализация таблицы адресатов с двумя столбцами.
        firstColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProcessName()));
        thirdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExePath()));

        showProcessDetails(null);

        try {
            processTable.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> showProcessDetails(newValue));
        }catch (Exception e) { e.printStackTrace(); }
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        // Добавление в таблицу данных из наблюдаемого списка
        processTable.setItems(mainApp.getProcessEntryList());
    }

    private void showProcessDetails(ProcessEntry processEntry) {
        try {
            if (processEntry != null) {
                // Заполняем метки информацией из объекта person.
                parentIDLabel.setText(Integer.toString(processEntry.getParentProcessID()));
                parentNameLabel.setText(processEntry.getOwnerDomain());
                pidLabel.setText(Integer.toString(processEntry.getProcessID()));
                typeLabel.setText(processEntry.getProcessType());
                runEnvLabel.setText(processEntry.getRuntime());
                depLabel.setText(processEntry.getSpaceLayout());
                sidLabel.setText(processEntry.getSID());
                intLevelLabel.setText("no");
                privilegesLabel.setText("no");
                aclLabel.setText("no");
                fileOwnerLabel.setText(processEntry.getOwnerName());

            } else {

                parentIDLabel.setText("");
                parentNameLabel.setText("");
                pidLabel.setText("");
                typeLabel.setText("");
                runEnvLabel.setText("");
                depLabel.setText("");
                dllLibsLabel.setText("");
                sidLabel.setText("");
                intLevelLabel.setText("");
                privilegesLabel.setText("");
                aclLabel.setText("");
                fileOwnerLabel.setText("");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleEditPerson() {
        ProcessEntry selectedProcess = processTable.getSelectionModel().getSelectedItem();
        if (selectedProcess != null) {
            boolean okClicked = mainApp.showProcessEditDialog(selectedProcess);
            if (okClicked) {
                showProcessDetails(selectedProcess);
            }

        } else {
            // Ничего не выбрано.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Process Selected");
            alert.setContentText("Please select a process in the table.");

            alert.showAndWait();
        }
    }

    @FXML
    private void  handleCloseButton()
    {
        mainApp.closeApplication();
    }

    @FXML
    private void  handleMinimizeButton()
    {
        if (maxSize)
            mainApp.minimizeApplication();
        else mainApp.maximizeApplication();

        maxSize = !maxSize;
    }

    @FXML
    private void  handleHideButton()
    {
        mainApp.hideApplication();
    }
}