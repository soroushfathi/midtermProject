package pages;

import elements.Loading;
import elements.LoadingType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import network.DataTransfer;
import network.Search;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static main.Config.PLAY_TYPE;

public class PlayType extends GridPane {
    public PlayType() {
        setPadding(new Insets(10, 10, 10, 10));
        setVgap(5);
        setHgap(5);
        getStylesheets().add("pages/Style.css");
        getStyleClass().add("play-type");

        Button local = new Button("Play on LOCAL");
        setConstraints(local, 18, 20);
        local.setPrefWidth(200);
        getChildren().add(local);


        Button network = new Button("Search for NETWORK");
        setConstraints(network, 18, 21);
        network.setPrefWidth(200);
        getChildren().add(network);

        ListView<String> list = new ListView<>();
        setConstraints(list, 18, 22);
        list.setPrefSize(200, 200);
        getChildren().add(list);

//        local.setOnMouseEntered(e->playSound("src/assets/hover.mp3"));
//        network.setOnMouseEntered(e->playSound("src/assets/hover.mp3"));

        local.setOnAction(e -> {
//            playSound("src/assets/click.mp3");
            PrepareBoard board = new PrepareBoard();
            Stage stage = new Stage();
            stage.setTitle("Game");
            stage.setScene(new Scene(board.build()));
            stage.show();

            ((Node) (e.getSource())).getScene().getWindow().hide();
        });
        network.setOnAction(e -> {
//            playSound("src/assets/click.mp3");
            PLAY_TYPE = main.PlayType.NETWORK;
            Search task = new Search();
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(task);

            Loading l = new Loading(LoadingType.SEARCH,"Search for servers");

            task.setOnRunning(__->l.show());
            task.setOnSucceeded(__ -> {
                l.close();
                executorService.shutdown();
                var servers = Search.servers;
                ObservableList<String> items = FXCollections.observableArrayList();

                for (var server : servers)
                    items.add(server.getName() + ":" + server.getPort() + "   " + server.getPlayer() + "/" + server.getSize());
                list.setItems(items);
                list.setCellFactory((ListView<String> p) -> new ColoredCell());
            });
        });

    }

    public void playSound(String path) {
        Media media = new Media(new File(path).toURI().toString());
        MediaPlayer md = new MediaPlayer(media);
        md.play();
    }

    private static class ColoredCell extends ListCell<String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                boolean isActive = !item.substring(item.indexOf(" ") + 3, item.indexOf("/")).equals(item.substring(item.indexOf("/") + 1));
                setText(item);
                setTextFill(isActive ? Color.GREEN:Color.RED );
                setOnMouseClicked(e -> {
                    if (e.getButton().equals(MouseButton.PRIMARY) && isActive) {
                        if (e.getClickCount() == 2) {
                            for (var server : Search.servers) {

                                if (item.substring(0, item.indexOf(" ")).equals(server.getName() + ":" + server.getPort())) {
                                    if (server.getPassword().length() > 0) {
                                        System.out.println("hello");
                                        TextInputDialog dialog = new TextInputDialog();
                                        Optional<String> result;
                                        dialog.setTitle("Password");
                                        dialog.setHeaderText(null);
                                        dialog.setGraphic(null);
                                        dialog.initStyle(StageStyle.UNDECORATED);
                                        dialog.setContentText("Pleas enter password:");

                                        result = dialog.showAndWait();
                                        result.ifPresent(p -> {
                                            if (p.equals(server.getPassword())) {
                                                Thread t = new Thread(new DataTransfer(server, ((Node) (e.getSource())).getScene()));
                                                t.start();
                                            } else
                                                System.out.println("wrong");
                                        });
                                    } else {
                                        Thread t = new Thread(new DataTransfer(server, ((Node) (e.getSource())).getScene()));
                                        t.start();
                                    }


                                }
                            }

                        }
                    }
                });
            }
        }
    }
}
