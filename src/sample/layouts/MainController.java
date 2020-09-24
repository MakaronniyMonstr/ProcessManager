package sample.layouts;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sample.Main;
import sample.utils.processloader.ProcessInfoLoader;
import sample.utils.processloader.PropertyProcessEntry;
import sample.utils.processloader.UtilTask;

import java.io.IOException;


public class MainController implements ProcessInfoLoader.OnUtilTaskCompletedListener{
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
    private TextField dllLibsField;
    @FXML
    private Label sidLabel;
    @FXML
    private Label intLevelLabel;
    @FXML
    private Label privilegesLabel;
    @FXML
    private Label fileOwnerLabel;

    // Ссылка на главное приложение.
    private Main mainApp;

    private boolean maxSize = false;

    public MainController() { ProcessInfoLoader.getInstance().setOnUtilTaskCompletedListener(this); }

    @FXML
    private void initialize() throws IOException {
        // Инициализация таблицы адресатов с двумя столбцами.
        firstColumn.setCellValueFactory(cellData -> cellData.getValue().processNameProperty());
        thirdColumn.setCellValueFactory(cellData -> cellData.getValue().exePathProperty());


        showProcessDetails(null);

        processTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    try {
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

                parentIDLabel.textProperty().bind(processEntry.parentProcessIDProperty());
                parentNameLabel.setText(findParentName(processEntry));//how to bind
                pidLabel.textProperty().bind(processEntry.processIDProperty());
                typeLabel.textProperty().bind(processEntry.processTypeProperty());
                runEnvLabel.textProperty().bind(processEntry.runtimeProperty());
                depLabel.textProperty().bind(processEntry.spaceLayoutProperty());
                sidLabel.textProperty().bind(processEntry.SIDProperty());
                fileOwnerLabel.textProperty().bind(processEntry.ownerNameProperty());

                UtilTask utilTask = new UtilTask(UtilTask.GET_PROCESS_INTEGRITY_LEVEL, processEntry.getProcessID());
                ProcessInfoLoader.getInstance().runNewTask(utilTask);
                /*utilTask.setCommand(UtilTask.GET_PROCESS_PRIVILEGES);
                ProcessInfoLoader.getInstance().runNewTask(utilTask);
                utilTask.setCommand(UtilTask.GET_MODULES_LIST);
                ProcessInfoLoader.getInstance().runNewTask(utilTask);*/
                //intLevelLabel.setText("");
                //privilegesLabel.setText("");


            } else {

                parentIDLabel.setText("");
                parentNameLabel.setText("");
                pidLabel.setText("");
                typeLabel.setText("");
                runEnvLabel.setText("");
                depLabel.setText("");
                dllLibsField.setText("");
                sidLabel.setText("");
                intLevelLabel.setText("");
                privilegesLabel.setText("");
                fileOwnerLabel.setText("");

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
        Platform.runLater(()->{

            if (task.getCommand() == UtilTask.GET_PROCESS_INTEGRITY_LEVEL)
                intLevelLabel.setText(task.getStringData());
            //else if (task.getCommand() == UtilTask.GET_PROCESS_INTEGRITY_LEVEL)
                //intLevelLabel.setText(task.getStringData());
            //ProcessEntry = Runtime.getRuntime().exec("procapi.exe " + utilTask.getStringCommand());
            //intLevelLabel.setText(String.valueOf(Runtime.getRuntime().exec("procapi.exe " + utilTask.getStringCommand())));
            //privilegesLabel.setText("no");//Runtime.getRuntime().exec();
            //aclLabel.setText("no");//Runtime.getRuntime().exec();
        });
    }
}