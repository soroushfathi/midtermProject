package elements;

import javafx.scene.layout.StackPane;
import pages.Board;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import main.Move;

import static main.Config.*;

public class Safe extends StackPane {
    public Safe(int x, int y) {
        move(x, y);
        Rectangle r = new Rectangle(TILE_SIZE, TILE_SIZE);
        r.setOpacity(SAFE_OPACITY);
        r.setFill(Color.valueOf(SAFE_COLOR));

        var dy=100+(550-HEIGHT * TILE_SIZE)/2;
        var dx=110+(450- WIDTH* TILE_SIZE)/2;

        getChildren().addAll(r);
        setVisible(false);

        setOnDragOver(e -> {
            e.acceptTransferModes(TransferMode.ANY);
            var data = e.getDragboard().getString();
            var x1 = Integer.parseInt(data.substring(0, data.indexOf("|")));
            var y1 = Integer.parseInt(data.substring(data.indexOf("|") + 1));
            int x2 = (int)( e.getSceneX()-dx) / TILE_SIZE;
            int y2 = (int) (e.getSceneY()-dy) / TILE_SIZE;
            if (Board.safeMargin[x2][y2].isVisible())
                Board.getBoard()[x1][y1].getElement().relocate(x2 * TILE_SIZE, y2 * TILE_SIZE);

        });

        setOnDragDropped(e -> {
            var data = e.getDragboard().getString();
            var x1 = Integer.parseInt(data.substring(0, data.indexOf("|")));
            var y1 = Integer.parseInt(data.substring(data.indexOf("|") + 1));

            int x2 = (int) ( e.getSceneX()-dx)  / TILE_SIZE;
            int y2 = (int) ( e.getSceneY()-dy)  / TILE_SIZE;
            if (Board.safeMargin[x2][y2].isVisible()) {


                Move.set(Board.getBoard(), x1, y1, x2, y2);

                for (int i = 0; i < WIDTH; i++)
                    Board.safeMargin[i][y1].setVisible(false);

                for (int j = 0; j < HEIGHT; j++)
                    Board.safeMargin[x1][j].setVisible(false);
            }
        });
    }

    public void move(int x, int y) {
        relocate(x * TILE_SIZE, y * TILE_SIZE);
    }

}
