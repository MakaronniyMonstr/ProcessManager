package sample;

import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.controllers.DialogController;
import sample.controllers.MainController;
import sample.utils.processloader.ProcessEntry;
import sample.utils.processloader.ProcessInfoLoader;
import sample.utils.processloader.ProcessModifyTask;

import java.io.IOException;
import java.util.List;

public class Main extends Application implements ProcessInfoLoader.OnProcessesInfoUpdatedListener {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<ProcessEntry> processEntryList = FXCollections.observableArrayList();

    public Main()
    {
        ProcessInfoLoader loader = ProcessInfoLoader.getInstance();

        loader.setOnProcessesUpdatedListener(this);

        processEntryList.add(new ProcessEntry(
                "test1", 1, "path", 1,
                "owner", "domain", "SID",
                "64x", "Native", "ASLR",
                10, 2));
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Process Manager");
        this.primaryStage.getIcons().add(new Image("sample/view/main_icon.png"));

        initRootLayout();
        showProcessOverview();
    }

    public void initRootLayout()
    {
        //primaryStage.setScene(new Scene(root, 1100, 700));

        try {
            // Загружаем корневой макет из fxml файла.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("root_layout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(rootLayout, 1100, 700);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showProcessOverview() {
        try {
            // Загружаем сведения об адресатах.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("sample.fxml"));
            AnchorPane processOverview = (AnchorPane) loader.load();

            // Помещаем сведения об адресатах в центр корневого макета.
            rootLayout.setCenter(processOverview);

            // Даём контроллеру доступ к главному приложению.
            MainController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showProcessEditDialog(ProcessEntry processEntry) {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("process_edit_layout.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Создаём диалоговое окно Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Process");
            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Передаём адресата в контроллер.
            DialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setProcessEntry(processEntry);

            // Отображаем диалоговое окно и ждём, пока пользователь его не закроет
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ObservableList<ProcessEntry> getProcessEntryList() {
        return processEntryList;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void onProcessesInfoLoaded(List<ProcessModifyTask> processModifyTasks) {
        System.out.println("Hello");
    }
}