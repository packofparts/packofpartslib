package POPLib.Swerve.SwerveConstants;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

import POPLib.Motor.MotorConfig;
import POPLib.Motor.MotorHelper;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;

/**
 * Wrapper class for swerve module constants.
 */
public class SwerveModuleConstants {
    public final int driveMotorId;
    public final int angleMotorId;
    public final int cancoderId;
    public final int moduleNumber;

    public final Rotation2d angleOffset;

    public final static NeutralModeValue angleNeutralMode = NeutralModeValue.Coast;
    public final static IdleMode angleIdleMode = IdleMode.kCoast;
    public static NeutralModeValue driveNeutralMode = NeutralModeValue.Brake;
    public static IdleMode driveIdleMode = IdleMode.kBrake;

    public final static double wheelCircumference = Units.inchesToMeters(4) * Math.PI;

    public final double driveRotationsToMeters;

    public final boolean swerveTuningMode;

    public final MotorConfig driveConfig;
    public final MotorConfig angleConfig;

    public final SDSModules moduleInfo;

    /**
     * Swerve Module Constants to be used when creating swerve modules.
     */
    public SwerveModuleConstants(int moduleNumber, int driveMotorId, int cancoderId, int angleMotorId, Rotation2d angleOffset, SDSModules moduleInfo,
            boolean swerveTuningMode, MotorConfig driveConfig, MotorConfig angleConfig) {
        this.moduleNumber = moduleNumber;
        this.angleOffset = angleOffset;

        this.driveMotorId = driveMotorId;
        this.cancoderId = cancoderId;
        this.angleMotorId = angleMotorId;

        this.moduleInfo = moduleInfo;

        driveRotationsToMeters = wheelCircumference * moduleInfo.driveGearRatio;

        this.swerveTuningMode = swerveTuningMode;

        this.driveConfig = driveConfig;
        this.angleConfig = angleConfig;
    }

    public SwerveModuleConstants(int moduleNumber, Rotation2d angleOffset, SDSModules moduleInfo,
        boolean swerveTuningMode, MotorConfig driveConfig, MotorConfig angleConfig) {
            this(moduleNumber,  (moduleNumber * 3) + 1, (moduleNumber * 3) + 2, (moduleNumber * 3) + 3, angleOffset, moduleInfo, swerveTuningMode, driveConfig, angleConfig);
    }

    public static SwerveModuleConstants[] generateConstants(Rotation2d[] angleOffsets, SDSModules moduleInfo,
            boolean swerveTuningMode, MotorConfig driveConfig, MotorConfig angleConfig) {
        SwerveModuleConstants[] ret = new SwerveModuleConstants[4];

        for (int i = 0; i < 4; ++i) {
            ret[i] = new SwerveModuleConstants(
                i,
                angleOffsets[i],
                moduleInfo,
                swerveTuningMode,
                driveConfig,
                angleConfig
            );
        }

        return ret;
    }

    public static SwerveModuleConstants[] generateConstants(Rotation2d[] angleOffsets,Boolean[] flippedModules, Integer[] canIds, SDSModules moduleInfo,
            boolean swerveTuningMode, MotorConfig driveConfig, MotorConfig angleConfig) {
        SwerveModuleConstants[] ret = new SwerveModuleConstants[4];

        MotorConfig invertedDrive = driveConfig.getInvertedConfig();
        MotorConfig invertedAngle = angleConfig.getInvertedConfig();
        for (int i = 0; i < 4; ++i) {
            ret[i] = new SwerveModuleConstants(
                i,
                canIds[i*3],
                canIds[i*3+1],
                canIds[i*3+2],
                angleOffsets[i],
                moduleInfo,
                swerveTuningMode,
                flippedModules[i] ? invertedDrive : driveConfig,
                flippedModules[i] ? invertedAngle : angleConfig
            );
        }

        return ret;
    }

    public static SwerveModuleConstants[] generateConstants(Rotation2d[] angleOffsets, Integer[] canIds, SDSModules moduleInfo,
    boolean swerveTuningMode, MotorConfig driveConfig, MotorConfig angleConfig) {
        SwerveModuleConstants[] ret = new SwerveModuleConstants[4];

        for (int i = 0; i < 4; ++i) {
            ret[i] = new SwerveModuleConstants(
                i,
                canIds[i*3],
                canIds[i*3+1],
                canIds[i*3+2],
                angleOffsets[i],
                moduleInfo,
                swerveTuningMode,
                driveConfig,
                angleConfig
            );
        }

        return ret;
        }

    public CANSparkMax getDriveNeo() {
        CANSparkMax neo = new CANSparkMax(driveMotorId, MotorType.kBrushless);
        driveConfig.setCanSparkMaxConfig(neo, MotorType.kBrushless);
        MotorHelper.setConversionFactor(neo, driveRotationsToMeters);
        return neo;
    }

    public TalonFX getDriveFalcon() {
        TalonFX drive = new TalonFX(driveMotorId, Constants.Ports.CANIVORE_NAME);
        TalonFXConfiguration config = driveConfig.getTalonConfig();
        config.Feedback.SensorToMechanismRatio = moduleInfo.driveGearRatio;
        drive.getConfigurator().apply(config);
        return drive;
    }

    public CANSparkMax getAngleNeo() {
        CANSparkMax neo = new CANSparkMax(angleMotorId, MotorType.kBrushless);
        angleConfig.setCanSparkMaxConfig(neo, MotorType.kBrushless);
        MotorHelper.setDegreeConversionFactor(neo, moduleInfo.angleGearRatio);

        neo.getPIDController().setPositionPIDWrappingEnabled(true);
        neo.getPIDController().setPositionPIDWrappingMinInput(0);
        neo.getPIDController().setPositionPIDWrappingMinInput(360);

        return neo;
    }

    public TalonFX getAngleFalcon() {
        TalonFX angle = new TalonFX(angleMotorId, Constants.Ports.CANIVORE_NAME);
        TalonFXConfiguration config = angleConfig.getTalonConfig();
        MotorHelper.setDegreeConversionFactor(config, moduleInfo.angleGearRatio);
        angle.getConfigurator().apply(config);
        return angle;
    }

    public CANcoder getCanCoder() {
        CANcoder angleEncoder = new CANcoder(cancoderId, Constants.Ports.CANIVORE_NAME);

        CANcoderConfiguration config = new CANcoderConfiguration();
        config.MagnetSensor.AbsoluteSensorRange = AbsoluteSensorRangeValue.Unsigned_0To1;
        config.MagnetSensor.SensorDirection = SensorDirectionValue.CounterClockwise_Positive;
        config.MagnetSensor.MagnetOffset = -angleOffset.getRotations();

        angleEncoder.getConfigurator().apply(config);

        return angleEncoder;
    }
}
