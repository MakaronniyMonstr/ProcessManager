package sample;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
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

import java.io.IOException;
import java.util.List;

public class Main extends Application implements ProcessInfoLoader.OnProcessesInfoUpdatedListener {

    ProcessInfoLoader loader;
    private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<ProcessEntry> processEntryList = FXCollections.observableArrayList();
    private double xOffset;
    private double yOffset;

    public Main()
    {
        this.loader = ProcessInfoLoader.getInstance();
        this.loader.setOnProcessesUpdatedListener(this);
        this.loader.runService();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Process Manager");
        this.primaryStage.getIcons().add(new Image("sample/resources/main_icon.png"));
        this.primaryStage.initStyle(StageStyle.UNDECORATED);

        initRootLayout();
        showProcessOverview();
    }

    public void initRootLayout()
    {
        try {
            // Загружаем корневой макет из fxml файла.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("resources/root_layout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(rootLayout, 1100, 700);

            scene.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = primaryStage.getX() - event.getScreenX();
                    yOffset = primaryStage.getY() - event.getScreenY();
                }
            });

            scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    primaryStage.setX(event.getScreenX() + xOffset);
                    primaryStage.setY(event.getScreenY() + yOffset);
                }
            });

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
            loader.setLocation(Main.class.getResource("resources/main_layout.fxml"));
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
            loader.setLocation(Main.class.getResource("resources/process_edit_layout.fxml"));
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

    public void  closeApplication()
    {
        loader.destroy();
        Platform.exit();
        System.exit(0);
    }

    public void hideApplication()
    {
        primaryStage.setIconified(true);
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
    public void onProcessesInfoLoaded(List<ProcessEntry> processesList) {
        if (processesList.size() > 0) {
            for (int i = 0; i < processesList.size(); i++) {
                if (i >= processEntryList.size()) {
                    processEntryList.add(processesList.get(i));
                } else {
                    processEntryList.get(i).update(processesList.get(i));
                }
            }

            if (processesList.size() < processEntryList.size()) {
                processEntryList.remove(processesList.size());
            }
        }
        else {
            processEntryList.addAll(processesList);
        }
    }
}