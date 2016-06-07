package com.maitaidan.controller;


import org.junit.Test;

/**
 * Created by Crytis on 2016/5/10.
 * Just test.
 */
public class MainControllerTest {
    @Test
    public void createConnection() throws Exception {
        MainController mainController = new MainController();
        mainController.getNodes("/");
    }

}