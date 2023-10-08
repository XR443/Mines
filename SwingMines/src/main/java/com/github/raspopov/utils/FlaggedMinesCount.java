package com.github.raspopov.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Component
public class FlaggedMinesCount {

    private final AtomicInteger flaggedMinesCount = new AtomicInteger(0);
    private int minesCount;

    private final ApplicationEventPublisher applicationEventPublisher;

    public void increment() {
        flaggedMinesCount.incrementAndGet();
        publishEvent();
    }

    public void decrement() {
        flaggedMinesCount.decrementAndGet();
        publishEvent();
    }

    public void clear() {
        flaggedMinesCount.set(0);
        minesCount = 0;
        publishEvent();
    }

    private void publishEvent() {
        applicationEventPublisher.publishEvent(new MinesCountEvent(flaggedMinesCount.get(), minesCount));
    }

    public void setMinesCount(int minesCount) {
        this.minesCount = minesCount;
        publishEvent();
    }
}
