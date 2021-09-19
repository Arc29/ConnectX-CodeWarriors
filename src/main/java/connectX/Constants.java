package connectX;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;

public final class Constants
{
    static final String projectName = "ConnectX";
    static double width;
    static double height;
    static final int rows = 9;
    static final int col = 9;
    static boolean low;
    static final FileFilter cFileFilter;
    static final FileFilter cppFileFilter;
    static final FileFilter outFileFilter;
    static final FileFilter exeFileFilter;
    static final FileFilter javaFileFilter;
    static final FileFilter classFileFilter;
    static final FileFilter pythonFileFilter;
    static final int[][] projectFrom={
            {1,2,3,1,2,3,1,2,3},
            {4,5,6,4,5,6,4,5,6},
            {7,8,9,7,8,9,7,8,9},
            {1,2,3,1,2,3,1,2,3},
            {4,5,6,4,5,6,4,5,6},
            {7,8,9,7,8,9,7,8,9},
            {1,2,3,1,2,3,1,2,3},
            {4,5,6,4,5,6,4,5,6},
            {7,8,9,7,8,9,7,8,9}
    } ;
    static final int[][] projectTo={
            {1,1,1,2,2,2,3,3,3},
            {1,1,1,2,2,2,3,3,3},
            {1,1,1,2,2,2,3,3,3},
            {4,4,4,5,5,5,6,6,6},
            {4,4,4,5,5,5,6,6,6},
            {4,4,4,5,5,5,6,6,6},
            {7,7,7,8,8,8,9,9,9},
            {7,7,7,8,8,8,9,9,9},
            {7,7,7,8,8,8,9,9,9}
    };

    static {
        Constants.width = 1920.0;
        Constants.height = 1040.0;
        Constants.low = false;
        cFileFilter = new FileNameExtensionFilter(null, new String[] { "c" });
        cppFileFilter = new FileNameExtensionFilter(null, new String[] { "cpp" });
        outFileFilter = new FileNameExtensionFilter(null, new String[] { "out" });
        exeFileFilter = new FileNameExtensionFilter(null, new String[] { "exe" });
        javaFileFilter = new FileNameExtensionFilter(null, new String[] { "java" });
        classFileFilter = new FileNameExtensionFilter(null, new String[] { "class" });
        pythonFileFilter = new FileNameExtensionFilter(null, new String[] { "py" });
    }

    public enum Player
    {
        P1(1),
        P2(2);

        private int value;

        private Player(final int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum State
    {
        EMPTY(0),
        P1(1),
        P2(2);


        private int value;

        private State(final int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

}

