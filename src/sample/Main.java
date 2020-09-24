package sample;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.layouts.DialogController;
import sample.layouts.MainController;
import sample.utils.processinfoloader.ProcessEntry;
import sample.utils.processinfoloader.ProcessInfoLoader;
import sample.utils.processinfoloader.PropertyProcessEntry;

import java.io.IOException;
import java.util.List;

public class Main extends Application implements ProcessInfoLoader.OnProcessesInfoUpdatedListener {

    private Stage primaryStage;
    private AnchorPane rootLayout;
    private int STAGE_WIDTH = 1200;
    private int STAGE_HEIGHT = 800;
    private ObservableList<PropertyProcessEntry> processEntryList;
    private double xOffset;
    private double yOffset;

    public Main()
    {
        processEntryList = FXCollections.observableArrayList();

        ProcessInfoLoader loader = ProcessInfoLoader.getInstance();

        // If procapi.exe doesn't exist
        if (!loader.isProcApiInstalled()) {
            closeApplication();
        }

        loader.setOnProcessesUpdatedListener(this);
        loader.runService();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Process Manager");
        this.primaryStage.getIcons().add(new Image("sample/resources/main_icon.png"));
        this.primaryStage.initStyle(StageStyle.UNDECORATED);
        this.primaryStage.initStyle(StageStyle.TRANSPARENT);
        this.primaryStage.setResizable(true);

        initRootLayout();
    }

    public void initRootLayout()
    {
        try {
            // Загружаем корневой макет из fxml файла.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("layouts/main_layout.fxml"));
            rootLayout = (AnchorPane) loader.load();

            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(rootLayout, STAGE_WIDTH, STAGE_HEIGHT);

            scene.setFill(Color.TRANSPARENT);

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

            MainController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showProcessEditDialog(PropertyProcessEntry processEntry) {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("layouts/process_edit_layout.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Создаём диалоговое окно Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Process");
            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.initStyle(StageStyle.TRANSPARENT);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            scene.setFill(Color.TRANSPARENT);
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

    public void closeApplication()
    {
        ProcessInfoLoader.getInstance().destroy();
        Platform.exit();
        System.exit(0);
    }

    public void minimizeApplication()
    {
        primaryStage.setMaximized(false);
    }

    public void maximizeApplication()
    {
        primaryStage.setMaximized(true);
    }

    public void hideApplication()
    {
        primaryStage.setIconified(true);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ObservableList<PropertyProcessEntry> getProcessEntryList() {
        return processEntryList;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void onProcessesInfoLoaded(List<ProcessEntry> processesList) {
        Platform.runLater(()->{
                for (int i = 0; i < processesList.size(); i++) {
                    if (i >= processEntryList.size()) {
                        processEntryList.add(new PropertyProcessEntry(processesList.get(i)));
                    } else {
                        processEntryList.get(i).update(processesList.get(i));
                    }
                }

                if (processesList.size() < processEntryList.size()) {
                    processEntryList.remove(processesList.size());
                }
        });
    }
}