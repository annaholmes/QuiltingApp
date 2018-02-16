import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class HashGrid<E> implements Serializable {

    private ArrayList<ArrayList<Integer>> grid;
    private HashMap<Integer, E> data;
    private int gridCode;

    private int currentCode;

    private int width;
    private int height;


    public HashGrid(int width, int height) {
        grid = new ArrayList<>();
        data = new HashMap<>();
        this.width = width;
        this.height = height;
        for (int w = 0; w < width; w++) {
            grid.add(new ArrayList<>());
            for (int h = 0; h < height; h++) {
                grid.get(w).add(0);
            }
        }

    }

    public int getWidth() {return this.width;}

    public int getHeight() {return this.height;}

    public E getBlockData(int width, int height) {
        return data.get(getBlockIntData(width, height));
    }

    public int getBlockIntData(int width, int height) throws ArrayIndexOutOfBoundsException {
        if (width < this.width && height < this.height) {
            return grid.get(width).get(height);
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public void setGridInt(int width, int height) throws ArrayIndexOutOfBoundsException {
        int code = currentCode;
        if (width < this.width && height < this.height) {
            grid.get(width).set(height, code);
        }
        else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public E getData(int s) {
        return data.get(s);
    }

    public void addData(int s, E data) {
        this.data.put(s, data);
        setCurrentData(s);

    }

    public void setCurrentData(int s) {
        this.currentCode = s;
    }


    public int getGridCode() {
        updateCode();

        return this.gridCode;
    }

    public void updateCode() {
        StringBuilder b = new StringBuilder();
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                b.append(getBlockIntData(w, h));
            }
        }
        this.gridCode = b.toString().hashCode();
    }

}
