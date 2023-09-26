package com.github.raspopov.domain;

import java.util.List;

public record Result(boolean success, List<CellInfo> cellInfoList) {
}
