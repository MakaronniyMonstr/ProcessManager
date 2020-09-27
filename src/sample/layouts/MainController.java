package sample.layouts;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sample.Main;
import sample.utils.processinfoloader.ProcessEntry;
import sample.utils.processinfoloader.ProcessInfoLoader;
import sample.utils.processinfoloader.PropertyProcessEntry;
import sample.utils.processinfoloader.UtilTask;

import java.io.IOException;
import java.util.Optional;


public class MainController implements ProcessInfoLoader.OnUtilTaskCompletedListener {
    @FXML
    private TableView<PropertyProcessEntry> processTable;
    @FXML
    private TableColumn<PropertyProcessEntry, String> firstColumn;
    @FXML
    private TableColumn<PropertyProcessEntry, String> thirdColumn;

    @FXML
    private TextField parentIDField;
    @FXML
    private TextField parentNameField;
    @FXML
    private TextField pidField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField runEnvField;
    @FXML
    private TextField depField;
    @FXML
    private TextField sidField;
    @FXML
    private TextField intLevelField;
    @FXML
    private TextField fileOwnerField;
    @FXML
    private TextField dllLibsField;
    @FXML
    private TextField privilegesField;
    @FXML
    private TextField aclField;

    private int lastIndex= 0;
    private PropertyProcessEntry selectedItem = null;
    // Ссылка на главное приложение.
    private Main mainApp;

    private boolean maxSize = false;

    public MainController() {
        ProcessInfoLoader.getInstance().addOnUtilTaskCompletedListener(this);
    }

    @FXML
    private void initialize() throws IOException {
        // Инициализация таблицы адресатов с двумя столбцами.
        firstColumn.setCellValueFactory(cellData -> cellData.getValue().processNameProperty());
        thirdColumn.setCellValueFactory(cellData -> cellData.getValue().exePathProperty());

        showProcessDetails(null);

        processTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    try {
                        selectedItem = new PropertyProcessEntry(newValue);
                        showProcessDetails(newValue);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        // Добавление в таблицу данных из наблюдаемого списка
        processTable.setItems(mainApp.getProcessEntryList());
    }

    private void showProcessDetails(PropertyProcessEntry processEntry) throws IOException {
            if (processEntry != null) {
                parentIDField.textProperty().bind(processEntry.parentProcessIDProperty());
                parentNameField.setText(findParentName(processEntry));
                pidField.textProperty().bind(processEntry.processIDProperty());
                typeField.textProperty().bind(processEntry.processTypeProperty());
                runEnvField.textProperty().bind(processEntry.runtimeProperty());
                depField.textProperty().bind(processEntry.spaceLayoutProperty());
                sidField.textProperty().bind(processEntry.SIDProperty());
                fileOwnerField.textProperty().bind(processEntry.ownerNameProperty());
                aclField.setText("");

                ProcessInfoLoader.getInstance().runNewTask(
                        new UtilTask(
                                UtilTask.GET_PROCESS_INTEGRITY_LEVEL,
                                processEntry.getProcessID()
                        )
                );
                ProcessInfoLoader.getInstance().runNewTask
                        (new UtilTask(
                                UtilTask.GET_PROCESS_PRIVILEGES,
                                processEntry.getProcessID()
                        )
                );
                ProcessInfoLoader.getInstance().runNewTask(
                        new UtilTask(
                                UtilTask.GET_MODULES_LIST,
                                processEntry.getProcessID()
                        )
                );

            } else {
                parentIDField.setText("Привет вероника!");
                parentNameField.setText("Как дела?");
                pidField.setText("Как жизнь?");
                typeField.setText("");
                runEnvField.setText("");
                depField.setText("");
                dllLibsField.setText("");
                sidField.setText("");
                intLevelField.setText("");
                privilegesField.setText("");
                fileOwnerField.setText("");
                aclField.setText("");
            }
    }

    private String findParentName(PropertyProcessEntry childProcess)
    {
        ObservableList<PropertyProcessEntry> processList = mainApp.getProcessEntryList();
        for (PropertyProcessEntry tmp : processList)
        {
            if (tmp.getProcessID().equals(childProcess.getParentProcessID()))
                return tmp.getProcessName();
        }

        return "UNKNOWN";
    }

    @FXML
    private void handleEditPerson() {
        PropertyProcessEntry selectedProcess = processTable.getSelectionModel().getSelectedItem();

        if (selectedProcess != null) {
            boolean okClicked = mainApp.showProcessEditDialog(selectedProcess);
            if (okClicked) {
                try {
                    showProcessDetails(selectedProcess);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    @Override
    public void onTaskCompleted(UtilTask task) {
        Platform.runLater(() -> {
            if (task.getCommand() == UtilTask.GET_PROCESS_INTEGRITY_LEVEL && task.getStringData() != null)
                intLevelField.setText(task.getStringData());

            if (task.getCommand() == UtilTask.GET_PROCESS_PRIVILEGES && task.getStringData() != null)
                privilegesField.setText(task.getStringData());

            if (task.getCommand() == UtilTask.GET_MODULES_LIST && task.getStringData() != null)
                dllLibsField.setText(task.getStringData());
        });
    }

    public TableView<PropertyProcessEntry> getProcessTable() {
        return processTable;
    }

    public void checkSelectedItemIsCorrect() {
        processTable.sort();

        if (selectedItem != null) {
            int pos = processTable.getItems().indexOf(selectedItem);

            if (pos >= 0) {
                processTable.getSelectionModel().select(pos);
                lastIndex = pos;
            }
            else {
                processTable.getSelectionModel().select(pos);
            }
        }
    }
}