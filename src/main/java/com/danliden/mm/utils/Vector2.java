package com.danliden.mm.utils;

public class Vector2 {

    public float x, y;

    public Vector2() {
        x = 0;
        y = 0;
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 vec) {
        this.x = vec.x;
        this.y = vec.y;
    }

    public void set(Vector2 vec) {
        this.x = vec.x;
        this.y = vec.y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void add(Vector2 vec) {
        this.add(vec.x, vec.y);
    }

    public void mul(Vector2 vec) {
        this.mul(vec.x, vec.y);
    }

    public void sub(Vector2 vec) {
        this.sub(vec.x, vec.y);
    }

    public void div(Vector2 vec) {
        this.div(vec.x, vec.y);
    }

    public void add(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public void mul(float x, float y) {
        this.x *= x;
        this.y *= y;
    }

    public void sub(float x, float y) {
        this.x -= x;
        this.y -= y;
    }

    public void div(float x, float y) {
        this.x /= x;
        this.y /= y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public boolean equalsTo(Vector2 vec) {
        return this.x == vec.x && this.y == vec.y;
    }
}
