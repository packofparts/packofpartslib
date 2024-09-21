package POPLib.Subsytems.Flywheel;

import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.VelocityDutyCycle;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import POPLib.Control.PIDConfig;
import POPLib.Math.MathUtil;
import POPLib.Motor.MotorConfig;
import POPLib.SmartDashboard.PIDTuning;
import POPLib.SmartDashboard.TunableNumber;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

public class TalonFlywheel extends Flywheel {
    public TalonFX leadMotor; 
    public TalonFX followerMotor; 
 
    public PIDTuning leadPidTuning;

    VelocityDutyCycle velocity;
    VoltageOut voltage;
    boolean resetShooter;
    
 
    protected TalonFlywheel(MotorConfig leadConfig, MotorConfig followerConfig, String subsytemName, boolean tuningMode, boolean motorsInverted) {
        super(subsytemName, tuningMode);

        this.leadMotor = leadConfig.createTalon();
        this.followerMotor = followerConfig.createTalon();

        this.leadPidTuning = new PIDTuning(subsytemName + " flywheel", PIDConfig.getZeroPid(), tuningMode);

        this.velocity = new VelocityDutyCycle(0.0);

        this.voltage = new VoltageOut(0.0);
        resetShooter = false;

        followerMotor.setControl(new Follower(leadConfig.canId, motorsInverted));
    } 

    public double getError(double setpoint) {
        return Math.abs(leadMotor.getVelocity().getValueAsDouble() - setpoint);
    }

    public void log() {
        SmartDashboard.putNumber(getName() + " velocity ", leadMotor.getVelocity().getValueAsDouble());
    }

    public double getVelocity() {
        return leadMotor.getVelocity().getValueAsDouble();
    }

    public Command toggaleVoltage() {
        return runOnce(() -> resetShooter = !resetShooter);
    }

    @Override
    public void periodic() {
        leadPidTuning.updatePID(leadMotor);

        SmartDashboard.putBoolean("Voltage Toggle", resetShooter);

        
        if (!resetShooter) {
            if (setpoint.hasChanged()) {
                leadMotor.setControl(velocity.withVelocity(setpoint.get()));
            }
        } else {
            leadMotor.setControl(voltage);
        }
    } 
}
