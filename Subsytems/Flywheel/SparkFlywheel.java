package POPLib.Subsytems.Flywheel;

import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkMax;
import POPLib.Math.MathUtil;
import POPLib.Motor.FollowerConfig;
import POPLib.Motor.MotorConfig;
import POPLib.SmartDashboard.PIDTuning;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SparkFlywheel extends Flywheel {
    SparkMax leadMotor; 
    SparkMax followerMotor; 
    PIDTuning leadPidTuning;
 
    protected SparkFlywheel(MotorConfig leadConfig, FollowerConfig followerConfig, String subsytemName, boolean tuningMode) {
        super(subsytemName, tuningMode);

        this.leadMotor = leadConfig.createSparkMax();
        this.followerMotor = followerConfig.createSparkMax(leadMotor);
    } 

    public double getError(double setpoint) {
        return MathUtil.getError(leadMotor, setpoint);
    }

    public void log() {
        SmartDashboard.putNumber(getName() + " velocity ", leadMotor.getEncoder().getVelocity());
    }

    @Override
    public void periodic() {
        leadPidTuning.updatePID(leadMotor);
        leadMotor.getClosedLoopController().setReference(setpoint.get(), ControlType.kVelocity);
    }
}
