package connectX;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;

public final class Constants
{
    static final String projectName = "Camelot";
    static double width;
    static double height;
    static final int rows = 16;
    static final int col = 12;
    static boolean low;
    static final FileFilter cFileFilter;
    static final FileFilter cppFileFilter;
    static final FileFilter outFileFilter;
    static final FileFilter exeFileFilter;
    static final FileFilter javaFileFilter;
    static final FileFilter classFileFilter;
    static final FileFilter pythonFileFilter;

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
        BLACK,
        WHITE;
    }

    public enum State
    {
        EMPTY(0),
        WHITEMEN(1),
        WHITEHORSE(2),
        WHITECASTLE(3),
        BLACKMEN(4),
        BLACKHORSE(5),
        BLACKCASTLE(6),
        BLOCKED(-1);

        private int value;

        private State(final int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum MoveType
    {
        PLAIN,
        SACRIFICE,
        INVALID;
    }
}

