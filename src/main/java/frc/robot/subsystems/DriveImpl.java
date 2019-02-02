package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.SPI;
import com.kauailabs.navx.frc.AHRS;
import frc.robot.RobotMap;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveImpl implements Drive {
    private final MecanumDrive drivetrain;
    private final WPI_TalonSRX frontLeftDrive;
    private final WPI_TalonSRX frontRightDrive;
    private final WPI_TalonSRX backLeftDrive;
    private final WPI_TalonSRX backRightDrive;

    //For gyro
    private AHRS ahrs;
    // For ultrasonic sensor

    public DriveImpl() {
        this.frontLeftDrive = new WPI_TalonSRX(RobotMap.DRIVETRAIN_LEFT_FRONT_TALON);
        frontLeftDrive.setInverted(true); 
        this.frontRightDrive = new WPI_TalonSRX(RobotMap.DRIVETRAIN_LEFT_BACK_TALON);
        frontRightDrive.setInverted(true);
        this.backLeftDrive = new WPI_TalonSRX(RobotMap.DRIVETRAIN_RIGHT_FRONT_TALON);
        backLeftDrive.setInverted(true);
        this.backRightDrive = new WPI_TalonSRX(RobotMap.DRIVETRAIN_RIGHT_BACK_TALON);
        backRightDrive.setInverted(true);

        this.drivetrain = new MecanumDrive(frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive);

        this.ahrs = new AHRS(SPI.Port.kMXP);
        this.ahrs.reset();
    }

    @Override
    public void drive(double forwardRate, double strafeRate, double turnRate) {
        double angle = -this.ahrs.getYaw();
        SmartDashboard.putNumber("Gyro:", angle);
        
        this.drivetrain.driveCartesian(strafeRate, forwardRate, turnRate);
    }

    @Override
    public void setCenterWheelsDeployed(boolean deployed) {
        // TODO
    }

    @Override
    public boolean getCenterWheelsDeployed() {
        return false;
    }

    @Override
    public void resetGyro() {
        this.ahrs.reset();
    }
}

