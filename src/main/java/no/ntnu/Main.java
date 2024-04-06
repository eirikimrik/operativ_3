package no.ntnu;

import no.ntnu.server.ServerLogic;

public class Main {
    public static void main(String[] args) {
        ServerLogic logic = new ServerLogic();
        System.out.println(logic.getResult("S5 4, M7 6, D9 8").toString());
    }
}