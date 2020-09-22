package sample.controllers;

import javafx.application.Platform;
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
    private Label subInfoLabel;
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

    public MainController() {
    }

    @FXML
    private void initialize() {
        // Инициализация таблицы адресатов с двумя столбцами.
        firstColumn.setCellValueFactory(cellData -> cellData.getValue().getProcessNameProperty());
        thirdColumn.setCellValueFactory(cellData -> cellData.getValue().getExePathProperty());

        showProcessDetails(null);

        processTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showProcessDetails(newValue));

    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        // Добавление в таблицу данных из наблюдаемого списка
        processTable.setItems(mainApp.getProcessEntryList());
    }

    private void showProcessDetails(ProcessEntry processEntry) {
        if (processEntry != null) {
            // Заполняем метки информацией из объекта person.
            parentIDLabel.setText(Integer.toString(processEntry.getParentProcessID()));
            pidLabel.setText(Integer.toString(processEntry.getProcessID()));
            typeLabel.setText("no");
            runEnvLabel.setText("no");
            depLabel.setText("no");
            subInfoLabel.setText("no");
            intLevelLabel.setText("no");
            privilegesLabel.setText(Integer.toString(processEntry.getBasePriority()));
            aclLabel.setText("no");
            fileOwnerLabel.setText("no");

        } else {

            parentIDLabel.setText("");
            pidLabel.setText("");
            typeLabel.setText("");
            runEnvLabel.setText("");
            depLabel.setText("");
            dllLibsLabel.setText("");
            subInfoLabel.setText("");
            intLevelLabel.setText("");
            privilegesLabel.setText("");
            aclLabel.setText("");
            fileOwnerLabel.setText("");
        }
    }

    /**
     * Вызывается, когда пользователь кликает по кнопка Edit...
     * Открывает диалоговое окно для изменения выбранного адресата.
     */
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
    private void  handleHideButton()
    {
        mainApp.hideApplication();
    }
}