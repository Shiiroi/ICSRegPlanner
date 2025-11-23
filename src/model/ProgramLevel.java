package model;

public class ProgramLevel {
    public static final int LEVEL_BS = 1;
    public static final int LEVEL_MS = 2;
    public static final int LEVEL_MIT = 2;
    public static final int LEVEL_PHD = 3;

    public static int getProgramLevel(String program) {
        if (program.equals("BS Computer Science")) {
            return LEVEL_BS;
        } else if (program.equals("MS Computer Science")) {
            return LEVEL_MS;
        } else if (program.equals("Master of Information Technology")) {
            return LEVEL_MIT;
        } else if (program.equals("PhD Computer Science")) {
            return LEVEL_PHD;
        }
        return 0;
    }

    public static int getCourseLevel(String courseCode) {
        String numPart = courseCode.replaceAll("[^0-9]", "");

        if (numPart.isEmpty()) {
            return 0;
        }

        int courseNum = Integer.parseInt(numPart);

        if (courseNum <= 200) {
            return LEVEL_BS;
        } else if (courseNum <= 300) {
            return LEVEL_MS;
        } else {
            return LEVEL_PHD;
        }
    }
}
