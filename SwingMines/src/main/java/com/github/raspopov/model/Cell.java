package com.github.raspopov.model;

import java.util.Optional;

public interface Cell {

    int x();

    int y();

    void setFlag(boolean flag);
    boolean isFlagged();

    void open();

    boolean isOpen();

    Optional<CellInfo> getCellInfo();

    void setCellInfo(CellInfo cellInfo);

}
