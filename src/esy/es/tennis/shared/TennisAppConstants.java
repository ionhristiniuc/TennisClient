package esy.es.tennis.shared;

public interface TennisAppConstants
{
    int PORT_NUMBER = 11223;
    int UDP_PORT_NUMBER = 11224;
    String separator = "|";
    String notification = "NOTIF";
    String connect = "CONNECT";
    String disconnect = "DISCONNECT";
    String invalidNick = "INV_NICK";
    String updateBoard = "UPDATE_BOARD";
    String movePaletteLeft = "MOVE_P_LEFT";
    String movePaletteRight = "MOVE_P_RIGHT";
    String ballMove = "BALL";
    int boardWidth = 550;
    int boardHeight = 600;
    int paletteWidth = 70;
    int paletteHeight = 10;
    int ballDiameter = 10;
    int moveSpeed = 7;
    int ballSpeed = 1;
}