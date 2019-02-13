package frc.robot.subsystems.drivetrain;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;

import com.kauailabs.navx.frc.AHRS;

import ca.team3161.lib.robot.motion.drivetrains.SpeedControllerGroup;
import frc.robot.RobotMap;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveImpl implements Drive {
    private final MecanumDrive holoDrive;
    private final DifferentialDrive tankDrive;
    private final OmniPod frontLeftDrive;
    private final OmniPod frontRightDrive;
    private final OmniPod backLeftDrive;
    private final OmniPod backRightDrive;
    private final ColsonPod leftColson;
    private final ColsonPod rightColson;

    private AHRS ahrs;

    PIDController turnController;
    boolean fieldCentric = true;
    double angleTarget;
    volatile double computedTurnPID;
	double rotate;
	double Pg = 0.0075;
	double Ig = 0.00;
	double Dg = 0.0;
	float kToleranceDegrees = 2;
	boolean rotateToAngle;
    double currentRotationRate;

    public DriveImpl() {
        this.frontLeftDrive = new RawOmniPodImpl(RobotMap.DRIVETRAIN_LEFT_FRONT_TALON);
        frontLeftDrive.setInverted(false);
        this.frontRightDrive = new RawOmniPodImpl(RobotMap.DRIVETRAIN_RIGHT_FRONT_TALON);
        frontRightDrive.setInverted(false);
        this.backLeftDrive = new RawOmniPodImpl(RobotMap.DRIVETRAIN_LEFT_BACK_TALON);
        backLeftDrive.setInverted(false);
        this.backRightDrive = new RawOmniPodImpl(RobotMap.DRIVETRAIN_RIGHT_BACK_TALON);
        backRightDrive.setInverted(false);

        Solenoid colsonValve = new Solenoid(RobotMap.COLSON_SOLENOID);
        this.leftColson = new ColsonPodImpl(RobotMap.DRIVETRAIN_LEFT_COLSON, colsonValve);
        this.rightColson = new ColsonPodImpl(RobotMap.DRIVETRAIN_RIGHT_COLSON, colsonValve);

        this.holoDrive = new MecanumDrive(frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive);
        this.tankDrive = new DifferentialDrive(
            new SpeedControllerGroup(frontLeftDrive, leftColson, backLeftDrive),
            new SpeedControllerGroup(frontRightDrive, rightColson, backRightDrive)
        );
        this.tankDrive.setSafetyEnabled(false);
        this.holoDrive.setSafetyEnabled(false);

        this.ahrs = new AHRS(SPI.Port.kMXP);
        this.ahrs.reset();

        this.turnController = new PIDController(Pg, Ig, Dg, new PIDSource() {
            @Override
            public void setPIDSourceType(PIDSourceType pidSource) {
                ahrs.setPIDSourceType(pidSource);
            }
        
            @Override
            public double pidGet() {
                return -1.f * ahrs.getAngle();
            }
        
            @Override
            public PIDSourceType getPIDSourceType() {
                return ahrs.getPIDSourceType();
            }
        }, this::gyroPID);
        turnController.enable();
        
        setAngleTarget(0);
    }

    @Override
    public void drive(double forwardRate, double strafeRate, double turnRate) {
        double angle = this.ahrs.getYaw();
        SmartDashboard.putNumber("Gyro:", angle);

        if (this.getCenterWheelsDeployed()) {
            this.tankDrive.arcadeDrive(forwardRate, turnRate);
        } else {
            this.setAngleTarget(this.angleTarget + turnRate * 180 * TimedRobot.kDefaultPeriod); // 180 degrees per second, divided by update rate
            this.holoDrive.driveCartesian(strafeRate, forwardRate, computedTurnPID, fieldCentric ? angle : 0);
        }
    }

    @Override
    public void setAngleTarget(double angleTarget) {
        this.angleTarget = angleTarget;
        turnController.setSetpoint(angleTarget);
    }

    @Override
    public void setCenterWheelsDeployed(boolean deployed) {
        this.leftColson.setDeployed(deployed);
        this.rightColson.setDeployed(deployed);

        this.frontLeftDrive.setScaledDown(deployed);
        this.frontRightDrive.setScaledDown(deployed);
        this.backLeftDrive.setScaledDown(deployed);
        this.backRightDrive.setScaledDown(deployed);
    }

    @Override
    public boolean getCenterWheelsDeployed() {
        // all pods should be synchronized so just pick one arbitrarily
        return this.leftColson.isDeployed();
    }

    @Override
    public void resetGyro() {
        this.ahrs.reset();
    }

    @Override
    public void setFieldCentric(boolean fieldCentric) {
        this.fieldCentric = fieldCentric;
    }

    //Sets the gyro to make the robot face a cetain angle
	private void gyroPID(double angle) {
        this.computedTurnPID = angle;
        SmartDashboard.putNumber("pid value", angle);
    } 
}

