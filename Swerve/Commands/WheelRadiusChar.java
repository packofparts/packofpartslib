package POPLib.Swerve.Commands;

import POPLib.Swerve.SwerveConstants.SDSModules;
import POPLib.Swerve.SwerveTemplates.BaseSwerve;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

public class WheelRadiusChar extends Command {
    private double MAX_TURN_SPEED = 0.1;    
    private double MAX_TURN_ACCEL = 0.4;

    private final BaseSwerve swerve;
    private final double driveBaseRadius;
    private final SlewRateLimiter accelLimit;

    private double currentRadius;
    private double lastAngle;
    private double accumulatedAngle;
    private double[] startWheelPose;

    public WheelRadiusChar(BaseSwerve swerve, SDSModules moduleInfo, double driveBaseRadius) {
        this.swerve = swerve;
        this.driveBaseRadius = driveBaseRadius;
        this.MAX_TURN_SPEED *= moduleInfo.maxAngularVelocity;
        this.MAX_TURN_ACCEL *= moduleInfo.maxAngularAcceleration;
        this.accelLimit = new SlewRateLimiter(MAX_TURN_ACCEL);

        addRequirements(swerve);
    }

    @Override
    public void initialize() {
        lastAngle = swerve.getGyro().getNormalizedRotation2dAngle().getRadians();
        accumulatedAngle = 0.0;
        startWheelPose = swerve.getPoseRotationsRadians();
        currentRadius = 0.0;
    }

    @Override
    public void execute() {
        swerve.driveRobotOriented(new Translation2d(0,0), accelLimit.calculate(MAX_TURN_SPEED));

        accumulatedAngle += MathUtil.angleModulus(swerve.getGyro().getNormalizedRotation2dAngle().getRadians() - lastAngle);
        lastAngle = swerve.getGyro().getNormalizedRotation2dAngle().getRadians();

        double averageWheelPos = 0.0;
        double[] wheelPositiions = swerve.getPoseRotationsRadians();

        for (int i=0; i < 4; ++i) {
            averageWheelPos += Math.abs(wheelPositiions[i]- startWheelPose[i]);
        }

        averageWheelPos /= 4.0;

        currentRadius = (accumulatedAngle * driveBaseRadius) / averageWheelPos;

        SmartDashboard.putNumber("Current Diamater", 2.0 * Units.metersToInches(currentRadius));
    }

    @Override
    public void end(boolean interrupted) {
        swerve.driveRobotOriented(new Translation2d(0,0), 0.0);

        if (accumulatedAngle <= 2*Math.PI) {
            System.out.println("Not enough data to compute final result");
        } else {
            System.out.println("Final Diamater in inches " + Units.metersToInches(currentRadius) * 2.0); 
        }
    }

}
