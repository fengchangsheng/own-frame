package com.fcs.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Lucare.Feng on 2016/3/23.
 */
public class LogTest {

    private static Logger logger = LoggerFactory.getLogger(LogTest.class);

    {
        logger.info("初始化");
    }

    public static void main(String[] args) {
        try {
            int a = 1;
            int b = 0;
            System.out.println(a/b);
        } catch (Exception e1) {
            e1.printStackTrace();
            logger.error("0不能作除数");
        }
    }

}
