package wordbox_7;

import javax.swing.*;

/**
 * Это главный класс программы. С него начинается выполнение
 * @author Сергей Лебидко
 */
public class MainClass {

    /**
     * @param args параметры командной строки
     */
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Pad());
        
    }
    
}
