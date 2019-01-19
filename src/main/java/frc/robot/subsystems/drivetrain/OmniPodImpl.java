package frc.robot.subsystems.drivetrain;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import frc.robot.subsystems.TalonPIDSource;

// TODO implement PID control over the contained Talon with its attached encoder
public class OmniPodImpl implements OmniPod {

    private final WPI_TalonSRX talon;

    private final TalonPIDSource talonPIDSource;
    private final PIDController talonPIDController;

    public OmniPodImpl(int talonCANPort) {
        this(talonCANPort, 0.001, 0.001, 0.001);
    }

    public OmniPodImpl(int talonCANPort, double Kp, double Ki, double Kd) {
        this.talon = new WPI_TalonSRX(talonCANPort);
        this.talonPIDSource = new TalonPIDSource(talon);
        this.talonPIDController = new PIDController(Kp, Ki, Kd, talonPIDSource, this.talon);
    }

    @Override
    public void set(double speed) {
        this.talon.set(speed);
    }

    @Override
    public double get() {
        return this.talon.get();
    }

    @Override
    public void disable() {
        this.talon.disable();
    }

    @Override
    public void stopMotor() {
        this.talon.stopMotor();
    }

    @Override
    public void setInverted(boolean isInverted) {
        this.talon.setInverted(isInverted);
    }

    @Override
    public boolean getInverted() {
        return this.talon.getInverted();
    }

    @Override
    public void pidWrite(double output) {
        this.talon.pidWrite(output);
    }

}