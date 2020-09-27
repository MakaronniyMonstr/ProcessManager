package sample.layouts;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.utils.processinfoloader.ProcessInfoLoader;
import sample.utils.processinfoloader.PropertyProcessEntry;
import sample.utils.processinfoloader.UtilTask;

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

    public DialogController() {ProcessInfoLoader.getInstance().addOnUtilTaskCompletedListener(this);}


    @FXML
    private void initialize() {
    }

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
                            processEntry.getProcessID()
                    )
            );
            ProcessInfoLoader.getInstance().runNewTask
                    (new UtilTask(
                                    UtilTask.SET_PROCESS_PRIVILEGES,
                                    processEntry.getProcessID()
                            )
                    );
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
        if (intLevel == null || intLevel.length() == 0
                             && (intLevel != "High" && intLevel != "Medium" && intLevel != "Untrusted")) {
            errorMessage += "No valid level!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
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
            if (task.getCommand() == UtilTask.GET_PROCESS_PRIVILEGES && task.getStringData() != null)
                privilegesField.setText(task.getStringData());
        });
    }
}
