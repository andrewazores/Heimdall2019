package frc.robot.subsystems.tower;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.collections4.bidimap.UnmodifiableBidiMap;

import ca.team3161.lib.robot.LifecycleEvent;
import ca.team3161.lib.robot.subsystem.RepeatingPooledSubsystem;
import ca.team3161.lib.utils.Utils;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import java.util.concurrent.TimeUnit;

import com.ctre.phoenix.motorcontrol.ControlMode;

import frc.robot.subsystems.tower.Tower.Position;
import frc.robot.subsystems.Gains;

class ArmImpl extends RepeatingPooledSubsystem implements Arm {

    private static final BidiMap<Position, Integer> POSITION_TICKS;
    static {
        final BidiMap<Position, Integer> positionTicks = new DualHashBidiMap<>();
        // TODO placeholder encoder tick values
        positionTicks.put(Position.STARTING_CONFIG, 0);
        positionTicks.put(Position.GROUND, -1);
        positionTicks.put(Position.LEVEL_1, -35 );
        positionTicks.put(Position.LEVEL_2, -153);
        positionTicks.put(Position.LEVEL_3, 6);
        POSITION_TICKS = UnmodifiableBidiMap.unmodifiableBidiMap(positionTicks);
    }

    private final WPI_TalonSRX controller;
    private Position targetPosition = Position.STARTING_CONFIG;

    ArmImpl(int talonPort) {
        super(50, TimeUnit.MILLISECONDS);
        this.controller = Utils.safeInit("arm controller", () -> new WPI_TalonSRX(talonPort));

        //Arm PID
        final int kPIDLoopIdx;
        final Gains kGains;
        final int kTimeoutMs;
        boolean kSensorPhase;
        boolean kMotorInvert;
        int absolutePosition;

        kPIDLoopIdx = 0;
        //touch kp with care dont increase greatly rather not move at all then go ape shit
        kGains = new Gains(0.16, 0.0, 0.0, 0.0, 0, 1); //TODO Placeholder values
        kTimeoutMs = 30;
        // absolutePosition = controller.getSensorCollection().getPulseWidthPosition();
        kMotorInvert = false;
        // kSensorPhase = true;

        //Set PID values on Talon
        controller.config_kF(kPIDLoopIdx, kGains.kF);
        controller.config_kP(kPIDLoopIdx, kGains.kP);
        controller.config_kI(kPIDLoopIdx, kGains.kI);
        controller.config_kD(kPIDLoopIdx, kGains.kD);

        // absolutePosition &= 0xFFF;
        // if (kSensorPhase) {absolutePosition *= -1;}
        // if (kMotorInvert) {absolutePosition *= -1;}

        controller.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, kPIDLoopIdx, kTimeoutMs);
        // controller.setSelectedSensorPosition(absolutePosition, kPIDLoopIdx, kTimeoutMs);
        //controller.setSelectedSensorPosition(0, 0, 0);
        controller.setSensorPhase(true);
        controller.configAllowableClosedloopError(kPIDLoopIdx, 5);

        //Speed Limiting
        controller.configPeakOutputForward(kGains.kPeakOutput);
        controller.configPeakOutputReverse(-kGains.kPeakOutput);

        //controller.configAllowableClosedloopError(kPIDLoopIdx, allowableCloseLoopError, kTimeoutMs);
    }

    @Override
    public void setPosition(Position position) {
        this.targetPosition = position;
        int encoderTicks;
        if (!POSITION_TICKS.containsKey(position)) {
            encoderTicks = POSITION_TICKS.getOrDefault(Position.STARTING_CONFIG, 0);
        } else {
            encoderTicks = POSITION_TICKS.get(position);
        }
        SmartDashboard.putNumber("encoder tick target arm",encoderTicks);
        this.controller.setIntegralAccumulator(0);
        this.controller.set(ControlMode.Position, encoderTicks);
        SmartDashboard.putString("arm Position", position.toString());
    }

    @Override
    public Position getPosition() {
        return this.targetPosition;
    }

    @Override
    public void setSpeed(double speed) {
        this.controller.set(ControlMode.PercentOutput, speed);
    }

    @Override
    public void reset() {
        this.controller.setIntegralAccumulator(0);
    }

    @Override
    public void defineResources() { }

    @Override
    public void lifecycleStatusChanged(LifecycleEvent previous, LifecycleEvent current) {
        if (current.equals(LifecycleEvent.ON_INIT)) {
            start();
        }

        if (previous.equals(LifecycleEvent.ON_AUTO) && current.equals(LifecycleEvent.ON_TELEOP)) {
            return;
        }
        reset();
    }

    @Override
    public void task() {
        SmartDashboard.putNumber("arm encoder ticks", controller.getSelectedSensorPosition());
    }

    @Override
    public double returnEncoderTicks() {
        return controller.getSelectedSensorPosition();
    }
}
