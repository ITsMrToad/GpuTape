package com.mr_toad.gpu_booster.client.rendering.util;

import java.util.OptionalInt;

public class IntArrayDeque {

    private int[] data;

    private int head;
    private int tail;
    private int size;

    public IntArrayDeque(int capacity) {
        this.data = new int[capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    public void addLast(int value) {
        this.ensureCapacity(this.size + 1);
        this.data[this.tail] = value;
        this.tail = (this.tail + 1) % this.data.length;
        this.size++;
    }

    public void addFirst(int value) {
        this.ensureCapacity(this.size + 1);
        this.head = (this.head - 1 + this.data.length) % this.data.length;
        this.data[this.head] = value;
        this.size++;
    }

    public OptionalInt pool() {
        if (this.size == 0) {
            return OptionalInt.empty();
        }
        int val = this.data[this.head];
        this.head = (this.head + 1) % this.data.length;
        this.size--;
        return OptionalInt.of(val);
    }

    private void ensureCapacity(int minCapacity) {
        if (this.data.length >= minCapacity) return;
        int newCap = Math.max(this.data.length * 2, minCapacity);
        int[] newData = new int[newCap];
        for (int i = 0; i < this.size; i++) {
            newData[i] = this.data[(this.head + i) % this.data.length];
        }
        this.data = newData;
        this.head = 0;
        this.tail = this.size;
    }
}
