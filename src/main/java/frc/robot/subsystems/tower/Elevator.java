package frc.robot.subsystems.tower;

import ca.team3161.lib.robot.LifecycleListener;
import frc.robot.subsystems.tower.Tower.Position;

interface Elevator extends LifecycleListener {
    void setPosition(Position position);
    Position getPosition();
    void setSpeed(double speed);
    double returnEncoderTicks();
    void reset();
}