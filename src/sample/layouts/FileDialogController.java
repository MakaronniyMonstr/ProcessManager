package sample.layouts;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.utils.processinfoloader.ProcessInfoLoader;
import sample.utils.processinfoloader.PropertyProcessEntry;
import sample.utils.processinfoloader.UtilTask;

public class FileDialogController implements ProcessInfoLoader.OnUtilTaskCompletedListener{

    @FXML
    private TextField pathField;
    @FXML
    private TextField intLevelField;
    @FXML
    private TextField fileOwnerField;
    @FXML
    private TextField aclField;


    private Stage dialogStage;
    private PropertyProcessEntry processEntry;
    private boolean okClicked = false;

    public FileDialogController() {ProcessInfoLoader.getInstance().addOnUtilTaskCompletedListener(this);}


    @FXML
    private void initialize() {  }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleIntLevelButton() {
        if (isInputValid()) {
            ProcessInfoLoader.getInstance().runNewTask(
                    new UtilTask(
                            UtilTask.SET_FILE_INTEGRITY_LEVEL,
                            pathField.getText() + " " + intLevelField.getText()
                    )
            );
        }
    }

    @FXML
    private void handleFileOwnerButton() {
        if (isInputValid()) {
            ProcessInfoLoader.getInstance().runNewTask(
                    new UtilTask(
                            UtilTask.SET_FILE_OWNER,
                            fileOwnerField.getText() + " " + fileOwnerField.getText()
                    )
            );
        }
    }

    @FXML
    private void handleAclButton() {
        if (isInputValid()) {
            ProcessInfoLoader.getInstance().runNewTask(
                    new UtilTask(
                            UtilTask.SET_FILE_ACL,
                            aclField.getText() + " " + aclField.getText()
                    )
            );
        }
    }


    @FXML
    private void handleCancel() {
        dialogStage.close();
    }


    private boolean isInputValid() {

        String errorMessage = "";


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
            /*if (task.getCommand() == UtilTask.GET_FILE_INTEGRITY_LEVEL && task.getStringData() != null)
                intLevelField.setText(task.getStringData());
            if (task.getCommand() == UtilTask.GET_FILE_OWNER && task.getStringData() != null)
                fileOwnerField.setText(task.getStringData());
            if (task.getCommand() == UtilTask.GET_FILE_ACL && task.getStringData() != null)
                aclField.setText(task.getStringData());*/
        });
    }
}
