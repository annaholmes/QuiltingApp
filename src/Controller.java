import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;

public class Controller extends Application {
    private HashGrid<Image> quiltData;
    private HashGrid<Color> blockData;

    @FXML
    public ColorPicker colorPicker;


    @FXML
    public ComboBox blockSelector;

    @FXML
    public GridPane quiltGUI;

    @FXML
    public GridPane blockGUI;

    private Set<String> icons;

    private HashGrid[] data;
    private GridPane[] gui;

    @FXML
    private Button load;

    @FXML
    private Button save;

    private FileChooser fileChooser = new FileChooser();
    private Stage stage;

    private int block;
    private int quilt;
    int qSize = 5;
    int bSize = 3;

    @Override
    public void start(final Stage stage) {
        this.stage = stage;
    }

    public void initialize() {
        block = 0;
        quilt = 1;
       // fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Quilt File (*.qlt)","*.qlt"));

        quiltData = new HashGrid<>(qSize, qSize);
        blockData = new HashGrid<>(bSize, bSize);
        gui = new GridPane[]{blockGUI,quiltGUI};
        data = new HashGrid[]{blockData, quiltData};

        initColorPick();
        initFiles();
        initBlockSelect();
        update();
    }


    public void initFiles() {
    /*    load.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        File file = fileChooser.showOpenDialog(stage);
                        if (file != null) {
                            loadQuiltFromFile(file);
                        }
                    }
                });
        save.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();

                //Set extension filter
              //  FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Quilt File (*.qlt)", ".qlt");
                //zfileChooser.getExtensionFilters().add(extFilter);

                //Show save file dialog
                File file = fileChooser.showSaveDialog(stage);

                if (file != null) {
                    writeToFile(file);
                }
            }
        });*/
    }

    public void initColorPick() {
        colorPicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                blockData.addData(colorPicker.getValue().toString().hashCode(), colorPicker.getValue());
            }
        });
    }

    public void initBlockSelect() {
        blockSelector.setCellFactory(listView -> new ListCell<String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    Image image = quiltData.getData(Integer.valueOf(item));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    setGraphic(imageView);
                }
            }
        });
        blockSelector.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                quiltData.setCurrentData(Integer.valueOf((String)blockSelector.getValue()));
            }
        });
        blockSelector.setButtonCell(new ButtonCell());

    }

    // https://stackoverflow.com/questions/32334137/javafx-choicebox-with-image-and-text
    //https://stackoverflow.com/questions/20604974/setbuttoncell-for-combobox/20621142
    private class ButtonCell extends ListCell<String>
    {
        @Override
        protected void updateItem( String item, boolean empty )
        {
            super.updateItem( item, empty );

            if ( item != null && ! empty )
            {
                Image image= quiltData.getData(Integer.valueOf(item));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                setGraphic(imageView);
            }
        }
    }


    public void update() {
        for (int i = 0; i < gui.length; i++) {
            gui[i].getChildren().clear();
            for (int w = 0; w < data[i].getWidth(); w++) {
                for (int h = 0; h < data[i].getHeight(); h++) {
                    StackPane child = new StackPane();
                    if (i == block) {
                        if (data[i].getBlockIntData(w,h) != 0) {
                            child = makeBlockItem(child, (Color) data[i].getBlockData(w, h));
                        } else {
                            child = makeBlockItem(child, Color.WHITE);
                        }
                    } else {
                        if (data[i].getBlockIntData(w, h) != 0) {
                            child = makeQuiltItem(child, (Image) data[i].getBlockData(w, h));
                        } else {
                            child = makeBlockItem(child, Color.WHITE);
                        }
                    }
                    StackPane finalChild = child;
                    if (i == block) {
                        child.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                changeData(block, GridPane.getColumnIndex(finalChild), GridPane.getRowIndex(finalChild));
                            }
                        });
                    } else {
                        child.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                changeData(quilt, GridPane.getColumnIndex(finalChild), GridPane.getRowIndex(finalChild));
                            }
                        });
                    }

                    GridPane.setColumnIndex(finalChild, w);
                    GridPane.setRowIndex(finalChild, h);
                    gui[i].add(finalChild, w, h);
                }
            }
        }

    }

    public StackPane makeBlockItem(StackPane child, Color color) {
        Rectangle r = new Rectangle();
        r.setFill(color);
        r.setStroke(Color.BLACK);
        r.widthProperty().bind(child.widthProperty());
        r.heightProperty().bind(child.heightProperty());
        child.getChildren().add(r);

        return child;
    }

    public StackPane makeQuiltItem(StackPane child, Image img) {
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(125);
        imageView.setFitHeight(128);
        child.getChildren().add(imageView);
        return child;
    }

    public void changeData(int grid_i, int width, int height) {
        data[grid_i].setGridInt(width, height);
        update();
    }

    public void saveBlock() {
        if (data[quilt].getData(data[block].getGridCode()) == null) {
            gui[block].setGridLinesVisible(false);
            Image snapshot = gui[block].snapshot(new SnapshotParameters(), null);
            gui[block].setGridLinesVisible(true);
            data[quilt].addData(data[block].getGridCode(), snapshot);
            blockSelector.getItems().add(String.valueOf(data[block].getGridCode()));
        }
    }

    @FXML
    public void clearBlock() {
        // ?
        this.blockData = new HashGrid<>(bSize, bSize);
        this.data[block] = blockData;
        update();
    }

    /*public void writeToFile(File f) {
        System.out.println("hello "  + f);
     //   if (f.getName().split(".").equals("qlt")) {
        try {

            //Saving of object in a file

            FileOutputStream file = new FileOutputStream(f);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            out.writeObject(this.quiltData);
            out.close();
            file.close();

            System.out.println("Object has been serialized");

        } catch (IOException ex) {
            System.out.println("IO Exception: " + ex.getMessage());
        }
    }







    public void loadQuiltFromFile(File f) {
        System.out.println("Hello " + f.toString());
//        if (!f.toString().split(".")[1].equals("qlt")) {
//            System.out.println("Hello..... " + f.toString());
//
//        }
//        else {
            try {
                // Reading the object from a file
                FileInputStream file = new FileInputStream(f);
                ObjectInputStream in = new ObjectInputStream(file);

                this.quiltData = (HashGrid<Image>) in.readObject();
                this.data[quilt] = quiltData;

                in.close();
                file.close();

            } catch (IOException ex) {
                System.out.println("IOException Caught");
            } catch (ClassNotFoundException ex) {
                System.out.println("ClassNotFoundException Caught");
            }
            update();
//        }

    }*/

}
