package frc.robot.subsystems.tower;

import java.util.concurrent.TimeUnit;

import ca.team3161.lib.robot.LifecycleEvent;
import ca.team3161.lib.robot.LifecycleListener;
import ca.team3161.lib.robot.sensors.RightSight;
import ca.team3161.lib.robot.subsystem.RepeatingPooledSubsystem;
import edu.wpi.first.wpilibj.Relay;
import frc.robot.RobotMap;

class GamePieceWatcher extends RepeatingPooledSubsystem implements LifecycleListener {

    private final RightSight hatchSensor;
    private final RightSight ballSensor;
    private final Relay relay;

    GamePieceWatcher() {
        super(50, TimeUnit.MILLISECONDS);
        this.relay = new Relay(RobotMap.LED_SPIKE, Relay.Direction.kForward);
        this.hatchSensor = new RightSight(RobotMap.HATCH_SENSOR);
        this.hatchSensor.setInverted(true);
        this.ballSensor = new RightSight(RobotMap.BALL_SENSOR);
        this.ballSensor.setInverted(true);
    }

    @Override
    public void defineResources() { }

    @Override
    public void task() {
        if (this.hatchSensor.get() || this.ballSensor.get()) {
            this.relay.set(Relay.Value.kOn);
        } else {
            this.relay.set(Relay.Value.kOff);
        }
    }

    @Override
    public void lifecycleStatusChanged(LifecycleEvent previous, LifecycleEvent current) {
        switch (current) {
            case ON_TEST:
            case ON_AUTO:
            case ON_TELEOP:
                this.start();
                break;
            default:
                this.cancel();
        }
    }
}