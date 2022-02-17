package model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
public class Vector {
    private final Double x, y, z;

    public Vector(Vector other) {
        this.x = other.getX();
        this.y = other.getY();
        this.z = other.getZ();
    }

    public Vector plus(Vector other) {
        return new Vector(this.getX() + other.getX(),
                this.getY() + other.getY(),
                this.getZ() + other.getZ());
    }

    public Vector minus(Vector other) {
        return new Vector(this.getX() - other.getX(),
                this.getY() - other.getY(),
                this.getZ() - other.getZ());
    }

    public Vector times(double scalar) {
        return new Vector(this.getX() * scalar, this.getY() * scalar, this.getZ() * scalar);
    }

    public double mod() {
        return Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2) + Math.pow(this.getZ(), 2));
    }

    public Vector inverse() {
        return new Vector(this.x * (-1.00), this.y * (-1.00), this.z * (-1.00));
    }
}
