package com.github.raspopov.frames;

import com.github.raspopov.ui.ButtonsPanel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class MinesFrame extends JFrame {

    public MinesFrame(@Qualifier("minesPanel") JPanel minesPanel,
                      ButtonsPanel buttonsPanel) throws HeadlessException {
        super("Mines");

        JPanel mainPane = new JPanel(new BorderLayout());

        mainPane.add(buttonsPanel, BorderLayout.NORTH);
//        mainPane.add(minesPanel, BorderLayout.CENTER);
        mainPane.add(new JScrollPane(minesPanel), BorderLayout.CENTER);

        add(mainPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 480);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
//        setUndecorated(true);
        setVisible(true);
    }
}
