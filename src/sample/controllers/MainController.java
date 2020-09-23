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
import sample.utils.processloader.PropertyProcessEntry;


public class MainController {
    @FXML
    private TableView<PropertyProcessEntry> processTable;
    @FXML
    private TableColumn<PropertyProcessEntry, String> firstColumn;
    @FXML
    private TableColumn<PropertyProcessEntry, String> thirdColumn;

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
        firstColumn.setCellValueFactory(cellData -> cellData.getValue().processNameProperty());
        thirdColumn.setCellValueFactory(cellData -> cellData.getValue().exePathProperty());

        showProcessDetails(null);

        processTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showProcessDetails(newValue));
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        // Добавление в таблицу данных из наблюдаемого списка
        processTable.setItems(mainApp.getProcessEntryList());
    }

    private void showProcessDetails(PropertyProcessEntry processEntry) {
            if (processEntry != null) {
                parentIDLabel.textProperty().bind(processEntry.parentProcessIDProperty());
                pidLabel.textProperty().bind(processEntry.processIDProperty());
                typeLabel.textProperty().bind(processEntry.processTypeProperty());
                runEnvLabel.textProperty().bind(processEntry.runtimeProperty());
                depLabel.textProperty().bind(processEntry.spaceLayoutProperty());
                sidLabel.textProperty().bind(processEntry.SIDProperty());
                fileOwnerLabel.textProperty().bind(processEntry.ownerNameProperty());
                intLevelLabel.setText("no");
                privilegesLabel.setText("no");
                aclLabel.setText("no");
                parentNameLabel.setText("no");
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
    }

    @FXML
    private void handleEditPerson() {
        PropertyProcessEntry selectedProcess = processTable.getSelectionModel().getSelectedItem();
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