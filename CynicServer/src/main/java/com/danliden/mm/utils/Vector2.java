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

    public static Vector2 Zero(){
        return new Vector2();
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

    public float distance(Vector2 other){
        return distance(other.x, other.y);
    }

    public float distance(float x, float y){
        double x2 = Math.pow(x - this.x, 2.0);
        double y2 = Math.pow(y - this.y, 2.0);
        return (float)Math.sqrt((x2 + y2));
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
