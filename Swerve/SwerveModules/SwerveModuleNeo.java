package POPLib.Swerve.SwerveModules;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import POPLib.SmartDashboard.PIDTuning;
import POPLib.Swerve.SwerveConstants.SwerveModuleConstants;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveModuleNeo extends SwerveModule {
    private final SparkMax driveMotor;
    private final RelativeEncoder driveEncoder;
    private final SparkClosedLoopController drivePID;

    private final SparkMax angleMotor;
    private final RelativeEncoder angleEncoder;
    private final SparkClosedLoopController anglePID;


    public SwerveModuleNeo(SwerveModuleConstants constants) {
        super(constants);

        driveMotor = constants.getDriveNeo();
        driveEncoder = driveMotor.getEncoder();
        drivePID = driveMotor.getClosedLoopController();

        angleMotor = constants.getAngleNeo();
        angleEncoder = angleMotor.getEncoder();
        anglePID = angleMotor.getClosedLoopController();
    }

    @Override
    protected void applySwerveModuleState(double velocityMPS, Rotation2d angleRadians) {
        anglePID.setReference(angleRadians.getRadians(), SparkMax.ControlType.kPosition);
        drivePID.setReference(velocityMPS, SparkMax.ControlType.kVelocity);
        SmartDashboard.putNumber("Current Encoder Angle: " + swerveModuleConstants.moduleNumber, Rotation2d.fromRadians(angleEncoder.getPosition()).getDegrees());
    }

    @Override
    public void resetToAbsolute() {
        angleEncoder.setPosition(getCanCoder().getDegrees()); 
    }

    @Override
    protected Rotation2d getEncoderAngle() {
        return Rotation2d.fromRadians(angleEncoder.getPosition());
    }

    @Override
    protected double getPositionMeter() {
        return driveEncoder.getPosition();
    }

    @Override
    protected double getVelocityMeter() {
        return driveEncoder.getVelocity();
    }

    @Override
    public void updatePID(PIDTuning angle, PIDTuning drive) {
        angle.updatePID(angleMotor);
        drive.updatePID(driveMotor);
    }
}
