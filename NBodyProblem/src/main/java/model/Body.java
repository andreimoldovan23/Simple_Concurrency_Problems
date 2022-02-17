package model;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"readWriteLock"})
@EqualsAndHashCode(exclude = {"mass", "readWriteLock"})
public class Body {
    @Getter @Setter private Double mass;
    private Vector position, velocity, acceleration;

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public Vector getPosition() {
        ReadLock lock = readWriteLock.readLock();
        lock.lock();
        Vector temp = new Vector(this.position);
        lock.unlock();
        return temp;
    }

    public Vector getVelocity() {
        ReadLock lock = readWriteLock.readLock();
        lock.lock();
        Vector temp = new Vector(this.velocity);
        lock.unlock();
        return temp;
    }

    public Vector getAcceleration() {
        ReadLock lock = readWriteLock.readLock();
        lock.lock();
        Vector temp = new Vector(this.acceleration);
        lock.unlock();
        return temp;
    }

    public void setPosition(Vector newPos) {
        WriteLock lock = readWriteLock.writeLock();
        lock.lock();
        this.position = newPos;
        lock.unlock();
    }

    public void setVelocity(Vector newVelocity) {
        WriteLock lock = readWriteLock.writeLock();
        lock.lock();
        this.velocity = newVelocity;
        lock.unlock();
    }

    public void setAcceleration(Vector newAcc) {
        WriteLock lock = readWriteLock.writeLock();
        lock.lock();
        this.acceleration = newAcc;
        lock.unlock();
    }

}
