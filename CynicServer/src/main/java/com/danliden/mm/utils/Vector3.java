package com.danliden.mm.utils;

public class Vector3 {

    public float x, y, z;

    public Vector3() {
        x = 0;
        y = 0;
        z = 0;
    }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public void set(Vector3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void add(Vector3 vec) {
        this.add(vec.x, vec.y, vec.z);
    }

    public void mul(Vector3 vec) {
        this.mul(vec.x, vec.y, vec.z);
    }

    public void sub(Vector3 vec) {
        this.sub(vec.x, vec.y, vec.z);
    }

    public void div(Vector3 vec) {
        this.div(vec.x, vec.y, vec.z);
    }

    public void add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void mul(float x, float y, float z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
    }

    public void sub(float x, float y, float z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
    }

    public void div(float x, float y, float z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public boolean equalsTo(Vector3 vec) {
        return this.x == vec.x && this.y == vec.y && this.z == vec.z;
    }
}
