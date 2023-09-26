package com.github.raspopov.frames;

import com.github.raspopov.ui.ButtonsPanel;
import com.github.raspopov.ui.MinesPanel;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class MinesFrame extends JFrame {

    public MinesFrame(MinesPanel minesPanel, ButtonsPanel buttonsPanel) throws HeadlessException {
        super("Mines");

        JPanel mainPane = new JPanel(new BorderLayout());

        mainPane.add(buttonsPanel, BorderLayout.NORTH);
        mainPane.add(minesPanel, BorderLayout.CENTER);

        add(mainPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 480);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
//        setUndecorated(true);
        setVisible(true);
    }
}
