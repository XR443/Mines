package com.github.raspopov.model;

import java.util.List;

public record Result(boolean success, List<Cell> cells) {
}
