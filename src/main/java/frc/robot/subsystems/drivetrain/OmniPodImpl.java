package frc.robot.subsystems.drivetrain;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

// TODO implement PID control over the contained Talon with its attached encoder
public class OmniPodImpl implements OmniPod {

    private final WPI_TalonSRX talon;

    public OmniPodImpl(int talonCANPort) {
        this.talon = new WPI_TalonSRX(talonCANPort);
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