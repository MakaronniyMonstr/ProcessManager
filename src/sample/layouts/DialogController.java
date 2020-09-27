package sample.layouts;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.utils.processinfoloader.ProcessInfoLoader;
import sample.utils.processinfoloader.PropertyProcessEntry;
import sample.utils.processinfoloader.UtilTask;

import java.util.ArrayList;
import java.util.List;

public class DialogController implements ProcessInfoLoader.OnUtilTaskCompletedListener{

    @FXML
    private TextField intLevelField;
    @FXML
    private TextField privilegesField;
    @FXML
    private TextField fileOwnerField;
    @FXML
    private TextField aclField;


    private Stage dialogStage;
    private PropertyProcessEntry processEntry;
    private boolean okClicked = false;
    private String startProcessPrivileges;

    public DialogController() {ProcessInfoLoader.getInstance().addOnUtilTaskCompletedListener(this);}


    @FXML
    private void initialize() {  }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    public void setProcessEntry(PropertyProcessEntry processEntry) {
        this.processEntry = processEntry;

        //fileOwnerField.setText(processEntry.getOwnerName());
        //aclField.setText(processEntry.getProcessName());
        ProcessInfoLoader.getInstance().runNewTask(
                new UtilTask(
                        UtilTask.GET_PROCESS_INTEGRITY_LEVEL,
                        processEntry.getProcessID()
                )
        );
        ProcessInfoLoader.getInstance().runNewTask(
                new UtilTask(
                        UtilTask.GET_PROCESS_PRIVILEGES,
                        processEntry.getProcessID()
                )
        );

    }


    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {

            ProcessInfoLoader.getInstance().runNewTask(
                    new UtilTask(
                            UtilTask.SET_PROCESS_INTEGRITY_LEVEL,
                            processEntry.getProcessID() + intLevelField.getText()
                    )
            );

            correctProcessPrivileges();

            /*ProcessInfoLoader.getInstance().runNewTask(
                    new UtilTask(
                            UtilTask.GET_MODULES_LIST,
                            processEntry.getProcessID()
                    )
            );*/

            okClicked = true;
            dialogStage.close();
        }
    }

    private void correctProcessPrivileges()
    {
        String privileges = privilegesField.getText();

        String arrPrivilegesOld[] = startProcessPrivileges.split(" ");
        String arrPrivilegesNew[] = privileges.split(" ");


        for (String tmp : arrPrivilegesOld)
        {
            if (!privileges.contains(tmp))
            {
                ProcessInfoLoader.getInstance().runNewTask(
                        new UtilTask(
                                UtilTask.SET_PROCESS_PRIVILEGES,
                                processEntry.getProcessID() + " " + tmp + "false"
                        )
                );
            }
        }

        for (String tmp : arrPrivilegesNew)
        {
            if (!startProcessPrivileges.contains(tmp))
            {
                ProcessInfoLoader.getInstance().runNewTask(
                        new UtilTask(
                                UtilTask.SET_PROCESS_PRIVILEGES,
                                processEntry.getProcessID() + " " + tmp + "true"
                        )
                );
            }
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }


    private boolean isInputValid() {

        String errorMessage = "";

        //if (fileOwnerField.getText() == null || fileOwnerField.getText().length() == 0) {
        //    errorMessage += "No valid file owner name!\n";
        //}
        //if (aclField.getText() == null || aclField.getText().length() == 0) {
        //    errorMessage += "No valid ACL list!\n";
        //}

        String intLevel = intLevelField.getText();
        if (intLevel == null || intLevel.length() == 0) {
            errorMessage += "Invalid level!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid data!");
            alert.setHeaderText("Please enter valid data!");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }

    @Override
    public void onTaskCompleted(UtilTask task) {

        Platform.runLater(() -> {
            if (task.getCommand() == UtilTask.GET_PROCESS_INTEGRITY_LEVEL && task.getStringData() != null)
                intLevelField.setText(task.getStringData());
            if (task.getCommand() == UtilTask.GET_PROCESS_PRIVILEGES && task.getStringData() != null) {
                privilegesField.setText(task.getStringData());
                startProcessPrivileges = privilegesField.getText();
            }
        });
    }
}
