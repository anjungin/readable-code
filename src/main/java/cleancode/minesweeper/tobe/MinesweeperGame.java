package cleancode.minesweeper.tobe;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class MinesweeperGame {

    public static final Scanner SCANNER = new Scanner(System.in);
    public static int BOARD_ROW_SIZE = 8;
    public static int BOARD_COL_SIZE = 10;
    public static String CLOSE_CELL_SIGN = "□";
    private static final String[][] BOARD = new String[BOARD_ROW_SIZE][BOARD_COL_SIZE];
    private static final Integer[][] LAND_MINE_COUNTS = new Integer[BOARD_ROW_SIZE][BOARD_COL_SIZE];
    private static final boolean[][] LAND_MINES = new boolean[BOARD_ROW_SIZE][BOARD_COL_SIZE];
    private static int gameStatus = 0; // 0: 게임 중, 1: 승리, -1: 패배

    public static void main(String[] args) {
        showGameStartComments();
        initializeGame();

        while (true) {
            showBoard();

            if (doesUserWinTheGame()) {
                System.out.println("지뢰를 모두 찾았습니다. GAME CLEAR!");
                break;
            }
            if (doesUserLoseTheGame()) {
                System.out.println("지뢰를 밟았습니다. GAME OVER!");
                break;
            }

            String cellInput = getCellInputFromUser(SCANNER);
            String userActionInput = getUserActionInputFromUser(SCANNER);
            actOnCell(cellInput, userActionInput);
        }
    }

    private static void actOnCell(String cellInput, String userActionInput) {
        int selectedColIndex = getSelectedColIndex(cellInput);
        int selectedRowIndex = getSelectedRowIndex(cellInput);

        if (doesUserChooseToPlantFlag(userActionInput)) {
            BOARD[selectedRowIndex][selectedColIndex] = "⚑";
            checkIfGameIsOver();
            return;
        }

        if (doesUserChooseToOpenFlag(userActionInput)) {
            if (isLandMineCell(selectedRowIndex, selectedColIndex)) {
                BOARD[selectedRowIndex][selectedColIndex] = "☼";
                changeGameStatusToLose();
                return;
            }

            open(selectedRowIndex, selectedColIndex);
            boolean isAllOpened = true;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 10; j++) {
                    if (BOARD[i][j].equals(CLOSE_CELL_SIGN)) {
                        isAllOpened = false;
                    }
                }
            }

            if (isAllOpened) {
                changeGameStatusToWin();
            }
            return;
        }
        System.out.println("잘못된 번호를 선택하셨습니다.");
    }

    private static void changeGameStatusToLose() {
        gameStatus = -1;
    }

    private static void changeGameStatusToWin() {
        gameStatus = 1;
    }

    private static boolean isLandMineCell(int selectedRowIndex, int selectedColIndex) {
        return LAND_MINES[selectedRowIndex][selectedColIndex];
    }

    private static boolean doesUserChooseToPlantFlag(String userActionInput) {
        return userActionInput.equals("2");
    }
    private static boolean doesUserChooseToOpenFlag(String userActionInput) {
        return userActionInput.equals("1");
    }

    private static int getSelectedRowIndex(String cellInput) {
        char cellInputRow = cellInput.charAt(1);
        return convertRowFrom(cellInputRow);
    }
    private static int getSelectedColIndex(String cellInput) {
        char cellInputCol = cellInput.charAt(0);
        return convertColFrom(cellInputCol);
    }

    private static String getUserActionInputFromUser(Scanner scanner) {
        System.out.println("선택한 셀에 대한 행위를 선택하세요. (1: 오픈, 2: 깃발 꽂기)");
        String userActionInput = scanner.nextLine();
        return userActionInput;
    }

    private static String getCellInputFromUser(Scanner scanner) {
        System.out.println("선택할 좌표를 입력하세요. (예: a1)");
        String cellInput = scanner.nextLine();
        return cellInput;
    }

    private static boolean doesUserWinTheGame() {
        return gameStatus == 1;
    }
    private static boolean doesUserLoseTheGame() {
        return gameStatus == -1;
    }

    private static void checkIfGameIsOver() {
        boolean isAllOpened = isAllCellOpened();

        if (isAllOpened) {
            gameStatus = 1;
        }
    }

    private static boolean isAllCellOpened() {
        return Arrays.stream(BOARD)
                .flatMap(Arrays::stream)
                .noneMatch(cell -> cell.equals(CLOSE_CELL_SIGN));
    }

    private static int convertRowFrom(int cellInputRow) {
        return Character.getNumericValue(cellInputRow) - 1;
    }
    private static int convertColFrom(int cellInputCol) {
        int selectedColIndex;
        switch (cellInputCol) {
            case 'a':
                selectedColIndex = 0;
                break;
            case 'b':
                selectedColIndex = 1;
                break;
            case 'c':
                selectedColIndex = 2;
                break;
            case 'd':
                selectedColIndex = 3;
                break;
            case 'e':
                selectedColIndex = 4;
                break;
            case 'f':
                selectedColIndex = 5;
                break;
            case 'g':
                selectedColIndex = 6;
                break;
            case 'h':
                selectedColIndex = 7;
                break;
            case 'i':
                selectedColIndex = 8;
                break;
            case 'j':
                selectedColIndex = 9;
                break;
            default:
                selectedColIndex = -1;
                break;
        }
        return  selectedColIndex;
    }

    private static void showBoard() {
        System.out.println("   a b c d e f g h i j");
        for (int row = 0; row < 8; row++) {
            System.out.printf("%d  ", row + 1);
            for (int col = 0; col < 10; col++) {
                System.out.print(BOARD[row][col] + " ");
            }
            System.out.println();
        }
    }

    private static void initializeGame() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 10; col++) {
                BOARD[row][col] = CLOSE_CELL_SIGN;
            }
        }

        for (int i = 0; i < 10; i++) {
            int col = new Random().nextInt(10);
            int row = new Random().nextInt(8);
            LAND_MINES[row][col] = true;
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 10; col++) {
                if (LAND_MINES[row][col]) {
                    LAND_MINE_COUNTS[row][col] = 0;
                    continue;
                }
                int count = countNearbyLandMines(row, col);
                LAND_MINE_COUNTS[row][col] = count;
            }
        }
    }

    private static int countNearbyLandMines(int row, int col) {
        int count = 0;
        if (row - 1 >= 0 && col - 1 >= 0 && LAND_MINES[row - 1][col - 1]) {
            count++;
        }
        if (row - 1 >= 0 && LAND_MINES[row - 1][col]) {
            count++;
        }
        if (row - 1 >= 0 && col + 1 < 10 && LAND_MINES[row - 1][col + 1]) {
            count++;
        }
        if (col - 1 >= 0 && LAND_MINES[row][col - 1]) {
            count++;
        }
        if (col + 1 < 10 && LAND_MINES[row][col + 1]) {
            count++;
        }
        if (row + 1 < 8 && col - 1 >= 0 && LAND_MINES[row + 1][col - 1]) {
            count++;
        }
        if (row + 1 < 8 && LAND_MINES[row + 1][col]) {
            count++;
        }
        if (row + 1 < 8 && col + 1 < 10 && LAND_MINES[row + 1][col + 1]) {
            count++;
        }
        return count;
    }

    private static void showGameStartComments() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("지뢰찾기 게임 시작!");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    private static void open(int row, int col) {
        if (row < 0 || row >= 8 || col < 0 || col >= 10) {
            return;
        }
        if (!BOARD[row][col].equals(CLOSE_CELL_SIGN)) {
            return;
        }
        if (LAND_MINES[row][col]) {
            return;
        }
        if (LAND_MINE_COUNTS[row][col] != 0) {
            BOARD[row][col] = String.valueOf(LAND_MINE_COUNTS[row][col]);
            return;
        } else {
            BOARD[row][col] = "■";
        }
        open(row - 1, col - 1);
        open(row - 1, col);
        open(row - 1, col + 1);
        open(row, col - 1);
        open(row, col + 1);
        open(row + 1, col - 1);
        open(row + 1, col);
        open(row + 1, col + 1);
    }

}
