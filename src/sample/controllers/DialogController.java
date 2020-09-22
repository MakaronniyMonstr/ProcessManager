package sample.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.utils.processloader.ProcessEntry;

public class DialogController {

    @FXML
    private TextField intLevelField;
    @FXML
    private TextField fileOwnerField;
    @FXML
    private TextField aclField;


    private Stage dialogStage;
    private ProcessEntry processEntry;
    private boolean okClicked = false;


    @FXML
    private void initialize() {
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    public void setProcessEntry(ProcessEntry processEntry) {
        this.processEntry = processEntry;

        //поменять на то, что должно быть
        intLevelField.setText(Integer.toString(processEntry.getBasePriority()));
        fileOwnerField.setText(processEntry.getOwnerName());
        aclField.setText(processEntry.getProcessName());
    }


    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {

            //поменять на то, что должно быть
            processEntry.setBasePriority(Integer.parseInt(intLevelField.getText()));
            processEntry.setExePath(fileOwnerField.getText());
            processEntry.setProcessName(aclField.getText());

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

        if (fileOwnerField.getText() == null || fileOwnerField.getText().length() == 0) {
            errorMessage += "No valid file owner name!\n";
        }
        if (aclField.getText() == null || aclField.getText().length() == 0) {
            errorMessage += "No valid ACL list!\n";
        }

        if (intLevelField.getText() == null || intLevelField.getText().length() == 0) {
            errorMessage += "No valid postal code!\n";
        } else {
            // пытаемся преобразовать почтовый код в int.
            try {
                Integer.parseInt(intLevelField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid postal code (must be an integer)!\n";
            }
        }


        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Показываем сообщение об ошибке.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
}
